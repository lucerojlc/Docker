-- init.sql
CREATE DATABASE IF NOT EXISTS sistema_telefonico;
USE sistema_telefonico;

CREATE TABLE IF NOT EXISTS ciudades (
    ciud_id INT PRIMARY KEY AUTO_INCREMENT,
    ciud_nombre VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS personas (
    dir_id INT PRIMARY KEY AUTO_INCREMENT,
    dir_nombre VARCHAR(100) NOT NULL,
    dir_direccion VARCHAR(200),
    dir_tel VARCHAR(20),
    dir_ciud_id INT,
    FOREIGN KEY (dir_ciud_id) REFERENCES ciudades(ciud_id)
);

-- Insertar datos de prueba
INSERT INTO ciudades (ciud_nombre) VALUES 
    ('Bogotá'),
    ('Medellín'),
    ('Cali'),
    ('Pasto');

INSERT INTO personas (dir_nombre, dir_direccion, dir_tel, dir_ciud_id) VALUES 
    ('Juan Pérez', 'Calle 123 #45-67', '3001234567', 1),
    ('María García', 'Carrera 10 #20-30', '3009876543', 2),
    ('Carlos López', 'Avenida 5 #15-25', '3105551234', 3),
    ('Ana Martínez', 'Calle 7 #8-9', '3207778888', 4);