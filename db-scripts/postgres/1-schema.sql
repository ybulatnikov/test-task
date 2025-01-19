CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    login VARCHAR(50),
    first_name VARCHAR(100),
    last_name VARCHAR(100)
)
