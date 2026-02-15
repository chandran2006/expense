-- SaveUp Database Setup Script
-- MySQL 8.0+

-- Create Database
CREATE DATABASE IF NOT EXISTS saveup;
USE saveup;

-- Note: Tables will be auto-created by Hibernate with spring.jpa.hibernate.ddl-auto=update
-- This script is for reference and manual setup if needed

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    preferred_language VARCHAR(10) NOT NULL DEFAULT 'EN',
    theme_mode VARCHAR(10) NOT NULL DEFAULT 'LIGHT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Transactions Table
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    category VARCHAR(100) NOT NULL,
    amount DOUBLE NOT NULL,
    description TEXT,
    date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_date (user_id, date),
    INDEX idx_user_type (user_id, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Budgets Table
CREATE TABLE IF NOT EXISTS budgets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    month VARCHAR(7) NOT NULL,
    limit_amount DOUBLE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_month (user_id, month),
    INDEX idx_user_month (user_id, month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Chat History Table
CREATE TABLE IF NOT EXISTS chat_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    response TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_timestamp (user_id, timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Sample Data (Optional - for testing)

-- Insert Sample User (password: test123)
-- Note: This is a BCrypt hash of "test123"
INSERT INTO users (name, email, password, role, preferred_language, theme_mode) 
VALUES (
    'Demo User', 
    'demo@saveup.com', 
    '$2a$10$xQGKvXEZvXxVXxVXxVXxVeXxVXxVXxVXxVXxVXxVXxVXxVXxVXxVX',
    'USER',
    'EN',
    'LIGHT'
) ON DUPLICATE KEY UPDATE name=name;

-- Sample Transactions (uncomment to use)
/*
INSERT INTO transactions (user_id, type, category, amount, description, date) VALUES
(1, 'INCOME', 'Salary', 50000.00, 'Monthly salary', '2024-01-01'),
(1, 'EXPENSE', 'Food', 2500.00, 'Groceries', '2024-01-05'),
(1, 'EXPENSE', 'Transport', 1500.00, 'Fuel', '2024-01-07'),
(1, 'EXPENSE', 'Shopping', 3000.00, 'Clothes', '2024-01-10'),
(1, 'EXPENSE', 'Bills', 2000.00, 'Electricity bill', '2024-01-15'),
(1, 'EXPENSE', 'Food', 1800.00, 'Restaurant', '2024-01-18'),
(1, 'EXPENSE', 'Entertainment', 1200.00, 'Movie tickets', '2024-01-20'),
(1, 'EXPENSE', 'Health', 800.00, 'Medicine', '2024-01-22');
*/

-- Sample Budget (uncomment to use)
/*
INSERT INTO budgets (user_id, month, limit_amount) VALUES
(1, '2024-01', 30000.00);
*/

-- Verify Tables
SHOW TABLES;

-- Check Table Structures
DESCRIBE users;
DESCRIBE transactions;
DESCRIBE budgets;
DESCRIBE chat_history;

-- Useful Queries for Testing

-- Get all users
-- SELECT * FROM users;

-- Get transactions for a user
-- SELECT * FROM transactions WHERE user_id = 1 ORDER BY date DESC;

-- Get monthly summary
-- SELECT 
--     type,
--     SUM(amount) as total
-- FROM transactions 
-- WHERE user_id = 1 
--   AND date BETWEEN '2024-01-01' AND '2024-01-31'
-- GROUP BY type;

-- Get category-wise expenses
-- SELECT 
--     category,
--     SUM(amount) as total
-- FROM transactions 
-- WHERE user_id = 1 
--   AND type = 'EXPENSE'
--   AND date BETWEEN '2024-01-01' AND '2024-01-31'
-- GROUP BY category
-- ORDER BY total DESC;

-- Check budget status
-- SELECT 
--     b.month,
--     b.limit_amount,
--     COALESCE(SUM(t.amount), 0) as current_spending,
--     (COALESCE(SUM(t.amount), 0) > b.limit_amount) as exceeded
-- FROM budgets b
-- LEFT JOIN transactions t ON t.user_id = b.user_id 
--     AND t.type = 'EXPENSE'
--     AND DATE_FORMAT(t.date, '%Y-%m') = b.month
-- WHERE b.user_id = 1 AND b.month = '2024-01'
-- GROUP BY b.id, b.month, b.limit_amount;
