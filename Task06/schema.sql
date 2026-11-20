CREATE DATABASE IF NOT EXISTS campus_events;
USE campus_events;

CREATE TABLE IF NOT EXISTS events (
    event_id INT PRIMARY KEY AUTO_INCREMENT,
    event_name VARCHAR(120) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS registrations (
    registration_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id VARCHAR(30) NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    event_id INT NOT NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_registration_event FOREIGN KEY (event_id) REFERENCES events(event_id),
    CONSTRAINT uq_student_event UNIQUE (student_id, event_id)
);

INSERT IGNORE INTO events(event_id, event_name) VALUES
(1, 'Cultural Fest'), (2, 'Music Night'), (3, 'Tech Expo');

CREATE INDEX idx_registrations_event ON registrations(event_id);
