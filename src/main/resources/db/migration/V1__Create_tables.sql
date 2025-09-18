CREATE TABLE categories (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT
);

-- Inserir categorias
INSERT INTO categories (name, description) VALUES 
('Eletrônicos', 'Produtos eletrônicos e gadgets'),
('Informática', 'Produtos para computadores e acessórios'),
('Smartphones', 'Telefones celulares e acessórios'),
('Eletrodomésticos', 'Aparelhos para casa'),
('Móveis', 'Móveis para escritório e casa');

CREATE TABLE products (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    price       DECIMAL(19, 2) NOT NULL,
    status      BOOLEAN      NOT NULL,
    code        INT,
    category_id BIGINT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories (id)
);

-- Inserir produtos
INSERT INTO products (name, description, price, status, code, category_id) VALUES 
('Smartphone XYZ', 'Smartphone com 8GB RAM e 128GB de armazenamento', 1299.99, true, 1, 3),
('Notebook ABC', 'Notebook com processador i7, 16GB RAM e SSD 512GB', 3999.99, true, 2, 2),
('Smart TV 50"', 'Smart TV LED 4K com 50 polegadas', 2499.99, true, 3, 1),
('Geladeira Frost Free', 'Geladeira Duplex Frost Free 400L', 3299.99, true, 4, 4),
('Mesa de Escritório', 'Mesa para escritório com gavetas', 499.99, true, 5, 5),
('Cadeira Ergonômica', 'Cadeira ergonômica para escritório', 899.99, true, 6, 5),
('Tablet Premium', 'Tablet com tela de 10 polegadas e 64GB', 1899.99, true, 7, 1),
('Monitor 27"', 'Monitor LED Full HD 27 polegadas', 1199.99, true, 8, 2),
('Fone de Ouvido Bluetooth', 'Fone de ouvido sem fio com cancelamento de ruído', 399.99, true, 9, 1),
('Microondas 30L', 'Forno microondas com 30 litros e múltiplas funções', 599.99, true, 10, 4);


CREATE TABLE users (
    id          BIGSERIAL       PRIMARY KEY,
    name        VARCHAR(100)    NOT NULL,
    email       VARCHAR(50)     NOT NULL UNIQUE CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    password    VARCHAR(100)    NOT NULL,
    role        VARCHAR(10)     NOT NULL CHECK (UPPER(role) IN ('ADMIN', 'USER'))
);

CREATE UNIQUE INDEX idx_users_email ON users(email);

INSERT INTO users (name, email, password, role) VALUES
('Administrador', 'contato@simplesdental.com', 'KMbT%5wT*R!46i@@YHqx', 'ADMIN');

