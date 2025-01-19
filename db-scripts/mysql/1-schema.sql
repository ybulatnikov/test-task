CREATE TABLE IF NOT EXISTS user_table (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    ldap_login VARCHAR(50),
    name VARCHAR(100),
    surname VARCHAR(100)
)
