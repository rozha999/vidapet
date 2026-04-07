-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS vidapet;
USE vidapet;

-- =======================
-- Tabla Propietario
-- =======================
CREATE TABLE propietario (
 id INT AUTO_INCREMENT PRIMARY KEY,
 nombre VARCHAR(100) NOT NULL,
 apellido VARCHAR(100) NOT NULL,
 telefono VARCHAR(20),
 email VARCHAR(100)
);

-- =======================
-- Tabla Mascota
-- =======================
CREATE TABLE mascota (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  especie VARCHAR(50),
  raza VARCHAR(50),
  fecha_nacimiento DATE,
  propietario_id INT NOT NULL,
  FOREIGN KEY (propietario_id) REFERENCES propietario(id) ON DELETE CASCADE
);

-- =======================
-- Tabla Tratamiento
-- =======================
CREATE TABLE tratamiento (
   id INT AUTO_INCREMENT PRIMARY KEY,
   nombre VARCHAR(100) NOT NULL
);

-- =======================
-- Tabla HistorialMedico
-- =======================
CREATE TABLE HistorialMedico (
  id INT AUTO_INCREMENT PRIMARY KEY,
  mascota_id INT NOT NULL,
  fecha DATE NOT NULL,
  problema VARCHAR(255),
  tratamiento_id INT,
  nombre_tratamiento VARCHAR(100),
  notas TEXT,
  FOREIGN KEY (mascota_id) REFERENCES mascota(id) ON DELETE CASCADE,
  FOREIGN KEY (tratamiento_id) REFERENCES tratamiento(id) ON DELETE SET NULL
);

-- =======================
-- Tabla Cita
-- =======================
CREATE TABLE cita (
  id INT AUTO_INCREMENT PRIMARY KEY,
  mascota_id INT NOT NULL,
  fecha DATE NOT NULL,
  hora TIME NOT NULL,
  estado VARCHAR(50),
  notas TEXT,
  FOREIGN KEY (mascota_id) REFERENCES mascota(id) ON DELETE CASCADE
);

USE vidapet;

-- =======================
-- Datos de prueba para Propietario
-- =======================
INSERT INTO propietario (nombre, apellido, telefono, email) VALUES
  ('Juan', 'Perez', '555-1234', 'juan.perez@email.com'),
  ('Maria', 'Lopez', '555-5678', 'maria.lopez@email.com'),
  ('Carlos', 'Gomez', NULL, 'carlos.gomez@email.com');

-- =======================
-- Datos de prueba para Mascota
-- =======================
INSERT INTO mascota (nombre, especie, raza, fecha_nacimiento, propietario_id) VALUES
   ('Firulais', 'Perro', 'Labrador', '2018-05-12', 1),
   ('Michi', 'Gato', 'Siames', '2020-03-22', 2),
   ('Rex', 'Perro', 'Pastor Alemán', '2019-11-10', 3);

-- =======================
-- Datos de prueba para Tratamiento
-- =======================
INSERT INTO tratamiento (nombre) VALUES
    ('Vacuna Antirrábica'),
    ('Desparasitación'),
    ('Chequeo General');

-- =======================
-- Datos de prueba para HistorialMedico
-- =======================
INSERT INTO HistorialMedico (mascota_id, fecha, problema, tratamiento_id, nombre_tratamiento, notas) VALUES
   (1, '2023-01-15', 'Fiebre', 3, 'Chequeo General', 'Recuperación completa'),
   (2, '2023-02-20', 'Pulgas', 2, 'Desparasitación', 'Aplicar champú antipulgas'),
   (3, '2023-03-05', 'Vacuna anual', 1, 'Vacuna Antirrábica', 'Siguiente vacuna en 1 año');

-- =======================
-- Datos de prueba para Cita
-- =======================
INSERT INTO cita (mascota_id, fecha, hora, estado, notas) VALUES
    (1, '2023-04-10', '10:00:00', 'Pendiente', 'Primera consulta'),
    (2, '2023-04-11', '14:30:00', 'Confirmada', 'Revisión general'),
    (3, '2023-04-12', '09:00:00', 'Cancelada', 'Cita reprogramada');