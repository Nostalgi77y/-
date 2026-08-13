ALTER TABLE orders
    ADD COLUMN payment_channel VARCHAR(20) NULL AFTER pay_status,
    ADD COLUMN transaction_id VARCHAR(64) NULL AFTER payment_channel,
    ADD COLUMN prepay_id VARCHAR(128) NULL AFTER transaction_id;

CREATE UNIQUE INDEX uk_orders_transaction_id ON orders(transaction_id);
