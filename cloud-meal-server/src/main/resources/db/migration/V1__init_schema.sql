CREATE TABLE employee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'ADMIN',
    status TINYINT NOT NULL DEFAULT 1,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_employee_username (username)
);

CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    openid VARCHAR(64),
    nickname VARCHAR(80) NOT NULL,
    avatar VARCHAR(500),
    phone VARCHAR(20),
    status TINYINT NOT NULL DEFAULT 1,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_user_openid (openid)
);

CREATE TABLE category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    type TINYINT NOT NULL DEFAULT 1 COMMENT '1菜品 2套餐',
    sort INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    KEY idx_category_type_status_sort (type, status, sort)
);

CREATE TABLE dish (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    image VARCHAR(500),
    description VARCHAR(500),
    stock INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    version INT NOT NULL DEFAULT 0,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    KEY idx_dish_category_status (category_id, status),
    CONSTRAINT chk_dish_price CHECK (price >= 0),
    CONSTRAINT chk_dish_stock CHECK (stock >= 0)
);

CREATE TABLE address_book (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    consignee VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    province VARCHAR(50),
    city VARCHAR(50),
    district VARCHAR(50),
    detail VARCHAR(255) NOT NULL,
    is_default TINYINT NOT NULL DEFAULT 0,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    KEY idx_address_user_default (user_id, is_default)
);

CREATE TABLE shopping_cart (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    dish_id BIGINT NOT NULL,
    dish_name VARCHAR(100) NOT NULL,
    image VARCHAR(500),
    unit_price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_cart_user_dish (user_id, dish_id),
    KEY idx_cart_user (user_id)
);

CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(40) NOT NULL,
    client_order_no VARCHAR(64) NOT NULL,
    user_id BIGINT NOT NULL,
    address_book_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    pay_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    amount DECIMAL(10,2) NOT NULL,
    consignee VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(500) NOT NULL,
    remark VARCHAR(500),
    payment_time DATETIME,
    cancel_reason VARCHAR(255),
    version INT NOT NULL DEFAULT 0,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_order_number (order_number),
    UNIQUE KEY uk_user_client_order (user_id, client_order_no),
    KEY idx_order_user_status_time (user_id, status, created_time),
    KEY idx_order_status_time (status, created_time)
);

CREATE TABLE order_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    dish_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    image VARCHAR(500),
    unit_price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_order_detail_order (order_id)
);

INSERT INTO category(name, type, sort) VALUES ('热销推荐', 1, 1), ('招牌主食', 1, 2), ('饮品甜点', 1, 3);
INSERT INTO dish(category_id, name, price, description, stock) VALUES
(1, '宫保鸡丁', 28.00, '鸡肉鲜嫩，酸甜微辣', 100),
(1, '鱼香肉丝', 26.00, '经典川味，下饭首选', 100),
(2, '扬州炒饭', 18.00, '粒粒分明，配料丰富', 200),
(3, '冰柠檬茶', 8.00, '清爽解腻', 200);
INSERT INTO user(openid, nickname) VALUES ('demo-openid', '演示用户');
INSERT INTO address_book(user_id, consignee, phone, province, city, district, detail, is_default)
VALUES (1, '演示用户', '13800000000', '江苏省', '南京市', '雨花台区', '软件大道100号', 1);
