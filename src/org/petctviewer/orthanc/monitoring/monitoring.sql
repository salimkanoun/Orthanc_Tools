-- phpMyAdmin SQL Dump
-- version 4.5.4.1deb2ubuntu2
-- http://www.phpmyadmin.net
--
-- Client :  localhost
-- Généré le :  Sam 23 Juin 2018 à 14:31
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
  `Last_Name` varchar(70) DEFAULT NULL,
  `First_Name` varchar(70) DEFAULT NULL,
  `Patient_ID` varchar(32) NOT NULL,
  `DOB` varchar(8) DEFAULT NULL,
  `Sex` varchar(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `series`
--

CREATE TABLE `series` (
  `size` double DEFAULT NULL,
  `age` varchar(32) DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `Manifacturer` varchar(32) DEFAULT NULL,
  `Manifacturer_Model` varchar(32) DEFAULT NULL,
  `Performing_Physician_Name` varchar(32) DEFAULT NULL,
  `Series_Description` varchar(32) DEFAULT NULL,
  `Station_Name` varchar(32) DEFAULT NULL,
  `Content_Date` varchar(8) DEFAULT NULL,
  `Content_Time` varchar(15) DEFAULT NULL,
  `Protocol_Name` varchar(32) DEFAULT NULL,
  `Series_Instance_UID` varchar(100) NOT NULL,
  `Comment_Radiation_Dose` text,
  `Radiopharmaceutical_sequence` text,
  `Radiopharmaceutical` varchar(32) DEFAULT NULL,
  `RadiopharmaceuticalStartTime` varchar(32) DEFAULT NULL,
  `RadionuclideTotalDose` varchar(32) DEFAULT NULL,
  `RadionuclideHalfLife` varchar(32) DEFAULT NULL,
  `RadionuclidePositronFraction` varchar(32) DEFAULT NULL,
  `Radiation_Dose_Module` text,
  `shared_Tags` text,
  `Orthanc_Serie_ID` varchar(70) NOT NULL,
  `parentStudyOrthanc` varchar(70) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `studies`
--

CREATE TABLE `studies` (
  `accessionNumber` varchar(35) DEFAULT NULL,
  `institutionName` varchar(35) DEFAULT NULL,
  `referringPhysicianName` varchar(35) DEFAULT NULL,
  `studyDate` varchar(35) DEFAULT NULL,
  `studyDescription` varchar(35) DEFAULT NULL,
  `studyID` varchar(35) DEFAULT NULL,
  `studyInstanceUID` varchar(100) NOT NULL,
  `studyTime` varchar(35) DEFAULT NULL,
  `Orthanc_Study_ID` varchar(70) NOT NULL,
  `parentPatientOrthanc` varchar(70) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Index pour les tables exportées
--

--
-- Index pour la table `patients`
--
ALTER TABLE `patients`
  ADD PRIMARY KEY (`Patient_ID`),
  ADD UNIQUE KEY `Orthanc_Patient_ID_2` (`Orthanc_Patient_ID`),
  ADD KEY `Orthanc_Patient_ID` (`Orthanc_Patient_ID`);

--
-- Index pour la table `series`
--
ALTER TABLE `series`
  ADD PRIMARY KEY (`Series_Instance_UID`),
  ADD KEY `parentStudyUID` (`parentStudyOrthanc`);

--
-- Index pour la table `studies`
--
ALTER TABLE `studies`
  ADD PRIMARY KEY (`studyInstanceUID`),
  ADD UNIQUE KEY `Orthanc_Study_ID_2` (`Orthanc_Study_ID`),
  ADD KEY `parentPatientID` (`parentPatientOrthanc`),
  ADD KEY `Orthanc_Study_ID` (`Orthanc_Study_ID`);

--
-- Contraintes pour les tables exportées
--

--
-- Contraintes pour la table `series`
--
ALTER TABLE `series`
  ADD CONSTRAINT `parentStudy` FOREIGN KEY (`parentStudyOrthanc`) REFERENCES `studies` (`Orthanc_Study_ID`);

--
-- Contraintes pour la table `studies`
--
ALTER TABLE `studies`
  ADD CONSTRAINT `parentPatient` FOREIGN KEY (`parentPatientOrthanc`) REFERENCES `patients` (`Orthanc_Patient_ID`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
