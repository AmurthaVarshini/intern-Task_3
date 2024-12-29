package ManagementSystem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class InventoryManagementSystemGUI {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/InventoryDB";
    private static final String DB_USER = "root"; // Replace with your DB username
    private static final String DB_PASSWORD = "Root123"; // Replace with your DB password

    private Connection connection;
    private JTable table;
    private DefaultTableModel tableModel;


    public InventoryManagementSystemGUI() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to the database!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    public static void main(String[] args) {
        InventoryManagementSystemGUI app = new InventoryManagementSystemGUI();
        app.showLoginDialog();
    }
    public void showLoginDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] fields = {
            "Username:", usernameField,
            "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(null, fields, "Login", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (authenticateUser(username, password)) {
                JOptionPane.showMessageDialog(null, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                createGUI();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
                showLoginDialog(); // Retry login
            }
        } else {
            System.exit(0); // Exit if login is canceled
        }
    }
    private boolean authenticateUser(String username, String password) {
        try {
            String query = "SELECT * FROM User WHERE username = ? AND password = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Returns true if a match is found
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error during authentication!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    public void createGUI() {
        JFrame frame = new JFrame("Inventory Management System");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        String[] columnNames = {"Product ID", "Name", "Quantity", "Price", "Category"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
        JButton addButton = new JButton("Add Product");
        addButton.addActionListener(e -> addProduct());
        buttonPanel.add(addButton);

        JButton deleteButton = new JButton("Delete Product");
        deleteButton.addActionListener(e -> deleteProduct());
        buttonPanel.add(deleteButton);
        JButton lowStockButton = new JButton("Low Stock");
        lowStockButton.addActionListener(e -> showLowStockProducts());
        buttonPanel.add(lowStockButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> updateDisplay());
        buttonPanel.add(refreshButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        updateDisplay();

        frame.setVisible(true);
    }

    private void addProduct() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField categoryField = new JTextField();

        Object[] fields = {
            "Product ID:", idField,
            "Name:", nameField,
            "Quantity:", quantityField,
            "Price:", priceField,
            "Category:", categoryField
        };

        int result = JOptionPane.showConfirmDialog(null, fields, "Add Product", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String id = idField.getText();
                String name = nameField.getText();
                int quantity = Integer.parseInt(quantityField.getText());
                double price = Double.parseDouble(priceField.getText());
                String category = categoryField.getText();

                String query = "INSERT INTO Product (id, name, quantity, price, category) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, id);
                stmt.setString(2, name);
                stmt.setInt(3, quantity);
                stmt.setDouble(4, price);
                stmt.setString(5, category);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(null, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                updateDisplay();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Invalid input or database error!", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }


    private void deleteProduct() {
        String productId = JOptionPane.showInputDialog(null, "Enter Product ID to delete:");
        if (productId != null && !productId.trim().isEmpty()) {
            try {
                String query = "DELETE FROM Product WHERE id = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, productId);
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Product deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Product ID not found!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                updateDisplay();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Database error!", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void showLowStockProducts() {
        String thresholdStr = JOptionPane.showInputDialog(null, "Enter low stock threshold:");
        try {
            int threshold = Integer.parseInt(thresholdStr);
            String query = "SELECT * FROM Product WHERE quantity < ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, threshold);
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel lowStockModel = new DefaultTableModel(new String[]{"Product ID", "Name", "Quantity", "Price", "Category"}, 0);
            while (rs.next()) {
                lowStockModel.addRow(new Object[]{
                    rs.getString("id"), rs.getString("name"), rs.getInt("quantity"),
                    rs.getDouble("price"), rs.getString("category")
                });
            }

            JTable lowStockTable = new JTable(lowStockModel);
            JOptionPane.showMessageDialog(null, new JScrollPane(lowStockTable), "Low Stock Products", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Invalid input or database error!", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void updateDisplay() {
        try {
            tableModel.setRowCount(0); // Clear existing rows
            String query = "SELECT * FROM Product";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("id"), rs.getString("name"), rs.getInt("quantity"),
                    rs.getDouble("price"), rs.getString("category")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database error!", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
