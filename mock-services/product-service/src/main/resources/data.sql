INSERT INTO products (name, category, price, stock, brand)
VALUES ('iPhone 15', 'Electronics', 79999.00, 10, 'Apple')
ON CONFLICT (id) DO NOTHING;

INSERT INTO products (name, category, price, stock, brand)
VALUES ('Galaxy S24', 'Electronics', 69999.00, 15, 'Samsung')
ON CONFLICT (id) DO NOTHING;

INSERT INTO products (name, category, price, stock, brand)
VALUES ('MacBook Air M3', 'Laptops', 124999.00, 5, 'Apple')
ON CONFLICT (id) DO NOTHING;

INSERT INTO products (name, category, price, stock, brand)
VALUES ('Sony WH-1000XM5', 'Audio', 29999.00, 20, 'Sony')
ON CONFLICT (id) DO NOTHING;

INSERT INTO products (name, category, price, stock, brand)
VALUES ('iPad Air', 'Tablets', 59999.00, 8, 'Apple')
ON CONFLICT (id) DO NOTHING;
