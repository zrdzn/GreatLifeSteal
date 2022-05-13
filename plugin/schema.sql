CREATE TABLE IF NOT EXISTS gls_users (
    id INT PRIMARY KEY,
    user_uuid VARCHAR(36) NOT NULL UNIQUE KEY,
    user_health INT DEFAULT 0
);