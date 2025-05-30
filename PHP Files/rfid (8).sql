-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 10, 2025 at 12:52 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `rfid`
--

-- --------------------------------------------------------

--
-- Table structure for table `active_sessions`
--

CREATE TABLE `active_sessions` (
  `id` int(11) NOT NULL,
  `Tr_cardno` varchar(20) NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT 1,
  `start_time` timestamp NOT NULL DEFAULT current_timestamp(),
  `subject_code` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `active_sessions`
--

INSERT INTO `active_sessions` (`id`, `Tr_cardno`, `status`, `start_time`, `subject_code`) VALUES
(1, '7AA4312', 0, '2025-02-25 15:38:25', 'csc109'),
(2, '7AA4312', 0, '2025-02-25 15:38:25', 'csc109'),
(3, '7AA4312', 0, '2025-02-25 15:39:52', 'csc109'),
(4, '7AA4312', 0, '2025-02-25 15:39:52', 'csc109'),
(5, '23C6B192', 0, '2025-02-26 17:37:57', 'gec108'),
(6, '23C6B192', 0, '2025-02-26 17:40:56', 'gec108'),
(7, 'CA3762BF', 0, '2025-04-07 03:06:35', 'csc110'),
(8, 'CA3762BF', 0, '2025-04-07 03:11:43', 'csc110'),
(9, 'CA3762BF', 0, '2025-04-07 03:12:07', 'csc110'),
(10, 'CA3762BF', 0, '2025-04-07 03:12:45', 'csc110'),
(11, 'CA3762BF', 0, '2025-04-07 05:11:07', 'csc110');

-- --------------------------------------------------------

--
-- Table structure for table `classwork`
--

CREATE TABLE `classwork` (
  `id` int(11) NOT NULL,
  `file_name` varchar(255) NOT NULL,
  `file_url` varchar(255) NOT NULL,
  `Tr_Id` int(11) NOT NULL,
  `uploaded_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `classwork`
--

INSERT INTO `classwork` (`id`, `file_name`, `file_url`, `Tr_Id`, `uploaded_at`) VALUES
(0, '01_Notes (1).pdf', 'uploads/01_Notes (1).pdf', 202502, '2025-04-07 05:16:37');

-- --------------------------------------------------------

--
-- Table structure for table `has`
--

CREATE TABLE `has` (
  `day_id` int(11) DEFAULT NULL,
  `slot_id` int(11) DEFAULT NULL,
  `Subject_code` varchar(7) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `has`
--

INSERT INTO `has` (`day_id`, `slot_id`, `Subject_code`) VALUES
(1, 1, 'csc110'),
(1, 2, 'csd107'),
(1, 4, 'csc110'),
(1, 1, 'gec110'),
(1, 2, 'gec108'),
(1, 4, 'gec108'),
(2, 1, 'csc110'),
(2, 2, 'csc109'),
(2, 4, 'csc109'),
(2, 1, 'gec108'),
(2, 2, 'gec110'),
(2, 4, 'gec110'),
(3, 1, 'csd107'),
(3, 2, 'csc110'),
(3, 4, 'csc109'),
(3, 1, 'gec108'),
(3, 2, 'gec110'),
(3, 4, 'gec110'),
(4, 1, 'csc110'),
(4, 2, 'csc109'),
(4, 4, 'csc109'),
(4, 1, 'gec108'),
(4, 2, 'gec110'),
(4, 4, 'gec110'),
(5, 1, 'csc110'),
(5, 2, 'csc109'),
(5, 4, 'csc109'),
(5, 1, 'gec108'),
(5, 2, 'gec110'),
(5, 4, 'gec110'),
(6, 1, 'csc110'),
(6, 2, 'csc109'),
(6, 4, 'csc109'),
(6, 1, 'gec108'),
(6, 2, 'gec110'),
(6, 4, 'gec110');

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

CREATE TABLE `student` (
  `CUIN` int(11) NOT NULL,
  `Student_name` varchar(100) NOT NULL,
  `Student_cardno` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `student`
--

INSERT INTO `student` (`CUIN`, `Student_name`, `Student_cardno`) VALUES
(2203613, 'Ayush Verekar', 'BE1CFC31'),
(2203627, 'Sakshi Vijapure', 'CE13BAA1'),
(2203662, 'Aadarsh Sawant', '3EA0B0A1'),
(2203669, 'Vedang Sawant', 'B363CED9'),
(2203672, 'Sprilea Dsouza', '731DB9D9'),
(2203714, 'Venkatesh More', '232982A7'),
(2203752, 'Angeeth Pavithran', 'AE391D31'),
(2203774, 'Litesh Gaude', '355DFD9'),
(2203911, 'Zaheed Khan', '36FCED9');

-- --------------------------------------------------------

--
-- Table structure for table `student_attendance`
--

CREATE TABLE `student_attendance` (
  `Student_cardno` varchar(20) NOT NULL,
  `entrytime` datetime DEFAULT current_timestamp(),
  `Subject_code` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `student_attendance`
--

INSERT INTO `student_attendance` (`Student_cardno`, `entrytime`, `Subject_code`) VALUES
('3EA0B0A1', '2025-02-25 21:11:42', 'csc109'),
('AE391D31', '2025-02-25 21:11:42', 'csc109'),
('BE1CFC31', '2025-02-25 21:11:58', 'csc109'),
('232982A7', '2025-02-25 21:11:58', 'csc109'),
('CE13BAA1', '2025-02-25 21:12:23', 'csc109'),
('AE391D31', '2025-02-25 21:12:23', 'csc109'),
('3EA0B0A1', '2025-02-25 21:13:00', 'csc109'),
('CE13BAA1', '2025-02-25 21:13:00', 'csc109'),
('BE1CFC31', '2025-02-25 21:14:51', 'csc109'),
('CE13BAA1', '2025-02-25 21:14:51', 'csc109'),
('AE391D31', '2025-02-25 21:15:04', 'csc109'),
('CE13BAA1', '2025-02-25 21:15:04', 'csc109'),
('731DB9D9', '2025-02-26 23:08:35', 'gec108'),
('CE13BAA1', '2025-04-07 08:41:52', 'csc110'),
('BE1CFC31', '2025-04-07 10:41:52', 'csc110'),
('BE1CFC31', '2025-04-07 10:41:59', 'csc110');

-- --------------------------------------------------------

--
-- Table structure for table `student_subject`
--

CREATE TABLE `student_subject` (
  `CUIN` int(10) NOT NULL,
  `Subject_code` varchar(7) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `student_subject`
--

INSERT INTO `student_subject` (`CUIN`, `Subject_code`) VALUES
(2203613, 'csd107'),
(2203613, 'csc110'),
(2203613, 'csc109'),
(2203627, 'csc107'),
(2203627, 'csc110'),
(2203627, 'csc109'),
(2203752, 'csd107'),
(2203752, 'csc110'),
(2203752, 'csc109'),
(2203714, 'csc107'),
(2203714, 'csc110'),
(2203714, 'csc109'),
(2203662, 'csd107'),
(2203662, 'csc110'),
(2203662, 'csc109'),
(2203669, 'gec108'),
(2203669, 'gec110'),
(2203672, 'gec110'),
(2203672, 'gec108'),
(2203911, 'gec110'),
(2203911, 'gec108');

-- --------------------------------------------------------

--
-- Table structure for table `subject`
--

CREATE TABLE `subject` (
  `Subject_code` varchar(10) NOT NULL,
  `Subject_name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `subject`
--

INSERT INTO `subject` (`Subject_code`, `Subject_name`) VALUES
('csd107', 'Data Analytics'),
('csc109', 'Fullstack Web Development'),
('gec110', 'Indian Stratigraphy'),
('csc110', 'Internet Of Things'),
('gec108', 'Sedimentary Petrology');

-- --------------------------------------------------------

--
-- Table structure for table `teacher`
--

CREATE TABLE `teacher` (
  `Tr_id` int(11) NOT NULL,
  `Tr_name` varchar(100) NOT NULL,
  `Tr_cardno` varchar(20) NOT NULL,
  `Department` varchar(50) DEFAULT NULL,
  `Hod` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `teacher`
--

INSERT INTO `teacher` (`Tr_id`, `Tr_name`, `Tr_cardno`, `Department`, `Hod`) VALUES
(202501, 'Shubha Kamat', 'CA3762BF', 'Computer Science', 1),
(202502, 'Nilesh Natekar', '7AA4312', 'Computer Science', 0),
(202503, 'Pooja Naik', '23C6B192', 'Geology', 0),
(202505, 'Amar Naik', 'D5F8322', 'Computer Science', 0),
(203504, 'Shubham Naik', '9C2332', 'Geology', 0);

-- --------------------------------------------------------

--
-- Table structure for table `teacher_subject`
--

CREATE TABLE `teacher_subject` (
  `Tr_id` int(11) DEFAULT NULL,
  `Subject_code` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `teacher_subject`
--

INSERT INTO `teacher_subject` (`Tr_id`, `Subject_code`) VALUES
(202501, 'csc110'),
(202502, 'csc109'),
(202503, 'gec108'),
(202505, 'csd107'),
(203504, 'gec110');

-- --------------------------------------------------------

--
-- Table structure for table `timetable`
--

CREATE TABLE `timetable` (
  `day_id` int(11) DEFAULT NULL,
  `slot_id` int(11) DEFAULT NULL,
  `Day` varchar(15) DEFAULT NULL,
  `slot` varchar(15) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `timetable`
--

INSERT INTO `timetable` (`day_id`, `slot_id`, `Day`, `slot`) VALUES
(1, 1, 'Monday', '8:45-9:45'),
(1, 2, 'Monday', '9:45-10:45'),
(1, 3, 'Monday', '10:45-11:15'),
(1, 4, 'Monday', '11:15-12:15'),
(1, 5, 'Monday', '12:15-1:15'),
(1, 6, 'Monday', '1:15-2:15'),
(1, 7, 'Monday', '2:15-3:15'),
(2, 1, 'Tuesday', '8:45-9:45'),
(2, 2, 'Tuesday', '9:45-10:45'),
(2, 3, 'Tuesday', '10:45-11:15'),
(2, 4, 'Tuesday', '11:15-12:15'),
(2, 5, 'Tuesday', '12:15-1:15'),
(2, 6, 'Tuesday', '1:15-2:15'),
(2, 7, 'Tuesday', '2:15-3:15'),
(3, 1, 'wednesday', '8:45-9:45'),
(3, 2, 'wednesday', '9:45-10:45'),
(3, 3, 'wednesday', '10:45-11:15'),
(3, 4, 'wednesday', '11:15-12:15'),
(3, 5, 'wednesday', '12:15-1:15'),
(3, 6, 'wednesday', '1:15-2:15'),
(3, 7, 'wednesday', '2:15-3:15'),
(4, 1, 'Thursday', '8:45-9:45'),
(4, 2, 'Thursday', '9:45-10:45'),
(4, 3, 'Thursday', '10:45-11:15'),
(4, 4, 'Thursday', '11:15-12:15'),
(4, 5, 'Thursday', '12:15-1:15'),
(4, 6, 'Thursday', '1:15-2:15'),
(4, 7, 'Thursday', '2:15-3:15'),
(5, 1, 'friday', '8:45-9:45'),
(5, 2, 'friday', '9:45-10:45'),
(5, 3, 'friday', '10:45-11:15'),
(5, 4, 'friday', '11:15-12:15'),
(5, 5, 'friday', '12:15-1:15'),
(5, 6, 'friday', '1:15-2:15'),
(5, 7, 'friday', '2:15-3:15'),
(6, 1, 'saturday', '8:45-9:45'),
(6, 2, 'saturday', '9:45-10:45'),
(6, 3, 'saturday', '10:45-11:15'),
(6, 4, 'saturday', '11:15-12:15'),
(6, 5, 'saturday', '12:15-1:15'),
(6, 6, 'saturday', '1:15-2:15'),
(6, 7, 'saturday', '2:15-3:15');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `active_sessions`
--
ALTER TABLE `active_sessions`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `classwork`
--
ALTER TABLE `classwork`
  ADD KEY `Tr_Id` (`Tr_Id`);

--
-- Indexes for table `student`
--
ALTER TABLE `student`
  ADD PRIMARY KEY (`CUIN`),
  ADD UNIQUE KEY `Student_cardno` (`Student_cardno`);

--
-- Indexes for table `student_attendance`
--
ALTER TABLE `student_attendance`
  ADD KEY `Student_cardno` (`Student_cardno`);

--
-- Indexes for table `subject`
--
ALTER TABLE `subject`
  ADD PRIMARY KEY (`Subject_code`),
  ADD UNIQUE KEY `Subject_name` (`Subject_name`);

--
-- Indexes for table `teacher`
--
ALTER TABLE `teacher`
  ADD PRIMARY KEY (`Tr_id`),
  ADD UNIQUE KEY `Tr_cardno` (`Tr_cardno`);

--
-- Indexes for table `teacher_subject`
--
ALTER TABLE `teacher_subject`
  ADD UNIQUE KEY `Tr_id` (`Tr_id`,`Subject_code`),
  ADD KEY `Subject_code` (`Subject_code`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `active_sessions`
--
ALTER TABLE `active_sessions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `teacher`
--
ALTER TABLE `teacher`
  MODIFY `Tr_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=203505;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `classwork`
--
ALTER TABLE `classwork`
  ADD CONSTRAINT `classwork_ibfk_1` FOREIGN KEY (`Tr_Id`) REFERENCES `teacher` (`Tr_id`) ON DELETE CASCADE;

--
-- Constraints for table `student_attendance`
--
ALTER TABLE `student_attendance`
  ADD CONSTRAINT `student_attendance_ibfk_1` FOREIGN KEY (`Student_cardno`) REFERENCES `student` (`Student_cardno`) ON DELETE CASCADE;

--
-- Constraints for table `teacher_subject`
--
ALTER TABLE `teacher_subject`
  ADD CONSTRAINT `teacher_subject_ibfk_1` FOREIGN KEY (`Tr_id`) REFERENCES `teacher` (`Tr_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `teacher_subject_ibfk_2` FOREIGN KEY (`Subject_code`) REFERENCES `subject` (`Subject_code`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
