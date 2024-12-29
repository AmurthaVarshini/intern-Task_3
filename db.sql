-- 1. Create Database

CREATE DATABASE InventoryDB;
USE InventoryDB;

-- 2. Create Tables

-- a. Product Table

CREATE TABLE Product (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50) NOT NULL
);

-- b. User Table

CREATE TABLE User (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL 
);


-- 3. Sample Data
-- Insert Sample Products

INSERT INTO Product (id, name, quantity, price, category)
VALUES
('P001', 'Laptop', 15, 800.00, 'Electronics'),
('P002', 'Smartphone', 25, 500.00, 'Electronics'),
('P003', 'Desk Chair', 10, 120.00, 'Furniture'),
('P004', 'Bookshelf', 5, 90.00, 'Furniture'),
('P005', 'Headphones', 30, 50.00, 'Accessories');


-- 4. Insert Sample Users

INSERT INTO User (username, password)
VALUES
('admin', 'admin123'), 
('employee', 'emp456');