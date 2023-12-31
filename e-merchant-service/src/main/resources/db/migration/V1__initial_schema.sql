CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(10, 2),
    status VARCHAR(100),
    customer_email VARCHAR(255),
    transaction_type VARCHAR(255),
    transaction_time timestamp,
    phone VARCHAR(15),
    merchant_id BIGINT,
    reference_id BIGINT,
    CONSTRAINT fk_merchant
        FOREIGN KEY (merchant_id)
            REFERENCES merchants(id)
);

CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    username VARCHAR(255) NOT NULL, UNIQUE
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL, UNIQUE
    user_type VARCHAR(10) NOT NULL,
    description TEXT,
    status VARCHAR(100),
    total_transaction_sum DECIMAL(10, 2)
    admin_level INT,
    admin_location VARCHAR(255)
);
