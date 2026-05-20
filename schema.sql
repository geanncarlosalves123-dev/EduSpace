DROP DATABASE IF EXISTS eduspace;

CREATE DATABASE IF NOT EXISTS eduspace
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE eduspace;

-- ============================================================
-- TABELA: usuarios
-- ============================================================
CREATE TABLE IF NOT EXISTS usuarios (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  nome         VARCHAR(120)  NOT NULL,
  email        VARCHAR(120)  NOT NULL UNIQUE,
  senha_hash   VARCHAR(255)  NOT NULL,       -- BCrypt hash
  perfil       ENUM('admin','professor') NOT NULL DEFAULT 'professor',
  ativo        TINYINT(1)    NOT NULL DEFAULT 1,
  criado_em    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  atualizado_em DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================================
-- TABELA: espacos
-- ============================================================
CREATE TABLE IF NOT EXISTS espacos (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  codigo       VARCHAR(20)   NOT NULL UNIQUE,   -- ex: lab1, aud, sala2
  nome         VARCHAR(120)  NOT NULL,
  tipo         ENUM('lab','auditorio','sala','equipamento') NOT NULL,
  capacidade   SMALLINT      NOT NULL DEFAULT 1,
  localizacao  VARCHAR(150),                    -- Bloco A - Térreo
  ativo        TINYINT(1)    NOT NULL DEFAULT 1,
  criado_em    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABELA: agendamentos
-- ============================================================
CREATE TABLE IF NOT EXISTS agendamentos (
  id              INT AUTO_INCREMENT PRIMARY KEY,
  professor_id    INT           NOT NULL,
  espaco_id       INT           NOT NULL,
  sala_identificacao VARCHAR(150) NOT NULL,      -- "Bloco A - Sala 101"
  data_uso        DATE          NOT NULL,
  horario_inicio  TIME          NOT NULL,
  horario_fim     TIME          NOT NULL,
  qtd_aulas       TINYINT       NOT NULL DEFAULT 1,
  qtd_notebooks   SMALLINT      NOT NULL DEFAULT 0,
  qtd_tablets     SMALLINT      NOT NULL DEFAULT 0,
  observacoes     TEXT,
  problema_relatado TEXT,                        -- preenchido ao liberar
  status          ENUM('confirmado','em_uso','liberado','cancelado') NOT NULL DEFAULT 'confirmado',
  confirmado_em   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  iniciado_em     DATETIME      NULL,
  liberado_em     DATETIME      NULL,
  cancelado_em    DATETIME      NULL,

  CONSTRAINT fk_ag_professor FOREIGN KEY (professor_id) REFERENCES usuarios(id) ON DELETE RESTRICT,
  CONSTRAINT fk_ag_espaco    FOREIGN KEY (espaco_id)    REFERENCES espacos(id)  ON DELETE RESTRICT,
  INDEX idx_ag_data     (data_uso),
  INDEX idx_ag_status   (status),
  INDEX idx_ag_espaco   (espaco_id, data_uso),
  INDEX idx_ag_professor(professor_id, data_uso)
);

-- ============================================================
-- DADOS REAIS — Usuários
-- ============================================================
INSERT INTO usuarios (nome, email, senha_hash, perfil) VALUES

('Admin Carlos',       'admin@escola.com', '$2a$12$placeholder_hash_carlos', 'admin'),
('Prof. Adriano Mafra' , 'adriano.mafra@escola.com', '$2b$12$7VFOTieDXzlmHZe8vyvB..Leg6qTxsk5Eqn55cZuNcZTE5kdDjDMG', 'professor'),
('Prof. Alexandra de Alcantara Damaceno', 'alexandra.damaceno@escola.com', '$2b$12$ON7u1ZN5T1wYKel4uH5NRe/lu4n9QgexuS92/2fXRsoQw4g8feKXK', 'professor'),
('Prof. Ana Cristina Magalhães Novais', 'ana.novais@escola.com', '$2b$12$us5BdbZS8s3PFfjfc/VMSe30nwmn1se21AxN6QJbQ6fsQ.yRTP6Sm', 'professor'),
('Prof. Ana Paula Neta', 'ana.neta@escola.com', '$2b$12$YmezOIiBNGYPIfCLHPigYOo79pCi64pOFgKBxzOm.AGQh6Mfo30a6', 'professor'),
('Prof. Anderson dos Santos Leal', 'anderson.leal@escola.com', '$2b$12$Ke0Q0W5F4l1sM9Zlmj4YFeGQh4bX8k2jI5J3M3JTF5Yh1nQhM7s7K', 'professor'),
('Prof. Antônio Ygor Evangelista dos Santos', 'antonio.santos@escola.com', '$2b$12$Q2YlE5A5N5x6R/0kLgAq2eQ3If5G5hV0A7G9pJv4yM1xQjM0mY9B2', 'professor'),
('Prof. Carlos Alberto da Silva', 'carlos.silva@escola.com', '$2b$12$W4Y7fU6hR9nJk3L0uDqV4eM7nF9oP2qA1bS5xD8gH3kL7mN2rT4yC', 'professor'),
('Prof. Caroline Ramos Araújo', 'caroline.araujo@escola.com', '$2b$12$kJ4mN7vQ2pR5tY8uW1xZ3aB6cD9eF2gH5iJ8kL1mN4oP7qR0sT3uV', 'professor'),
('Prof. Daniel Rosendo da Silva', 'daniel.silva@escola.com', '$2b$12$P7oI4uY2tR5eW8qA1sD3fG6hJ9kL2zX5cV8bN1mQ4wE7rT0yU3iO', 'professor'),
('Prof. Danielle Souza Melo', 'danielle.melo@escola.com', '$2b$12$L9mN2bV5cX8zA1sD4fG7hJ0kQ3wE6rT9yU2iO5pP8aS1dF4gH7jK', 'professor'),
('Prof. Denio Fernandes Verneque', 'denio.verneque@escola.com', '$2b$12$H2jK5lM8nB1vC4xZ7aS0dF3gH6jK9lQ2wE5rT8yU1iO4pP7aS0dF', 'professor'),
('Prof. Eliana Alves de Abreu Souza', 'eliana.souza@escola.com', '$2b$12$M4nB7vC0xZ3aS6dF9gH2jK5lQ8wE1rT4yU7iO0pP3aS6dF9gH2jK', 'professor'),
('Prof. Erike Santos Aristides', 'erike.aristides@escola.com', '$2b$12$R5tY8uI1oP4aS7dF0gH3jK6lQ9wE2rT5yU8iO1pP4aS7dF0gH3jK', 'professor'),
('Prof. Everton Ferreira Fernandes', 'everton.fernandes@escola.com', '$2b$12$T6yU9iO2pP5aS8dF1gH4jK7lQ0wE3rT6yU9iO2pP5aS8dF1gH4jK', 'professor'),
('Prof. Federico Traveria Lujan', 'federico.lujan@escola.com', '$2b$12$U7iO0pP3aS6dF9gH2jK5lQ8wE1rT4yU7iO0pP3aS6dF9gH2jK5lQ', 'professor'),
('Prof. Gisele Gualberto de Oliveira', 'gisele.oliveira@escola.com', '$2b$12$V8oP1aS4dF7gH0jK3lQ6wE9rT2yU5iO8pP1aS4dF7gH0jK3lQ6wE', 'professor'),
('Prof. Jakson Nascimento Silva', 'jakson.silva@escola.com', '$2b$12$W9pP2aS5dF8gH1jK4lQ7wE0rT3yU6iO9pP2aS5dF8gH1jK4lQ7wE', 'professor'),
('Prof. Joana Papacidero Correia dos Santos', 'joana.santos@escola.com', '$2b$12$X0aS3dF6gH9jK2lQ5wE8rT1yU4iO7pP0aS3dF6gH9jK2lQ5wE8rT', 'professor'),
('Prof. Jussara Edith Celecina de Almeida', 'jussara.almeida@escola.com', '$2b$12$Y1dF4gH7jK0lQ3wE6rT9yU2iO5pP8aS1dF4gH7jK0lQ3wE6rT9yU', 'professor'),
('Prof. Kaue Sousa Gomes', 'kaue.gomes@escola.com', '$2b$12$Z2gH5jK8lQ1wE4rT7yU0iO3pP6aS9dF2gH5jK8lQ1wE4rT7yU0iO', 'professor'),
('Prof. Kelly Cristina Moreira de Souza', 'kelly.souza@escola.com', '$2b$12$aS3dF6gH9jK2lQ5wE8rT1yU4iO7pP0aS3dF6gH9jK2lQ5wE8rT', 'professor'),
('Prof. Lenilde Fortunata Marques', 'lenilde.marques@escola.com', '$2b$12$bF4gH7jK0lQ3wE6rT9yU2iO5pP8aS1dF4gH7jK0lQ3wE6rT9yU', 'professor'),
('Prof. Lidyane V. Santos', 'lidyane.santos@escola.com', '$2b$12$cG5hJ8kL1mN4oP7qR0sT3uV6wX9yZ2aB5cD8eF1gH4iJ7kL0mN', 'professor'),
('Prof. Maria Claudia Ricardo', 'maria.ricardo@escola.com', '$2b$12$dH6jK9lQ2wE5rT8yU1iO4pP7aS0dF3gH6jK9lQ2wE5rT8yU1iO', 'professor'),
('Prof. Maria Larissa Lira Gomes', 'maria.gomes@escola.com', '$2b$12$eI7kL0mN3bV6cX9zA2sD5fG8hJ1kL4mN7bV0cX3zA6sD9fG2hJ', 'professor'),
('Prof. Marília de Jesus Ferreira Almeida', 'marilia.almeida@escola.com', '$2b$12$fJ8lQ1wE4rT7yU0iO3pP6aS9dF2gH5jK8lQ1wE4rT7yU0iO3pP', 'professor'),
('Prof. Marli da Silva Lacerda de Oliveira', 'marli.oliveira@escola.com', '$2b$12$gK9mN2bV5cX8zA1sD4fG7hJ0kQ3wE6rT9yU2iO5pP8aS1dF4gH', 'professor'),
('Prof. Marcia Fernandes da Silva', 'marcia.silva@escola.com', '$2b$12$hL0nQ3wE6rT9yU2iO5pP8aS1dF4gH7jK0lQ3wE6rT9yU2iO5pP', 'professor'),
('Prof. Marcondes Batista dos Santos', 'marcondes.santos@escola.com', '$2b$12$iM1oP4aS7dF0gH3jK6lQ9wE2rT5yU8iO1pP4aS7dF0gH3jK6lQ', 'professor'),
('Prof. Rosangela Santana', 'rosangela.santana@escola.com', '$2b$12$jN2pP5aS8dF1gH4jK7lQ0wE3rT6yU9iO2pP5aS8dF1gH4jK7lQ', 'professor'),
('Prof. Valéria Cristina Fagundes', 'valeria.fagundes@escola.com', '$2b$12$kO3qR6tY9uI2oP5aS8dF1gH4jK7lQ0wE3rT6yU9iO2pP5aS8dF', 'professor'),
('Prof. Vanessa Alves dos Santos', 'vanessa.santos@escola.com', '$2b$12$lP4rT7yU0iO3pP6aS9dF2gH5jK8lQ1wE4rT7yU0iO3pP6aS9dF', 'professor'),
('Prof. Vanzineia Lopes da Silva', 'vanzineia.silva@escola.com', '$2b$12$mQ5sU8iO1pP4aS7dF0gH3jK6lQ9wE2rT5yU8iO1pP4aS7dF0gH', 'professor'),
('Prof. Vivian Lucinda da Cruz', 'vivian.cruz@escola.com', '$2b$12$nR6tV9oP2aS5dF8gH1jK4lQ7wE0rT3yU6iO9pP2aS5dF8gH1jK', 'professor'),
('Prof. Vinicius Marques Carneiro', 'vinicius.carneiro@escola.com', '$2b$12$oS7uW0pP3aS6dF9gH2jK5lQ8wE1rT4yU7iO0pP3aS6dF9gH2jK', 'professor'),
('Prof. Taina Joyce Silva', 'taina.silva@escola.com', '$2b$12$pT8vX1qR4bS7dF0gH3jK6lQ9wE2rT5yU8iO1pP4aS7dF0gH3jK', 'professor'),
('Prof. Douglas Filipe Barbosa', 'douglas.barbosa@escola.com', '$2b$12$qU9wY2rT5cV8eF1gH4jK7lQ0wE3rT6yU9iO2pP5aS8dF1gH4jK', 'professor'),
('Prof. Israel Biork Torres de Oliveira', 'israel.oliveira@escola.com', '$2b$12$rV0xZ3sU6dW9fG2hJ5kL8mN1oP4qR7tY0uI3oP6aS9dF2gH5jK', 'professor'),
('Prof. Sylvio Lula de Oliveira', 'sylvio.oliveira@escola.com', '$2b$12$sW1yA4tV7eX0gH3jK6lQ9wE2rT5yU8iO1pP4aS7dF0gH3jK6lQ', 'professor'),
('Prof. Sthefany Reis Santos Felix', 'sthefany.felix@escola.com', '$2b$12$tX2zB5uW8fY1hJ4kL7mN0oP3qR6tV9oP2aS5dF8gH1jK4lQ7wE', 'professor'),
('Prof. Priscila Almeida Monteagudo', 'priscila.monteagudo@escola.com', '$2b$12$uY3aC6vX9gZ2iK5lM8nO1pQ4rT7uW0pP3aS6dF9gH2jK5lQ8wE', 'professor'),
('Prof. Luciene Aparecida Vieira de Andrade', 'luciene.andrade@escola.com', '$2b$12$vZ4bD7wY0hA3jL6mN9oP2qR5tV8xZ1qR4bS7dF0gH3jK6lQ9wE', 'professor'),
('Prof. Clayton', 'clayton@escola.com', '$2b$12$ea1NZzyiiWgh5/dip2bE.O23XzQQK0RBZmDmJb6guWVkIFxZxMO5u', 'professor');

-- ============================================================
-- DADOS INICIAIS — Espaços
-- ============================================================
INSERT INTO espacos (codigo, nome, tipo, capacidade, localizacao) VALUES
('lab1',   'Laboratório de Informática 1', 'lab',          30,  ' 1º Andar'),
('lab2',    'Laboratorio de Ciências',          'lab',    200, 'Terreo'),
('aud1',  'Anfiteatro',            'auditorio',         35,  'Terreo'),
('bibli',  'Biblioteca',            'sala',         35,  '2º Andar'),
('sala1', 'Sala de Inglês',          'sala',  1, ' 1º Andar'  ),
('sala2', 'Sala de Recursos',          'sala',  1, ' terreo'  );

-- ============================================================
-- VIEW útil: vw_agendamentos_detalhado
-- ============================================================
CREATE OR REPLACE VIEW vw_agendamentos_detalhado AS
SELECT
  a.id,
  u.nome              AS professor_nome,
  u.email             AS professor_email,
  e.nome              AS espaco_nome,
  e.tipo              AS espaco_tipo,
  a.sala_identificacao,
  a.data_uso,
  a.horario_inicio,
  a.horario_fim,
  a.qtd_aulas,
  a.qtd_notebooks,
  a.qtd_tablets,
  a.observacoes,
  a.problema_relatado,
  a.status,
  a.confirmado_em,
  a.iniciado_em,
  a.liberado_em,
  a.cancelado_em
FROM agendamentos a
JOIN usuarios u ON u.id = a.professor_id
JOIN espacos  e ON e.id = a.espaco_id;

-- ============================================================
-- PROCEDURE: sp_relatorio_semanal
-- Retorna todos os agendamentos da semana corrente
-- ============================================================
DELIMITER $$
CREATE PROCEDURE sp_relatorio_semanal()
BEGIN
  SELECT
    a.id,
    u.nome              AS professor,
    e.nome              AS espaco,
    a.sala_identificacao AS sala,
    a.data_uso,
    a.horario_inicio,
    a.horario_fim,
    a.qtd_aulas,
    a.qtd_notebooks,
    a.qtd_tablets,
    a.problema_relatado AS problema,
    a.status
  FROM agendamentos a
  JOIN usuarios u ON u.id = a.professor_id
  JOIN espacos  e ON e.id = a.espaco_id
  WHERE a.data_uso BETWEEN
        DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY)
    AND DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 6 DAY)
  ORDER BY a.data_uso, a.horario_inicio;
END$$
DELIMITER ;

USE eduspace;

SELECT * FROM usuarios;
SELECT * FROM espacos;
SELECT * FROM agendamentos;