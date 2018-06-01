-- phpMyAdmin SQL Dump
-- version 4.5.4.1deb2ubuntu2
-- http://www.phpmyadmin.net
--
-- Client :  localhost
-- Généré le :  Ven 18 Mai 2018 à 23:58
-- Version du serveur :  5.7.22-0ubuntu0.16.04.1
-- Version de PHP :  7.0.30-0ubuntu0.16.04.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données :  `monitoring`
--

-- --------------------------------------------------------

--
-- Structure de la table `patients`
--

CREATE TABLE `patients` (
  `Orthanc_Patient_ID` varchar(70) NOT NULL,
  `Last_Name` varchar(70) NOT NULL,
  `First_Name` varchar(70) NOT NULL,
  `Patient_ID` varchar(32) NOT NULL,
  `DOB` varchar(8) NOT NULL,
  `Sex` varchar(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `series`
--

CREATE TABLE `series` (
  `size` double NOT NULL,
  `age` double NOT NULL,
  `weight` double NOT NULL,
  `Manifacturer` varchar(32) NOT NULL,
  `Manifacturer_Model` varchar(32) NOT NULL,
  `Performing_Physician_Name` varchar(32) NOT NULL,
  `Series_Description` varchar(32) NOT NULL,
  `Station_Name` varchar(32) NOT NULL,
  `Content_Date` varchar(8) NOT NULL,
  `Content_Time` varchar(15) NOT NULL,
  `Protocol_Name` varchar(32) NOT NULL,
  `Series_Instance_UID` varchar(70) NOT NULL,
  `Comment_Radiation_Dose` text NOT NULL,
  `Radiopharmaceutical_sequence` text NOT NULL,
  `Radiopharmaceutical` varchar(32) NOT NULL,
  `RadiopharmaceuticalStartTime` varchar(32) NOT NULL,
  `RadionuclideTotalDose` varchar(32) NOT NULL,
  `RadionuclideHalfLife` varchar(32) NOT NULL,
  `RadionuclidePositronFraction` varchar(32) NOT NULL,
  `Radiation_Dose_Module` text NOT NULL,
  `shared_Tags` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `studies`
--

CREATE TABLE `studies` (
  `accessionNumber` varchar(35) NOT NULL,
  `institutionName` varchar(35) NOT NULL,
  `referringPhysicianName` varchar(35) NOT NULL,
  `studyDate` varchar(35) NOT NULL,
  `studyDescription` varchar(35) NOT NULL,
  `studyID` varchar(35) NOT NULL,
  `studyInstanceUID` varchar(70) NOT NULL,
  `studyTime` varchar(35) NOT NULL,
  `Orthanc_Study_ID` varchar(70) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Index pour les tables exportées
--

--
-- Index pour la table `patients`
--
ALTER TABLE `patients`
  ADD PRIMARY KEY (`Patient_ID`);

--
-- Index pour la table `series`
--
ALTER TABLE `series`
  ADD PRIMARY KEY (`Series_Instance_UID`);

--
-- Index pour la table `studies`
--
ALTER TABLE `studies`
  ADD PRIMARY KEY (`studyInstanceUID`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
