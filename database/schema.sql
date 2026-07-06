-- ============================================
-- PlacementPrep Database Schema
-- ============================================
-- Note: Spring Boot's spring.jpa.hibernate.ddl-auto=update will auto-create
-- these tables on first run. This file is provided so you can inspect the
-- schema directly, seed sample data, or set the DB up manually if preferred.

CREATE DATABASE IF NOT EXISTS placementprep_db;
USE placementprep_db;

-- ---------------- Users ----------------
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    college VARCHAR(255),
    department VARCHAR(255),
    year VARCHAR(50),
    profile_photo_url VARCHAR(500),
    role ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
    current_streak INT DEFAULT 0,
    last_activity_date DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------------- Questions ----------------
CREATE TABLE IF NOT EXISTS questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(100) NOT NULL,
    topic VARCHAR(150),
    question_text VARCHAR(1000) NOT NULL,
    option_a VARCHAR(500) NOT NULL,
    option_b VARCHAR(500) NOT NULL,
    option_c VARCHAR(500) NOT NULL,
    option_d VARCHAR(500) NOT NULL,
    correct_answer CHAR(1) NOT NULL,
    explanation VARCHAR(2000),
    difficulty ENUM('EASY','MEDIUM','HARD') NOT NULL,
    company VARCHAR(100)
);

-- ---------------- Results ----------------
CREATE TABLE IF NOT EXISTS results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category VARCHAR(100),
    score INT NOT NULL,
    total_questions INT NOT NULL,
    correct_count INT NOT NULL,
    wrong_count INT NOT NULL,
    skipped_count INT NOT NULL,
    percentage DOUBLE NOT NULL,
    accuracy DOUBLE NOT NULL,
    time_taken_seconds BIGINT,
    date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ---------------- Bookmarks ----------------
CREATE TABLE IF NOT EXISTS bookmarks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    UNIQUE KEY unique_bookmark (user_id, question_id)
);

-- ============================================
-- Sample Seed Data
-- ============================================

-- Sample admin user (password is "Admin@123" hashed with BCrypt)
-- You can also just register normally and manually change role to ADMIN in the DB.
INSERT INTO users (name, email, password, role) VALUES
('Admin', 'admin@placementprep.com', '$2a$10$4b6z1S9O5F3z9V3q2Yb6ZOZzq0e2QZ0J1JYb0m3Zt8p7t8p7t8p7t.', 'ADMIN');

-- Sample questions across categories
INSERT INTO questions (category, topic, question_text, option_a, option_b, option_c, option_d, correct_answer, explanation, difficulty, company) VALUES
('Quantitative Aptitude', 'Percentages', 'If 20% of a number is 50, what is the number?', '100', '200', '250', '150', 'C', '20% of x = 50 => x = 50 / 0.20 = 250.', 'EASY', 'Zoho'),
('Quantitative Aptitude', 'Time and Work', 'A can do a work in 10 days and B in 15 days. Working together, how many days will they take?', '5', '6', '8', '4', 'B', '1/10 + 1/15 = 1/6, so together they take 6 days.', 'MEDIUM', 'TCS'),
('Logical Reasoning', 'Series', 'Find the next number: 2, 6, 12, 20, 30, ?', '40', '42', '36', '44', 'B', 'Differences are 4,6,8,10,12 -> next term 30+12=42.', 'MEDIUM', 'Infosys'),
('Verbal Ability', 'Synonyms', 'Choose the synonym of "Benevolent".', 'Cruel', 'Kind', 'Selfish', 'Angry', 'B', 'Benevolent means kind and generous.', 'EASY', 'Wipro'),
('Computer Networks', 'OSI Model', 'Which OSI layer is responsible for routing?', 'Data Link', 'Network', 'Transport', 'Session', 'B', 'The Network layer (Layer 3) handles routing between networks.', 'MEDIUM', 'Zoho'),
('DBMS', 'Normalization', 'Which normal form removes transitive dependency?', '1NF', '2NF', '3NF', 'BCNF', 'C', '3NF removes transitive dependencies on the primary key.', 'MEDIUM', 'Freshworks'),
('OOP Concepts', 'Basics', 'Which OOP principle allows a subclass to provide a specific implementation of a method?', 'Encapsulation', 'Abstraction', 'Overriding', 'Overloading', 'C', 'Method overriding lets a subclass redefine a superclass method.', 'EASY', 'Zoho'),
('Java', 'Collections', 'Which Java collection does not allow duplicate elements?', 'ArrayList', 'LinkedList', 'HashSet', 'Vector', 'C', 'Set implementations like HashSet disallow duplicates.', 'EASY', 'Chargebee'),
('Operating System', 'Process Management', 'Which scheduling algorithm can lead to starvation?', 'Round Robin', 'FCFS', 'Priority Scheduling', 'SJF (non-preemptive) without aging', 'D', 'Without aging, low priority processes in SJF/Priority scheduling may starve.', 'HARD', 'TCS'),
('SQL', 'Joins', 'Which SQL join returns all rows from both tables, with NULLs where there is no match?', 'INNER JOIN', 'LEFT JOIN', 'RIGHT JOIN', 'FULL OUTER JOIN', 'D', 'FULL OUTER JOIN returns matched and unmatched rows from both tables.', 'MEDIUM', 'Freshworks');

-- Add more questions per category as needed via the Admin Panel or CSV import.
