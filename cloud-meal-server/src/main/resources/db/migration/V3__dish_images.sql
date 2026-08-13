UPDATE dish SET image = '/api/dishes/gong-bao-chicken.png' WHERE id = 1;
UPDATE dish SET image = '/api/dishes/yu-xiang-pork.png' WHERE id = 2;
UPDATE dish SET image = '/api/dishes/yangzhou-fried-rice.png' WHERE id = 3;
UPDATE dish SET image = '/api/dishes/iced-lemon-tea.png' WHERE id = 4;
UPDATE dish SET image = '/api/dishes/pan-fried-chicken.png' WHERE name = '云膳香煎鸡排';

INSERT INTO dish(id, category_id, name, price, image, description, stock, status, version)
SELECT 5, 1, '云膳香煎鸡排', 28.00, '/api/dishes/pan-fried-chicken.png', '现点现煎，外酥里嫩', 100, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM dish WHERE name = '云膳香煎鸡排' AND deleted = 0);
