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
-- Tablestructure for table `LoginBU`
--

CREATE TABLE `LoginBU` (
  `id` int(11) NOT NULL,
  `studentnummer` int(11) NOT NULL,
  `checkIn` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `checkUit` timestamp NULL DEFAULT NULL,
  `ToegevoegdBedrijfspunten` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Info for exported table `LoginBU`
--

INSERT INTO `LoginBU` (`id`, `studentnummer`, `checkIn`, `checkUit`, `ToegevoegdBedrijfspunten`) VALUES
(1, 123456789, '2018-04-17 08:38:53', '2018-04-17 10:38:53', 0),
(2, 123456789, '2018-04-18 23:00:34', '2018-04-19 01:00:34', 1),
(3, 098765432, '2018-04-17 23:00:34', '2018-04-18 01:00:34', 1),
(4, 098765432, '2018-04-11 22:44:33', '2018-04-12 00:44:33', 1);

--
-- Index for table `LoginBU`
--
ALTER TABLE `LoginBU`
  ADD PRIMARY KEY (`studentnummer`,`checkIn`),
  ADD KEY `id` (`id`);

--
-- AUTO_INCREMENT for table `LoginBU`
--
ALTER TABLE `LoginBU`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=415;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
