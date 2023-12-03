CREATE TABLE IF NOT EXISTS merchants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description TEXT,
    status VARCHAR(100),
    total_transaction_sum DECIMAL(10, 2)
);

CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(10, 2),
    status VARCHAR(100),
    customer_email VARCHAR(255),
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
);
