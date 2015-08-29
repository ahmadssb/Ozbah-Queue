-- phpMyAdmin SQL Dump
-- version 4.0.10.7
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Aug 29, 2015 at 04:22 PM
-- Server version: 5.5.42-cll
-- PHP Version: 5.4.31

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `ahmadssb_ozbah`
--

-- --------------------------------------------------------

--
-- Table structure for table `ozbah_events`
--

CREATE TABLE IF NOT EXISTS `ozbah_events` (
  `event_id` int(11) NOT NULL AUTO_INCREMENT,
  `event_name` varchar(255) NOT NULL,
  `event_password` varchar(25) NOT NULL,
  PRIMARY KEY (`event_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

--
-- Dumping data for table `ozbah_events`
--

INSERT INTO `ozbah_events` (`event_id`, `event_name`, `event_password`) VALUES
(1, 'عزبتنا', '123'),
(2, 'عزبتنا للضومنة ', '123'),
(3, 'عزبتنا للبلوت', '123'),
(4, 'حبايب فقط', '123');

-- --------------------------------------------------------

--
-- Table structure for table `ozbah_users`
--

CREATE TABLE IF NOT EXISTS `ozbah_users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` text NOT NULL,
  `user_priority` varchar(2) NOT NULL,
  `user_status` varchar(2) NOT NULL,
  `user_event_id` int(11) NOT NULL,
  `user_modifiedon` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  KEY `user_event_id` (`user_event_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=697 ;

--
-- Dumping data for table `ozbah_users`
--

INSERT INTO `ozbah_users` (`user_id`, `user_name`, `user_priority`, `user_status`, `user_event_id`, `user_modifiedon`) VALUES
(695, 'سالم عبد‚‗‚احمح‚‗‚سالم‚‗‚عبدو', '', 'A', 1, '0000-00-00 00:00:00'),
(696, 'ابراهيم ', '', 'W', 1, '0000-00-00 00:00:00');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
