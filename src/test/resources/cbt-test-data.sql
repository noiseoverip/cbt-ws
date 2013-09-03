-- phpMyAdmin SQL Dump
-- version 3.5.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Sep 01, 2013 at 03:31 PM
-- Server version: 5.5.24-log
-- PHP Version: 5.3.13

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `cbt`
--

--
-- Dumping data for table `testscript`
--

INSERT INTO `testscript` (`id`, `name`, `path`, `file_name`, `user_id`, `classes`, `updated`) VALUES
(1, 'testscript1', 'magicpath', 'magicfilename', 1, NULL, '2013-09-01 12:14:19');

--
-- Dumping data for table `testtarget`
--

INSERT INTO `testtarget` (`id`, `name`, `user_id`, `path`, `file_name`, `updated`, `metadata`) VALUES
(1, 'testtarget1', 1, 'magicpath', 'magicfilename', '2013-09-01 12:14:24', NULL);

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id`, `name`, `password`) VALUES
(1, 'testuser1', '41da76f0fc3ec62a6939e634bfb6a342'),
(2, 'testuser2', '58dd024d49e1d1b83a5d307f09f32734');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
