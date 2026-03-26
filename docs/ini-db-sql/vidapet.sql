-- ============================================
-- Script de inicialización de la base de datos
-- Nombre de la base de datos: vidapet
-- ============================================

-- 1. Eliminar la base de datos si existía
DROP DATABASE IF EXISTS vidapet;

-- 2. Crear la base de datos
CREATE DATABASE vidapet;

-- 3. Usar la base de datos
USE vidapet;

-- ============================================
-- Tabla: propietario
-- ============================================
CREATE TABLE propietario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100)
);

-- ============================================
-- Tabla: mascota
-- ============================================
CREATE TABLE mascota (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL,
  especie VARCHAR(50),
  raza VARCHAR(50),
  fecha_nacimiento DATE,
  propietario_id INT,
  CONSTRAINT fk_mascota_propietario FOREIGN KEY (propietario_id)
      REFERENCES propietario(id)
      ON DELETE SET NULL
      ON UPDATE CASCADE
);

-- ============================================
-- Tabla: consulta
-- ============================================
CREATE TABLE consulta (
   id INT AUTO_INCREMENT PRIMARY KEY,
   fecha DATETIME NOT NULL,
   motivo VARCHAR(255),
   diagnostico VARCHAR(255),
   mascota_id INT,
   CONSTRAINT fk_consulta_mascota FOREIGN KEY (mascota_id)
       REFERENCES mascota(id)
       ON DELETE SET NULL
       ON UPDATE CASCADE
);

-- ============================================
-- Tabla: tratamiento
-- ============================================
CREATE TABLE tratamiento (
   id INT AUTO_INCREMENT PRIMARY KEY,
   tipo VARCHAR(50) NOT NULL,
   duracion VARCHAR(50),
   consulta_id INT,
   CONSTRAINT fk_tratamiento_consulta FOREIGN KEY (consulta_id)
       REFERENCES consulta(id)
       ON DELETE CASCADE
       ON UPDATE CASCADE
);

-- ============================================
-- Datos de ejemplo (opcional)
-- ============================================
INSERT INTO propietario(nombre, apellido, telefono, email)
VALUES 
('Juan', 'Pérez', '555-1234', 'juan@example.com'),
('María', 'García', '555-5678', 'maria@example.com');

INSERT INTO mascota(nombre, especie, raza, edad, propietario_id)
VALUES
('Firulais', 'Perro', 'Labrador', 3, 1),
('Michi', 'Gato', 'Siamés', 2, 2);

INSERT INTO consulta(fecha, motivo, diagnostico, mascota_id)
VALUES
(NOW(), 'Chequeo general', 'Saludable', 1),
(NOW(), 'Vacuna', 'Vacunado', 2);

INSERT INTO tratamiento(tipo, duracion, consulta_id)
VALUES
('Vacuna antirrábica', '1 día', 2),
('Desparasitación', '2 días', 1);