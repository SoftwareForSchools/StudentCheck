-- phpMyAdmin SQL Dump
-- version 4.7.5
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generated on: 19 apr 2018 om 08:22
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
-- Tablestructure for table `Bedrijfspunten`
--

CREATE TABLE `Bedrijfspunten` (
  `Studentnummer` int(11) NOT NULL,
  `StudentNaam` varchar(50) NOT NULL,
  `AantalKeerGeweest` int(11) NOT NULL,
  `AantalBedrijfsuren` int(11) NOT NULL,
  `AantalBedrijfspunten` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Info for exported table `Bedrijfspunten`
--

INSERT INTO `Bedrijfspunten` (`Studentnummer`, `StudentNaam`, `AantalKeerGeweest`, `AantalBedrijfsuren`, `AantalBedrijfspunten`) VALUES
(123456789, '', 1, 4, 0.1),
(098765432, '', 2, 8, 0.3);

--
-- Index for table `Bedrijfspunten`
--
ALTER TABLE `Bedrijfspunten`
  ADD PRIMARY KEY (`Studentnummer`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
