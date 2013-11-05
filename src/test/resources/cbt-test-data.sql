-- phpMyAdmin SQL Dump
-- version 4.0.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Sep 21, 2013 at 06:58 PM
-- Server version: 5.6.12-log
-- PHP Version: 5.4.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Dumping data for table `device`
--

INSERT INTO `device` (`device_id`, `device_serial_number`, `device_owner_id`, `device_unique_id`, `device_type_id`, `device_state`, `device_os_id`, `device_updated`) VALUES
(1, 'demoDeviceSerialNumber', 1, 'a0f750cd775436474261993e4f3d518d', 1, 'OFFLINE', 1, '2013-09-21 16:17:37');

--
-- Dumping data for table `device_job`
--

INSERT INTO `device_job` (`device_job_id`, `device_job_device_id`, `device_job_testrun_id`, `device_job_created`, `device_job_updated`, `device_job_status`, `device_job_meta`) VALUES
(1, 1, 1, '2013-09-13 00:00:00', '2013-09-21 16:16:51', 'WAITING', NULL);

--
-- Dumping data for table `testconfig`
--

INSERT INTO `testconfig` (`test_config_id`, `test_config_name`, `user_id`, `test_script_id`, `test_target_id`, `test_profile_id`, `metadata`, `updated`) VALUES
(1, 'testestconfig', 1, 1, 1, 1, NULL, '2013-09-21 16:15:38');

--
-- Dumping data for table `testprofile`
--

INSERT INTO `testprofile` (`testprofile_id`, `testprofile_user_id`, `testprofile_name`, `testprofile_mode`, `testprofile_updated`, `testprofile_metadata`) VALUES
(1, 1, 'demotestprofile', 'NORMAL', '2013-09-21 16:15:52', NULL);

--
-- Dumping data for table `testrun`
--

INSERT INTO `testrun` (`testrun_id`, `testrun_user_id`, `testrun_testconfig_id`, `testrun_created`, `testrun_updated`, `testrun_status`) VALUES
(1, 1, 1, '2013-09-18 00:00:00', '2013-09-21 16:16:32', 'WAITING');

--
-- Dumping data for table `testscript`
--

INSERT INTO `testscript` (`testscript_id`, `testscript_name`, `testscript_type`, `testscript_file_path`, `testscript_file_name`, `testscript_user_id`, `testscript_classes`, `testscript_updated`) VALUES
(1, 'testscript1', 'instrumentation', 'magicpath', 'magicfilename', 1, '["clas1","class2","class3"]', '2013-09-21 18:43:49');

--
-- Dumping data for table `testtarget`
--

INSERT INTO `testtarget` (`testtarget_id`, `testtarget_name`, `testtarget_user_id`, `testtarget_file_path`, `testtarget_file_name`, `testtarget_updated`, `testtarget_metadata`) VALUES
(1, 'testtarget1', 1, 'magicpath', 'magicfilename', '2013-09-01 12:14:24', NULL);

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`user_id`, `user_name`, `user_password`, `user_email`) VALUES
(1, 'testuser1', '41da76f0fc3ec62a6939e634bfb6a342', 'testuser1@testusers.com'),
(2, 'testuser2', '58dd024d49e1d1b83a5d307f09f32734', 'testuser2@testusers.com');
