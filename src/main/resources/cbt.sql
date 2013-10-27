   -- phpMyAdmin SQL Dump
-- version 4.0.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Sep 21, 2013 at 08:26 PM
-- Server version: 5.6.12-log
-- PHP Version: 5.4.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `cbt`
--
-- --------------------------------------------------------

--
-- Table structure for table `device`
--

CREATE TABLE IF NOT EXISTS `device` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `serial_number` char(255) NOT NULL,
  `owner_id` bigint(20) NOT NULL,
  `device_unique_id` char(255) NOT NULL,
  `device_type_id` bigint(20) NOT NULL,
  `state` enum('ONLINE','OFFLINE') NOT NULL DEFAULT 'OFFLINE',
  `device_os_id` bigint(50) NOT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `deviceunique_id` (`device_unique_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=248 ;

-- --------------------------------------------------------

--
-- Table structure for table `device_job`
--

CREATE TABLE IF NOT EXISTS `device_job` (
  `device_job_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `device_job_device_id` bigint(20) NOT NULL,
  `device_job_testrun_id` bigint(20) NOT NULL,
  `device_job_created` datetime NOT NULL,
  `device_job_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `device_job_status` enum('WAITING','CHECKEDOUT','RUNNING','FINISHED') NOT NULL DEFAULT 'WAITING',
  `device_job_meta` text,
  PRIMARY KEY (`device_job_id`),
  KEY `device_id` (`device_job_device_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

-- --------------------------------------------------------

--
-- Table structure for table `device_job_result`
--

CREATE TABLE IF NOT EXISTS `device_job_result` (
  `devicejobresult_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `devicejob_id` bigint(20) NOT NULL,
  `state` enum('PASSED','FAILED') NOT NULL,
  `tests_run` int(11) NOT NULL,
  `tests_failed` int(11) NOT NULL,
  `tests_errors` int(11) NOT NULL,
  `output` text NOT NULL,
  PRIMARY KEY (`devicejobresult_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `device_os`
--

CREATE TABLE IF NOT EXISTS `device_os` (
  `device_os_id` bigint(11) NOT NULL AUTO_INCREMENT,
  `device_os_name` char(255) NOT NULL,
  PRIMARY KEY (`deviceos_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `device_sharing`
--

CREATE TABLE IF NOT EXISTS `device_sharing` (
  `device_sharing_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `device_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`device_sharing_id`),
  UNIQUE KEY `device_id` (`device_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `device_type`
--

CREATE TABLE IF NOT EXISTS `device_type` (
  `device_type_id` bigint(11) NOT NULL AUTO_INCREMENT,
  `device_type_model` char(100) NOT NULL,
  `device_type_manufacture` char(100) NOT NULL,
  `device_type_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `results`
--

CREATE TABLE IF NOT EXISTS `results` (
  `result_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `state` enum('PASSED','FAILED''','') NOT NULL,
  `metadata` longtext NOT NULL COMMENT 'json string of metadata values',
  `test_id` bigint(20) NOT NULL,
  PRIMARY KEY (`result_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `testconfig`
--

CREATE TABLE IF NOT EXISTS `testconfig` (
  `test_config_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `test_config_name` varchar(255) NOT NULL,
  `user_id` bigint(11) NOT NULL,
  `test_script_id` bigint(20) NOT NULL,
  `test_target_id` bigint(20) NOT NULL,
  `test_profile_id` bigint(11) NOT NULL,
  `metadata` longtext CHARACTER SET utf16 COLLATE utf16_unicode_ci,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`test_config_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=46 ;

-- --------------------------------------------------------

--
-- Table structure for table `testprofile`
--

CREATE TABLE IF NOT EXISTS `testprofile` (
  `testprofile_id` bigint(11) NOT NULL AUTO_INCREMENT,
  `testprofile_user_id` bigint(20) NOT NULL,
  `testprofile_name` varchar(255) NOT NULL,
  `testprofile_mode` enum('NORMAL','FAST') NOT NULL COMMENT 'simple, fast',
  `testprofile_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `testprofile_metadata` longtext,
  PRIMARY KEY (`testprofile_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=14 ;

-- --------------------------------------------------------

--
-- Table structure for table `testprofile_devices`
--

CREATE TABLE IF NOT EXISTS `testprofile_devices` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `testprofile_id` bigint(20) NOT NULL,
  `devicetype_id` bigint(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `testprofile_id` (`testprofile_id`,`devicetype_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=9 ;

-- --------------------------------------------------------

--
-- Table structure for table `testrun`
--

CREATE TABLE IF NOT EXISTS `testrun` (
  `testrun_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `testrun_user_id` bigint(20) NOT NULL,
  `testrun_testconfig_id` bigint(20) NOT NULL,
  `testrun_created` datetime NOT NULL,
  `testrun_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `testrun_status` enum('WAITING','RUNNING','FAILED','PASSED') NOT NULL DEFAULT 'WAITING',
  PRIMARY KEY (`testrun_id`),
  KEY `config_id` (`testrun_testconfig_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=158 ;

-- --------------------------------------------------------

--
-- Table structure for table `testscript`
--

CREATE TABLE IF NOT EXISTS `testscript` (
  `testscript_id` bigint(11) NOT NULL AUTO_INCREMENT,
  `testscript_name` varchar(255) DEFAULT NULL,
  `testscript_type` enum('INSTRUMENTATION','UIAUTOMATOR') NOT NULL,
  `testscript_file_path` char(255) DEFAULT NULL,
  `testscript_file_name` char(255) DEFAULT NULL,
  `testscript_user_id` bigint(11) NOT NULL,
  `testscript_classes` text,
  `testscript_updated` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`testscript_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

-- --------------------------------------------------------

--
-- Table structure for table `testtarget`
--

CREATE TABLE IF NOT EXISTS `testtarget` (
  `testtarget_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `testtarget_name` varchar(255) DEFAULT NULL,
  `testtarget_user_id` bigint(11) NOT NULL,
  `testtarget_file_path` char(255) DEFAULT NULL,
  `testtarget_file_name` char(255) DEFAULT NULL,
  `testtarget_updated` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `testtarget_metadata` text,
  PRIMARY KEY (`testtarget_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=46 ;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `name` char(30) NOT NULL,
  `password` char(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3 ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `testrun`
--
ALTER TABLE `testrun`
  ADD CONSTRAINT `testrun_ibfk_1` FOREIGN KEY (`testrun_testconfig_id`) REFERENCES `testconfig` (`test_config_id`);
