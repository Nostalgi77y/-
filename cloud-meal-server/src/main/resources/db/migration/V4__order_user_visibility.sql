ALTER TABLE orders
    ADD COLUMN user_visible TINYINT NOT NULL DEFAULT 1 COMMENT '1用户端可见，0用户端已删除' AFTER deleted;
