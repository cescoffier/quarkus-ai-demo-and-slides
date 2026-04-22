INSERT INTO customer (id, name, email) VALUES (1, 'Alice Martin', 'alice@example.com');
INSERT INTO customer (id, name, email) VALUES (2, 'Bob Johnson', 'bob@example.com');
INSERT INTO customer (id, name, email) VALUES (3, 'Carol Williams', 'carol@example.com');

ALTER SEQUENCE customer_seq RESTART WITH 100;

INSERT INTO orders (id, product, amount, status, customer_id) VALUES (1234, 'Acme Widget Pro', 49.99, 'CONFIRMED', 1);
INSERT INTO orders (id, product, amount, status, customer_id) VALUES (1235, 'Acme Gizmo X', 129.50, 'SHIPPED', 2);
INSERT INTO orders (id, product, amount, status, customer_id) VALUES (1236, 'Acme Turbo Blender', 89.00, 'PENDING', 1);
INSERT INTO orders (id, product, amount, status, customer_id) VALUES (1237, 'Acme Smart Lamp', 45.00, 'DELIVERED', 3);
INSERT INTO orders (id, product, amount, status, customer_id) VALUES (1238, 'Acme Widget Pro', 499.99, 'CONFIRMED', 2);

ALTER SEQUENCE orders_seq RESTART WITH 2000;
