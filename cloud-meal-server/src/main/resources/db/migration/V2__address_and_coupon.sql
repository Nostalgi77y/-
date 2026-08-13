CREATE TABLE coupon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    threshold_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(10,2) NOT NULL,
    total_count INT NOT NULL DEFAULT 0,
    received_count INT NOT NULL DEFAULT 0,
    valid_from DATETIME NOT NULL,
    valid_until DATETIME NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    KEY idx_coupon_status_time (status, valid_from, valid_until)
);

CREATE TABLE user_coupon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    coupon_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'UNUSED',
    used_order_id BIGINT,
    received_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    used_time DATETIME,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_user_coupon (user_id, coupon_id),
    KEY idx_user_coupon_status (user_id, status)
);

ALTER TABLE orders
    ADD COLUMN original_amount DECIMAL(10,2) NULL AFTER pay_status,
    ADD COLUMN discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0 AFTER original_amount,
    ADD COLUMN user_coupon_id BIGINT NULL AFTER discount_amount;

UPDATE orders SET original_amount = amount WHERE original_amount IS NULL;
ALTER TABLE orders MODIFY original_amount DECIMAL(10,2) NOT NULL;

INSERT INTO coupon(name, threshold_amount, discount_amount, total_count, valid_from, valid_until)
VALUES
('新人立减券', 20.00, 5.00, 1000, NOW(), DATE_ADD(NOW(), INTERVAL 365 DAY)),
('满50减10', 50.00, 10.00, 500, NOW(), DATE_ADD(NOW(), INTERVAL 180 DAY));
