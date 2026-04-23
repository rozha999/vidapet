-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 23, 2026 at 05:04 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `vidapet`
--
CREATE DATABASE IF NOT EXISTS vidapet;
USE vidapet;

-- --------------------------------------------------------

--
-- Table structure for table `cita`
--

CREATE TABLE `cita` (
  `id` int(11) NOT NULL,
  `mascota_id` int(11) NOT NULL,
  `propietario_id` int(11) NOT NULL,
  `fecha` datetime NOT NULL,
  `nota` text DEFAULT NULL,
  `estado` varchar(20) DEFAULT NULL,
  `consulta_id` int(11) DEFAULT NULL,
  `veterinario_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `cita`
--

INSERT INTO `cita` (`id`, `mascota_id`, `propietario_id`, `fecha`, `nota`, `estado`, `consulta_id`, `veterinario_id`) VALUES
(35, 13, 4, '2026-04-08 22:20:00', 'Tiene fiebre muy alta', 'ATENDIDO', 42, 1),
(37, 14, 1, '2026-04-10 23:26:00', 'ggjhgjh', 'ATENDIDO', 44, 1);

-- --------------------------------------------------------

--
-- Table structure for table `consulta`
--

CREATE TABLE `consulta` (
  `id` int(11) NOT NULL,
  `diagnostico` varchar(255) DEFAULT NULL,
  `cita_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `consulta`
--

INSERT INTO `consulta` (`id`, `diagnostico`, `cita_id`) VALUES
(42, 'Tiene fiebre muy alta', 35),
(44, 'fthjfgthgf', 37);

-- --------------------------------------------------------

--
-- Table structure for table `especie`
--

CREATE TABLE `especie` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `foto` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `especie`
--

INSERT INTO `especie` (`id`, `nombre`, `foto`) VALUES
(4, 'Perro', 'uploads/1776699421343_perro.jpg'),
(5, 'Gato', 'uploads/1776700353273_gatoo.jpg'),
(6, 'Ave', 'uploads/1776700091162_ave.jpg'),
(7, 'Pez', 'uploads/1776700127799_pez.png'),
(8, 'Conejo', 'uploads/1776700195057_conejo.jpg'),
(9, 'Tortuga', 'uploads/1776700223252_tortuga.jpg'),
(10, 'Reptil', 'uploads/1776700279321_reptil.jpg'),
(11, 'Hámster', 'uploads/1776700316507_Hámster.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `mascota`
--

CREATE TABLE `mascota` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `raza` varchar(50) DEFAULT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `propietario_id` int(11) NOT NULL,
  `foto` varchar(255) DEFAULT NULL,
  `especie_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `mascota`
--

INSERT INTO `mascota` (`id`, `nombre`, `raza`, `fecha_nacimiento`, `propietario_id`, `foto`, `especie_id`) VALUES
(13, 'jack', 'persiancat', '2026-04-17', 4, 'uploads/1776878573465_gatoo.jpg', 5),
(14, 'nini', 'bahkuki', '2026-04-22', 1, 'uploads/1776705983491_Hámster.jpg', 11);

-- --------------------------------------------------------

--
-- Table structure for table `propietario`
--

CREATE TABLE `propietario` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `propietario`
--

INSERT INTO `propietario` (`id`, `nombre`, `apellido`, `telefono`, `email`) VALUES
(1, 'Luis', 'García', '09120000001', 'luis@example.com'),
(2, 'Ana', 'Martínez', '09120000002', 'ana@example.com'),
(4, 'Sofía', 'López', '09120000004', 'sofia@example.com'),
(5, 'Miguel', 'Pérez', '09120000005', 'miguel@example.com');

-- --------------------------------------------------------

--
-- Table structure for table `tratamiento`
--

CREATE TABLE `tratamiento` (
  `id` int(11) NOT NULL,
  `consulta_id` int(11) NOT NULL,
  `tratamiento` varchar(255) NOT NULL,
  `fecha_inicio` date DEFAULT NULL,
  `fecha_fin` date DEFAULT NULL,
  `observaciones` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tratamiento`
--

INSERT INTO `tratamiento` (`id`, `consulta_id`, `tratamiento`, `fecha_inicio`, `fecha_fin`, `observaciones`) VALUES
(23, 42, 'vacuna132', '2026-04-22', '2026-04-21', 'tiene que descansar 10 dias'),
(24, 42, 'pastilla ', '2026-04-22', '2026-04-29', 'cada dia toma una'),
(26, 44, 'ghfgh', '2026-04-23', '2026-04-30', 'fghfth');

-- --------------------------------------------------------

--
-- Table structure for table `veterinario`
--

CREATE TABLE `veterinario` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) NOT NULL,
  `telefono` varchar(30) DEFAULT NULL,
  `correo` varchar(120) DEFAULT NULL,
  `especialidad` varchar(100) DEFAULT NULL,
  `codigo_colegiado` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `veterinario`
--

INSERT INTO `veterinario` (`id`, `nombre`, `apellido`, `telefono`, `correo`, `especialidad`, `codigo_colegiado`) VALUES
(1, 'carlos', 'juez', '555-5678', 'maria@example.com', 'deramkkfd', '125546656');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `cita`
--
ALTER TABLE `cita`
  ADD PRIMARY KEY (`id`),
  ADD KEY `mascota_id` (`mascota_id`),
  ADD KEY `propietario_id` (`propietario_id`),
  ADD KEY `fk_cita_veterinario` (`veterinario_id`);

--
-- Indexes for table `consulta`
--
ALTER TABLE `consulta`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_consulta_cita` (`cita_id`);

--
-- Indexes for table `especie`
--
ALTER TABLE `especie`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `mascota`
--
ALTER TABLE `mascota`
  ADD PRIMARY KEY (`id`),
  ADD KEY `propietario_id` (`propietario_id`),
  ADD KEY `fk_mascota_especie` (`especie_id`);

--
-- Indexes for table `propietario`
--
ALTER TABLE `propietario`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tratamiento`
--
ALTER TABLE `tratamiento`
  ADD PRIMARY KEY (`id`),
  ADD KEY `consulta_id` (`consulta_id`);

--
-- Indexes for table `veterinario`
--
ALTER TABLE `veterinario`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `codigo_colegiado` (`codigo_colegiado`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `cita`
--
ALTER TABLE `cita`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=38;

--
-- AUTO_INCREMENT for table `consulta`
--
ALTER TABLE `consulta`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=45;

--
-- AUTO_INCREMENT for table `especie`
--
ALTER TABLE `especie`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `mascota`
--
ALTER TABLE `mascota`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `propietario`
--
ALTER TABLE `propietario`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `tratamiento`
--
ALTER TABLE `tratamiento`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT for table `veterinario`
--
ALTER TABLE `veterinario`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `cita`
--
ALTER TABLE `cita`
  ADD CONSTRAINT `cita_ibfk_1` FOREIGN KEY (`mascota_id`) REFERENCES `mascota` (`id`),
  ADD CONSTRAINT `cita_ibfk_2` FOREIGN KEY (`propietario_id`) REFERENCES `propietario` (`id`),
  ADD CONSTRAINT `fk_cita_veterinario` FOREIGN KEY (`veterinario_id`) REFERENCES `veterinario` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `consulta`
--
ALTER TABLE `consulta`
  ADD CONSTRAINT `fk_consulta_cita` FOREIGN KEY (`cita_id`) REFERENCES `cita` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `mascota`
--
ALTER TABLE `mascota`
  ADD CONSTRAINT `fk_mascota_especie` FOREIGN KEY (`especie_id`) REFERENCES `especie` (`id`),
  ADD CONSTRAINT `mascota_ibfk_1` FOREIGN KEY (`propietario_id`) REFERENCES `propietario` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `tratamiento`
--
ALTER TABLE `tratamiento`
  ADD CONSTRAINT `tratamiento_ibfk_1` FOREIGN KEY (`consulta_id`) REFERENCES `consulta` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
