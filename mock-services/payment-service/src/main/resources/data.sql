INSERT INTO payments (id, user_id, amount, status, payment_method)
VALUES (1, 1, 499.99, 'SUCCESS', 'UPI')
ON CONFLICT (id) DO NOTHING;

INSERT INTO payments (id, user_id, amount, status, payment_method)
VALUES (2, 2, 1299.50, 'PENDING', 'CARD')
ON CONFLICT (id) DO NOTHING;

INSERT INTO payments (id, user_id, amount, status, payment_method)
VALUES (3, 3, 799.00, 'FAILED', 'NET_BANKING')
ON CONFLICT (id) DO NOTHING;

INSERT INTO payments (id, user_id, amount, status, payment_method)
VALUES (4, 1, 249.00, 'SUCCESS', 'WALLET')
ON CONFLICT (id) DO NOTHING;

INSERT INTO payments (id, user_id, amount, status, payment_method)
VALUES (5, 4, 1999.99, 'SUCCESS', 'CARD')
ON CONFLICT (id) DO NOTHING;
