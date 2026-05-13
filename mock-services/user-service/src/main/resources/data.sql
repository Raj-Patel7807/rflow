INSERT INTO users(id, name, email)
VALUES (1, 'Raj', 'raj@gmail.com')
ON CONFLICT (id) DO NOTHING;

INSERT INTO users(id, name, email)
VALUES (2, 'RajJR', 'rajjr@gmail.com')
ON CONFLICT (id) DO NOTHING;

INSERT INTO users(id, name, email)
VALUES (3, 'user', 'user@gmail.com')
ON CONFLICT (id) DO NOTHING;

INSERT INTO users(id, name, email)
VALUES (4, 'user1', 'user1@gmail.com')
ON CONFLICT (id) DO NOTHING;

INSERT INTO users(id, name, email)
VALUES (5, 'user2', 'user2@gmail.com')
ON CONFLICT (id) DO NOTHING;
