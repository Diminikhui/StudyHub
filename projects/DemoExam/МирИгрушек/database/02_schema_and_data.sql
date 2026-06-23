BEGIN;

DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS pickup_points CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS suppliers CASCADE;
DROP TABLE IF EXISTS manufacturers CASCADE;
DROP TABLE IF EXISTS units CASCADE;

CREATE TABLE roles (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    full_name VARCHAR(200) NOT NULL,
    login VARCHAR(200) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE categories (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(200) NOT NULL UNIQUE
);

CREATE TABLE suppliers (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(200) NOT NULL UNIQUE
);

CREATE TABLE manufacturers (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(200) NOT NULL UNIQUE
);

CREATE TABLE units (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE products (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    article VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(500) NOT NULL,
    unit_id BIGINT NOT NULL REFERENCES units(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    price NUMERIC(12, 2) NOT NULL CHECK (price >= 0),
    supplier_id BIGINT NOT NULL REFERENCES suppliers(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    manufacturer_id BIGINT NOT NULL REFERENCES manufacturers(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    category_id BIGINT NOT NULL REFERENCES categories(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    discount INTEGER NOT NULL CHECK (discount BETWEEN 0 AND 100),
    stock INTEGER NOT NULL CHECK (stock >= 0),
    description TEXT NOT NULL,
    image_path VARCHAR(500)
);

CREATE TABLE pickup_points (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    address VARCHAR(500) NOT NULL
);

CREATE TABLE orders (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_number INTEGER NOT NULL UNIQUE,
    order_date VARCHAR(20) NOT NULL,
    delivery_date VARCHAR(20) NOT NULL,
    pickup_point_id BIGINT NOT NULL REFERENCES pickup_points(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    user_id BIGINT NOT NULL REFERENCES users(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    pickup_code VARCHAR(30) NOT NULL,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE order_items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON UPDATE CASCADE ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    UNIQUE (order_id, product_id)
);

CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_supplier ON products(supplier_id);
CREATE INDEX idx_products_price ON products(price);
CREATE INDEX idx_products_stock ON products(stock);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);

INSERT INTO roles(code, name) VALUES
('admin', 'Администратор'),
('manager', 'Менеджер'),
('client', 'Авторизированный клиент');

INSERT INTO users(role_id, full_name, login, password) VALUES
((SELECT id FROM roles WHERE code = 'admin'), 'Ворсин Петр Евгеньевич', '94d5ous@gmail.com', 'uzWC67'),
((SELECT id FROM roles WHERE code = 'admin'), 'Старикова Елена Павловна', 'uth4iz@mail.com', '2L6KZG'),
((SELECT id FROM roles WHERE code = 'admin'), 'Одинцов Серафим Артёмович', 'yzls62@outlook.com', 'JlFRCZ'),
((SELECT id FROM roles WHERE code = 'manager'), 'Михайлюк Анна Вячеславовна', '1diph5e@tutanota.com', '8ntwUp'),
((SELECT id FROM roles WHERE code = 'manager'), 'Ситдикова Елена Анатольевна', 'tjde7c@yahoo.com', 'YOyhfR'),
((SELECT id FROM roles WHERE code = 'manager'), 'Никифорова Весения Николаевна', 'wpmrc3do@tutanota.com', 'RSbvHv'),
((SELECT id FROM roles WHERE code = 'client'), 'Степанов Михаил Артёмович', '5d4zbu@tutanota.com', 'rwVDh9'),
((SELECT id FROM roles WHERE code = 'client'), 'Ворсин Петр Евгеньевич', 'ptec8ym@yahoo.com', 'LdNyos'),
((SELECT id FROM roles WHERE code = 'client'), 'Старикова Елена Павловна', '1qz4kw@mail.com', 'gynQMT'),
((SELECT id FROM roles WHERE code = 'client'), 'Сазонов Руслан Германович', '4np6se@mail.com', 'AtnDjr');

INSERT INTO categories(name) VALUES
('Игровой набор'),
('Конструктор'),
('Детский музыкальный инструмент'),
('Машинка');

INSERT INTO suppliers(name) VALUES
('Pikeshop'),
('Playbig'),
('Knauf'),
('CHILITOY'),
('Vinylon');

INSERT INTO manufacturers(name) VALUES
('ABSпластик'),
('BambiniFelici'),
('Junion');

INSERT INTO units(name) VALUES
('шт.');

INSERT INTO products(
    article,
    name,
    unit_id,
    price,
    supplier_id,
    manufacturer_id,
    category_id,
    discount,
    stock,
    description,
    image_path
) VALUES
(
    'PMEZMH',
    'Детский игровой набор машинок Щенячий патруль / Dogs mini . 9 героев + 9 инерфионных машинок',
    (SELECT id FROM units WHERE name = 'шт.'),
    1414,
    (SELECT id FROM suppliers WHERE name = 'Pikeshop'),
    (SELECT id FROM manufacturers WHERE name = 'ABSпластик'),
    (SELECT id FROM categories WHERE name = 'Игровой набор'),
    22,
    50,
    'Детский набор машинок с героями мультсериала «Щенячий патруль» подойдет как для мальчиков, так и для девочек. В детский набор входит 9 фигурок щенков спасателей. ',
    'resources/images/products/1.JPG'
),
(
    'BPV4MM',
    'Конструктор Гарри Поттер Сова Букля 630 деталей совместим с lego harry potter, лего совместимый)',
    (SELECT id FROM units WHERE name = 'шт.'),
    771,
    (SELECT id FROM suppliers WHERE name = 'Playbig'),
    (SELECT id FROM manufacturers WHERE name = 'ABSпластик'),
    (SELECT id FROM categories WHERE name = 'Конструктор'),
    15,
    26,
    'Коллекционная модель Букля состоит из множества потрясающих элементов, а также специального механизма внутри. С его помощью можно плавно поднимать-опускать крылья птицы.',
    'resources/images/products/2.JPG'
),
(
    'JVL42J',
    'Музыкальные инструменты для детей, ксилофон, барабаны, развивающие игрушки, игрушки для детей',
    (SELECT id FROM units WHERE name = 'шт.'),
    2750,
    (SELECT id FROM suppliers WHERE name = 'Playbig'),
    (SELECT id FROM manufacturers WHERE name = 'BambiniFelici'),
    (SELECT id FROM categories WHERE name = 'Детский музыкальный инструмент'),
    15,
    0,
    'Откройте мир музыки для вашего ребенка с этой уникальной игрушкой! Это многофункциональное музыкальное чудо объединяет в себе всё, что нужно для творческого развития.',
    'resources/images/products/3.JPG'
),
(
    'F895RB',
    'Машинка игрушка диско шар светящаяся музыкальная',
    (SELECT id FROM units WHERE name = 'шт.'),
    368,
    (SELECT id FROM suppliers WHERE name = 'Knauf'),
    (SELECT id FROM manufacturers WHERE name = 'ABSпластик'),
    (SELECT id FROM categories WHERE name = 'Машинка'),
    6,
    7,
    'Светящаяся музыкальная машина с диско шаром переливается разными цветами, играет ритмичные мелодии, объезжает препятствия и крутится, поэтому с ней точно не будет скучно.',
    'resources/images/products/4.JPG'
),
(
    '3XBOTN',
    'Игровой набор Hot Wheels Action Loop Cyclone Challenge Track, с машинкой и удобным хранением, HTK16',
    (SELECT id FROM units WHERE name = 'шт.'),
    3426,
    (SELECT id FROM suppliers WHERE name = 'Knauf'),
    (SELECT id FROM manufacturers WHERE name = 'BambiniFelici'),
    (SELECT id FROM categories WHERE name = 'Игровой набор'),
    10,
    21,
    'Игровой набор Hot Wheels Action Loop Cyclone Challenge Track - это уникальная игра, которая позволит вам испытать себя и своих друзей в скорости и ловкости. Этот набор состоит из металлической дорожки с циклоном, которая создает потрясающий эффект и добавляет дополнительную сложность в игру.',
    'resources/images/products/5.JPG'
),
(
    '3L7RCZ',
    'Игровой набор с деревянными машинками Стройплощадка Кран-Паркс, Junion',
    (SELECT id FROM units WHERE name = 'шт.'),
    7400,
    (SELECT id FROM suppliers WHERE name = 'Knauf'),
    (SELECT id FROM manufacturers WHERE name = 'Junion'),
    (SELECT id FROM categories WHERE name = 'Игровой набор'),
    15,
    0,
    'Игровой набор «Стройплощадка Кран-Паркс Junion» — это большая игрушечная парковка с деревянными машинками и настоящим подъёмным краном, придуманная в Яндексе настоящими родителями.',
    'resources/images/products/6.JPG'
),
(
    'S72AM3',
    'Синтезатор детский с микрофоном 61 клавиша',
    (SELECT id FROM units WHERE name = 'шт.'),
    1749,
    (SELECT id FROM suppliers WHERE name = 'CHILITOY'),
    (SELECT id FROM manufacturers WHERE name = 'Junion'),
    (SELECT id FROM categories WHERE name = 'Детский музыкальный инструмент'),
    10,
    35,
    'Откройте для ребенка дверь в мир музыки с детским синтезатором! Этот компактный инструмент с микрофоном станет верным другом для юных музыкантов, помогая им развивать творческий потенциал и получать удовольствие от игры.',
    'resources/images/products/7.JPG'
),
(
    '2G3280',
    'Деревянный игровой набор JUNION Стройплощадка "Кран-Паркс" с подъёмным, строительным краном и машинками, 18 предметов, подвижные элементы',
    (SELECT id FROM units WHERE name = 'шт.'),
    1624,
    (SELECT id FROM suppliers WHERE name = 'Vinylon'),
    (SELECT id FROM manufacturers WHERE name = 'Junion'),
    (SELECT id FROM categories WHERE name = 'Игровой набор'),
    9,
    20,
    'Игровой набор «Стройплощадка Кран-Паркс Junion» — это большая игрушечная парковка с деревянными машинками и настоящим подъёмным краном, придуманная в Яндексе настоящими родителями.',
    'resources/images/products/8.JPG'
),
(
    'MIO8YV',
    'Музыкальная игрушка интерактивная Пульт, детский прорезыватель для малышей',
    (SELECT id FROM units WHERE name = 'шт.'),
    305,
    (SELECT id FROM suppliers WHERE name = 'Vinylon'),
    (SELECT id FROM manufacturers WHERE name = 'BambiniFelici'),
    (SELECT id FROM categories WHERE name = 'Детский музыкальный инструмент'),
    9,
    31,
    'Музыкальная игрушка интерактивная Пульт, детский прорезыватель для малышей',
    'resources/images/products/9.JPG'
),
(
    'UER2QD',
    'Большой набор опытов и экспериментов для детей 14 в 1',
    (SELECT id FROM units WHERE name = 'шт.'),
    2506,
    (SELECT id FROM suppliers WHERE name = 'Vinylon'),
    (SELECT id FROM manufacturers WHERE name = 'BambiniFelici'),
    (SELECT id FROM categories WHERE name = 'Игровой набор'),
    8,
    27,
    'Большой набор опытов и экспериментов для детей 14 в 1',
    'resources/images/products/10.JPG'
);

INSERT INTO pickup_points(address) VALUES
('420151, г. Лесной, ул. Вишневая, 32'),
('125061, г. Лесной, ул. Подгорная, 8'),
('630370, г. Лесной, ул. Шоссейная, 24'),
('400562, г. Лесной, ул. Зеленая, 32'),
('614510, г. Лесной, ул. Маяковского, 47'),
('410542, г. Лесной, ул. Светлая, 46'),
('620839, г. Лесной, ул. Цветочная, 8'),
('443890, г. Лесной, ул. Коммунистическая, 1'),
('603379, г. Лесной, ул. Спортивная, 46'),
('603721, г. Лесной, ул. Гоголя, 41'),
('410172, г. Лесной, ул. Северная, 13'),
('614611, г. Лесной, ул. Молодежная, 50'),
('454311, г.Лесной, ул. Новая, 19'),
('660007, г.Лесной, ул. Октябрьская, 19'),
('603036, г. Лесной, ул. Садовая, 4'),
('394060, г.Лесной, ул. Фрунзе, 43'),
('410661, г. Лесной, ул. Школьная, 50'),
('625590, г. Лесной, ул. Коммунистическая, 20'),
('625683, г. Лесной, ул. 8 Марта'),
('450983, г.Лесной, ул. Комсомольская, 26'),
('394782, г. Лесной, ул. Чехова, 3'),
('603002, г. Лесной, ул. Дзержинского, 28'),
('450558, г. Лесной, ул. Набережная, 30'),
('344288, г. Лесной, ул. Чехова, 1'),
('614164, г.Лесной,  ул. Степная, 30'),
('394242, г. Лесной, ул. Коммунистическая, 43'),
('660540, г. Лесной, ул. Солнечная, 25'),
('125837, г. Лесной, ул. Шоссейная, 40'),
('125703, г. Лесной, ул. Партизанская, 49'),
('625283, г. Лесной, ул. Победы, 46'),
('614753, г. Лесной, ул. Полевая, 35'),
('426030, г. Лесной, ул. Маяковского, 44'),
('450375, г. Лесной ул. Клубная, 44'),
('625560, г. Лесной, ул. Некрасова, 12'),
('630201, г. Лесной, ул. Комсомольская, 17'),
('190949, г. Лесной, ул. Мичурина, 26');

INSERT INTO orders(
    order_number,
    order_date,
    delivery_date,
    pickup_point_id,
    user_id,
    pickup_code,
    status
) VALUES
(1, '27.02.2025', '20.04.2025', 1, 7, '901', 'Завершен'),
(2, '28.09.2024', '21.04.2025', 11, 8, '902', 'Завершен'),
(3, '21.03.2025', '22.04.2025', 2, 9, '903', 'Завершен'),
(4, '20.02.2025', '23.04.2025', 11, 10, '904', 'Завершен'),
(5, '17.03.2025', '24.04.2025', 2, 7, '905', 'Завершен'),
(6, '01.03.2025', '25.04.2025', 15, 8, '906', 'Завершен'),
(7, '30.02.2025', '26.04.2025', 3, 9, '907', 'Завершен'),
(8, '31.03.2025', '27.04.2025', 19, 10, '908', 'Новый '),
(9, '02.04.2025', '28.04.2025', 5, 9, '909', 'Новый '),
(10, '03.04.2025', '29.04.2025', 19, 10, '910', 'Новый ');

INSERT INTO order_items(order_id, product_id, quantity) VALUES
((SELECT id FROM orders WHERE order_number = 1), (SELECT id FROM products WHERE article = 'PMEZMH'), 2),
((SELECT id FROM orders WHERE order_number = 1), (SELECT id FROM products WHERE article = 'BPV4MM'), 2),
((SELECT id FROM orders WHERE order_number = 2), (SELECT id FROM products WHERE article = 'JVL42J'), 1),
((SELECT id FROM orders WHERE order_number = 2), (SELECT id FROM products WHERE article = 'F895RB'), 1),
((SELECT id FROM orders WHERE order_number = 3), (SELECT id FROM products WHERE article = '3XBOTN'), 10),
((SELECT id FROM orders WHERE order_number = 3), (SELECT id FROM products WHERE article = '3L7RCZ'), 10),
((SELECT id FROM orders WHERE order_number = 4), (SELECT id FROM products WHERE article = 'S72AM3'), 5),
((SELECT id FROM orders WHERE order_number = 4), (SELECT id FROM products WHERE article = '2G3280'), 4),
((SELECT id FROM orders WHERE order_number = 5), (SELECT id FROM products WHERE article = 'MIO8YV'), 2),
((SELECT id FROM orders WHERE order_number = 5), (SELECT id FROM products WHERE article = 'UER2QD'), 2),
((SELECT id FROM orders WHERE order_number = 6), (SELECT id FROM products WHERE article = 'PMEZMH'), 2),
((SELECT id FROM orders WHERE order_number = 6), (SELECT id FROM products WHERE article = 'BPV4MM'), 2),
((SELECT id FROM orders WHERE order_number = 7), (SELECT id FROM products WHERE article = 'JVL42J'), 1),
((SELECT id FROM orders WHERE order_number = 7), (SELECT id FROM products WHERE article = 'F895RB'), 1),
((SELECT id FROM orders WHERE order_number = 8), (SELECT id FROM products WHERE article = '3XBOTN'), 10),
((SELECT id FROM orders WHERE order_number = 8), (SELECT id FROM products WHERE article = '3L7RCZ'), 10),
((SELECT id FROM orders WHERE order_number = 9), (SELECT id FROM products WHERE article = 'S72AM3'), 5),
((SELECT id FROM orders WHERE order_number = 9), (SELECT id FROM products WHERE article = '2G3280'), 4),
((SELECT id FROM orders WHERE order_number = 10), (SELECT id FROM products WHERE article = 'MIO8YV'), 2),
((SELECT id FROM orders WHERE order_number = 10), (SELECT id FROM products WHERE article = 'UER2QD'), 2);

COMMIT;
