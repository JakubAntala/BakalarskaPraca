-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hostiteľ: 127.0.0.1
-- Čas generovania: Út 15.Apr 2025, 21:26
-- Verzia serveru: 10.4.32-MariaDB
-- Verzia PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Databáza: `hotel_events`
--

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `event`
--

CREATE TABLE `event` (
  `id` int(11) NOT NULL,
  `hotel_id` int(11) DEFAULT NULL,
  `event_name` varchar(255) NOT NULL,
  `event_date` date NOT NULL,
  `organizer_company` varchar(255) NOT NULL,
  `available` bit(1) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `description` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Sťahujem dáta pre tabuľku `event`
--

INSERT INTO `event` (`id`, `hotel_id`, `event_name`, `event_date`, `organizer_company`, `available`, `created_at`, `updated_at`, `user_id`, `description`) VALUES
(3, 3, 'Bitcoin Seminar', '2024-03-25', 'EduGroup', b'1', '2024-12-22 14:07:19', '2025-01-17 16:04:42', 16, 'This event is amazing;You should attend;Perfect for networking;'),
(4, 4, 'Wide Workshop', '2024-04-30', 'SkillBuilders', b'1', '2024-12-22 14:07:19', '2024-12-22 14:07:19', 16, 'This event is amazing;You should attend;Perfect for networking;Don’t miss out;'),
(5, 7, 'Eminem Concert', '2024-12-28', 'MusicPro2', b'1', '2024-12-22 14:07:19', '2025-03-27 18:24:40', 16, 'This event is amazing;You should attend;Perfect for networking;Don’t miss out;DONT BE MAD '),
(6, 6, 'Dog Beauty Exhibition', '2024-06-15', 'ArtWorld', b'1', '2024-12-22 14:07:19', '2024-12-22 14:07:19', 16, 'This event is amazing;You should attend;Perfect for networking;Don’t miss out;'),
(7, 7, 'C.E.O. Meeting', '2024-07-20', 'BizPartners', b'1', '2024-12-22 14:07:19', '2024-12-22 14:07:19', 16, 'This event is amazing;You should attend;Perfect for networking;Don’t miss out;'),
(8, 8, 'Good Workshop II', '2024-08-25', 'FunTime', b'1', '2024-12-22 14:07:19', '2024-12-22 14:07:19', 16, 'This event is amazing;You should attend;Perfect for networking;Don’t miss out;'),
(9, 9, 'Snow Summit', '2024-09-30', 'GlobalLeaders', b'1', '2024-12-22 14:07:19', '2024-12-22 14:07:19', 16, 'This event is amazing;You should attend;Perfect for networking;Don’t miss out;'),
(10, 10, 'Inter Exchange Seminar Z AMERIKY', '2024-10-15', 'WellnessInc', b'1', '2024-12-22 14:07:19', '2025-03-27 22:16:32', 16, 'This event is amazing;You should attend;Perfect for networking;Don’t miss out;'),
(26, 1, 'Work and Travel Amerika Mastermind2', '2024-12-28', 'Org Spol', b'0', '2024-12-28 17:30:17', '2025-03-27 18:27:24', 15, 'Ahoj; Mas; SA ?; nie nemam sa \r\n');

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `event_reservation`
--

CREATE TABLE `event_reservation` (
  `id` int(11) NOT NULL,
  `event_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `date` date DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Sťahujem dáta pre tabuľku `event_reservation`
--

INSERT INTO `event_reservation` (`id`, `event_id`, `user_id`, `start_time`, `end_time`, `date`, `created_at`, `updated_at`) VALUES
(7, 7, 7, '2024-07-20 14:00:00', '2024-07-20 16:00:00', '2024-07-20', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(8, 8, 8, '2024-08-25 10:00:00', '2024-08-25 12:00:00', '2024-08-25', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(9, 9, 9, '2024-09-30 13:00:00', '2024-09-30 15:00:00', '2024-09-30', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(10, 10, 10, '2024-10-15 18:00:00', '2024-10-15 20:00:00', '2024-10-15', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(13, 10, 15, '2024-10-15 18:00:00', '2024-10-15 20:00:00', '2024-12-28', '2024-12-28 17:52:42', '2024-12-28 17:52:42'),
(31, 5, 17, '2024-12-28 10:00:00', '2024-12-28 18:00:00', '2024-12-28', '2025-03-27 17:52:23', '2025-03-27 17:52:23'),
(40, 3, 17, '2024-03-25 10:00:00', '2024-03-25 18:00:00', '2024-03-25', '2025-03-27 22:15:56', '2025-03-27 22:15:56'),
(41, 10, 16, '2024-10-15 10:00:00', '2024-10-15 18:00:00', '2024-10-15', '2025-03-27 22:16:41', '2025-03-27 22:16:41');

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `hotel`
--

CREATE TABLE `hotel` (
  `id` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `rating` decimal(10,0) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Sťahujem dáta pre tabuľku `hotel`
--

INSERT INTO `hotel` (`id`, `name`, `address`, `rating`, `email`, `created_at`, `updated_at`, `image`) VALUES
(1, 'Hotel Alpha', '123 Main St', 5, 'alpha@example.com', '2024-12-22 14:07:19', '2024-12-22 14:07:19', 'images/hotel1.jpg'),
(2, 'Hotel Beta', '456 Park Ave', 4, 'beta@example.com', '2024-12-22 14:07:19', '2024-12-22 14:07:19', 'images/hotel2.jpg'),
(3, 'Hotel Gamma', '789 Ocean Dr', 4, 'gamma@example.com', '2024-12-22 14:07:19', '2024-12-22 14:07:19', 'images/hotel3.jpg'),
(4, 'Hotel Delta', '101 Lake Rd', 4, 'delta@example.com', '2024-12-22 14:07:19', '2024-12-22 14:07:19', 'images/hotel4.jpg'),
(5, 'Hotel Epsilon', '202 Hill St', 4, 'epsilon@example.com', '2024-12-22 14:07:19', '2024-12-22 14:07:19', 'images/hotel5.jpg'),
(6, 'Hotel Zeta', '303 Valley Blvd', 5, 'zeta@example.com', '2024-12-22 14:07:19', '2024-12-22 14:07:19', 'images/hotel6.jpg'),
(7, 'Hotel Eta', '404 River Ln', 4, 'eta@example.com', '2024-12-22 14:07:19', '2024-12-22 14:07:19', 'images/hotel7.jpg'),
(8, 'Hotel Theta', '505 Forest Rd', 4, 'theta@example.com', '2024-12-22 14:07:19', '2024-12-22 14:07:19', 'images/hotel8.jpg'),
(9, 'Hotel Iota', '606 Meadow Dr', 4, 'iota@example.com', '2024-12-22 14:07:19', '2024-12-22 14:07:19', 'images/hotel9.jpg'),
(10, 'Hotel Kappa', '707 Desert Ct', 5, 'kappa@example.com', '2024-12-22 14:07:19', '2024-12-22 14:07:19', 'images/hotel10.jpg');

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `hotel_reviews`
--

CREATE TABLE `hotel_reviews` (
  `id` int(11) NOT NULL,
  `hotel_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `rating` int(11) DEFAULT NULL,
  `note` text DEFAULT NULL,
  `date` date DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Sťahujem dáta pre tabuľku `hotel_reviews`
--

INSERT INTO `hotel_reviews` (`id`, `hotel_id`, `user_id`, `rating`, `note`, `date`, `created_at`, `updated_at`) VALUES
(1, 1, 1, 5, 'Excellent service!', '2024-01-15', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(2, 2, 2, 4, 'Very good experience.', '2024-02-20', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(3, 3, 3, 3, 'Average stay.', '2024-03-25', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(4, 4, 4, 5, 'Amazing hospitality!', '2024-04-30', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(5, 5, 5, 2, 'Not up to the mark.', '2024-05-10', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(6, 6, 6, 4, 'Good overall.', '2024-06-15', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(7, 7, 7, 5, 'Highly recommended!', '2024-07-20', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(8, 8, 8, 3, 'It was okay.', '2024-08-25', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(9, 9, 9, 4, 'Nice place.', '2024-09-30', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(10, 10, 10, 5, 'Perfect!', '2024-10-15', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(16, 2, 15, 4, '1231231231', '2025-01-17', '2025-01-17 12:43:43', '2025-01-17 12:43:43'),
(21, 10, 17, 2, 'super', '2025-03-26', '2025-03-26 18:03:19', '2025-03-26 18:03:19'),
(22, 1, 16, 1, 'asd', '2025-03-26', '2025-03-26 18:06:23', '2025-03-26 18:06:23'),
(23, 5, 16, 1, 'asd', '2025-03-26', '2025-03-26 18:12:32', '2025-03-26 18:12:32'),
(24, 1, 16, 2, 'asdasda', '2025-03-27', '2025-03-27 22:16:49', '2025-03-27 22:16:49'),
(25, 1, 17, 1, 'aha', '2025-03-28', '2025-03-28 19:37:22', '2025-03-28 19:37:22'),
(26, 2, 17, 1, '123', '2025-03-28', '2025-03-28 19:41:22', '2025-03-28 19:41:22');

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `born` date DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Sťahujem dáta pre tabuľku `user`
--

INSERT INTO `user` (`id`, `first_name`, `last_name`, `born`, `phone`, `email`, `password`, `gender`, `role`, `created_at`, `updated_at`) VALUES
(1, 'John', 'Doe', '1985-06-15', '1234567890', 'john.doe@example.com', '123', 'M', 'staff', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(2, 'Jane', 'Smith', '1990-08-25', '0987654321', 'jane.smith@example.com', '', 'F', 'guest', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(3, 'Alice', 'Johnson', '1982-04-10', '1122334455', 'alice.johnson@example.com', '', 'F', 'guest', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(4, 'Bob', 'Brown', '1978-12-20', '6677889900', 'bob.brown@example.com', '', 'M', 'guest', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(5, 'Charlie', 'Davis', '1995-03-05', '4455667788', 'charlie.davis@example.com', '', 'M', 'guest', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(6, 'Emily', 'White', '1988-07-19', '2233445566', 'emily.white@example.com', '', 'F', 'guest', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(7, 'David', 'Wilson', '1992-11-30', '3344556677', 'david.wilson@example.com', '', 'M', 'guest', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(8, 'Sarah', 'Lee', '1980-02-25', '5566778899', 'sarah.lee@example.com', '', 'F', 'guest', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(9, 'Michael', 'Taylor', '1987-09-10', '7788990011', 'michael.taylor@example.com', '', 'M', 'guest', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(10, 'Laura', 'Anderson', '1993-05-15', '8899001122', 'laura.anderson@example.com', '', 'F', 'guest', '2024-12-22 14:07:19', '2024-12-22 14:07:19'),
(14, 'Jakub', 'Antala', '2024-12-26', '0903662946', 'jakub.antala100@gmail.com', '$2a$10$wxwGAKLOgYoNbj5R7uQR4.ju3vHNnpNMA9KNetRv9fb5WGp7ngJ26', 'M', 'guest', '2024-12-25 20:25:14', '2024-12-25 20:25:14'),
(15, 'Jakub', 'Antala', '2024-12-04', '1903662946', 'staff@gmail.com', '$2a$10$XmsxD61Y/DYKRFWVFkbFx.OCiEJsuKCrlLcJ/C6iq32N4WVnm9HUq', 'F', 'staff', '2024-12-26 14:34:38', '2025-01-17 15:20:36'),
(16, 'Admin', 'Admin', '2024-12-19', '0903662946', 'admin@gmail.com', '$2a$10$HSxFldsgo6azIKHYMqjbzO2cP7qaVfjTDs2MFnetSO7HptUMnf.tO', 'M', 'admin', '2024-12-26 15:30:40', '2024-12-26 15:30:40'),
(17, 'Guest', 'Guest', '2011-11-11', '1903662946', 'guest@gmail.com', '$2a$10$h4K7zjwIzeTn2OHNLw2dZOAtuXFAjCRuU1Qe8kH1LFJXwLKtFV2i2', 'M', 'guest', '2024-12-27 19:32:19', '2024-12-27 19:32:19'),
(19, 'ada', 'ada', '2025-01-09', '903662946', 'ada@gmail.com', '$2a$10$SNT5cPX939icXBx4.OaFruhvn4eM0MQE4z5XXYikCOVQK9R3gbie6', 'M', 'guest', '2025-01-17 15:54:16', '2025-01-17 15:54:16'),
(20, 'ads', 'asd', '2025-01-18', '903662946', 'asd@ukfsk', '$2a$10$LQhir80ZmgBIpVpcxAYKN.sJ5.jxRJC0itCB9sdnfOHzx9Fvh0HLO', 'M', 'staff', '2025-01-17 16:06:22', '2025-01-17 16:06:22');

--
-- Kľúče pre exportované tabuľky
--

--
-- Indexy pre tabuľku `event`
--
ALTER TABLE `event`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idHotel` (`hotel_id`),
  ADD KEY `user_id` (`user_id`) USING BTREE;

--
-- Indexy pre tabuľku `event_reservation`
--
ALTER TABLE `event_reservation`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idEvent` (`event_id`),
  ADD KEY `idCustomer` (`user_id`);

--
-- Indexy pre tabuľku `hotel`
--
ALTER TABLE `hotel`
  ADD PRIMARY KEY (`id`);

--
-- Indexy pre tabuľku `hotel_reviews`
--
ALTER TABLE `hotel_reviews`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idHotel` (`hotel_id`),
  ADD KEY `idCustomer` (`user_id`);

--
-- Indexy pre tabuľku `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT pre exportované tabuľky
--

--
-- AUTO_INCREMENT pre tabuľku `event`
--
ALTER TABLE `event`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT pre tabuľku `event_reservation`
--
ALTER TABLE `event_reservation`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=43;

--
-- AUTO_INCREMENT pre tabuľku `hotel`
--
ALTER TABLE `hotel`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT pre tabuľku `hotel_reviews`
--
ALTER TABLE `hotel_reviews`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT pre tabuľku `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- Obmedzenie pre exportované tabuľky
--

--
-- Obmedzenie pre tabuľku `event`
--
ALTER TABLE `event`
  ADD CONSTRAINT `event_ibfk_1` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`);

--
-- Obmedzenie pre tabuľku `event_reservation`
--
ALTER TABLE `event_reservation`
  ADD CONSTRAINT `event_reservation_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`),
  ADD CONSTRAINT `event_reservation_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Obmedzenie pre tabuľku `hotel_reviews`
--
ALTER TABLE `hotel_reviews`
  ADD CONSTRAINT `hotel_reviews_ibfk_1` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`),
  ADD CONSTRAINT `hotel_reviews_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
