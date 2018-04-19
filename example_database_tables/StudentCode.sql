-- phpMyAdmin SQL Dump
-- version 4.7.5
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generated on: 19 apr 2018 om 08:23
-- Server version: 5.6.30
-- PHP-version: 7.2.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: ``
--

-- --------------------------------------------------------

--
-- Tablestructure for table `StudentCode`
--

CREATE TABLE `StudentCode` (
  `StudentCode` int(11) NOT NULL,
  `Serial` varchar(20) NOT NULL,
  `DatumGemaakt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Info for exported table `StudentCode`
--

INSERT INTO `StudentCode` (`StudentCode`, `Serial`, `DatumGemaakt`) VALUES
(123456789, 'example_code', '2018-04-04 14:30:19'),
(098765432, 'student_code', '2018-04-04 14:30:19');

--
-- Index for table `StudentCode`
--
ALTER TABLE `StudentCode`
  ADD PRIMARY KEY (`Serial`),
  ADD UNIQUE KEY `Serial` (`Serial`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
