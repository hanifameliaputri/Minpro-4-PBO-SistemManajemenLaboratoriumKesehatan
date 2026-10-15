-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Oct 15, 2026 at 02:22 PM
-- Server version: 8.0.30
-- PHP Version: 8.1.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `laboratorium_kesehatan`
--

-- --------------------------------------------------------

--
-- Table structure for table `hasil_pemeriksaan`
--

CREATE TABLE `hasil_pemeriksaan` (
  `id_hasil` varchar(10) NOT NULL,
  `id_pasien` varchar(10) NOT NULL,
  `id_pemeriksaan` varchar(10) NOT NULL,
  `id_petugas` varchar(10) NOT NULL,
  `hasil` varchar(255) NOT NULL,
  `status` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `hasil_pemeriksaan`
--

INSERT INTO `hasil_pemeriksaan` (`id_hasil`, `id_pasien`, `id_pemeriksaan`, `id_petugas`, `hasil`, `status`) VALUES
('H1', 'P1', 'PM1', 'PT1', 'Hemoglobin 13.5 g/dL, Leukosit normal', 'Normal'),
('H2', 'P2', 'PM3', 'PT1', 'Gula darah sewaktu 210 mg/dL', 'Tidak Normal'),
('H3', 'P3', 'PM2', 'pt2', 'gejala tipes', 'Tidak Normal');

-- --------------------------------------------------------

--
-- Table structure for table `pasien`
--

CREATE TABLE `pasien` (
  `id_pasien` varchar(10) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `umur` int NOT NULL,
  `jenis_kelamin` varchar(20) NOT NULL,
  `keluhan` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `pasien`
--

INSERT INTO `pasien` (`id_pasien`, `nama`, `umur`, `jenis_kelamin`, `keluhan`) VALUES
('P1', 'Aulia Ashylla P', 19, 'Perempuan', 'Demam dan batuk sejak 3 hari'),
('P2', 'Budi Santoso', 34, 'Laki-laki', 'Sering lemas dan sakit kepala'),
('P3', 'putrika', 22, 'Perempuan', 'demam dan batuk 4 hari'),
('P5', 'vidya', 33, 'Perempuan', 'batuk batuk');

-- --------------------------------------------------------

--
-- Table structure for table `pemeriksaan`
--

CREATE TABLE `pemeriksaan` (
  `id_pemeriksaan` varchar(10) NOT NULL,
  `nama_pemeriksaan` varchar(100) NOT NULL,
  `biaya` decimal(12,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `pemeriksaan`
--

INSERT INTO `pemeriksaan` (`id_pemeriksaan`, `nama_pemeriksaan`, `biaya`) VALUES
('PM1', 'Tes Darah Lengkap', '150000.00'),
('PM2', 'Tes Urine', '100000.00'),
('PM3', 'Tes Gula Darah', '75000.00');

-- --------------------------------------------------------

--
-- Table structure for table `petugas`
--

CREATE TABLE `petugas` (
  `id_petugas` varchar(10) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `umur` int NOT NULL,
  `jenis_kelamin` varchar(20) NOT NULL,
  `tipe_petugas` varchar(10) NOT NULL,
  `spesialisasi` varchar(100) DEFAULT NULL,
  `nomor_str` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `petugas`
--

INSERT INTO `petugas` (`id_petugas`, `nama`, `umur`, `jenis_kelamin`, `tipe_petugas`, `spesialisasi`, `nomor_str`) VALUES
('PT1', 'Asti Putri', 29, 'Perempuan', 'Analis', 'Hematologi', NULL),
('PT2', 'dr. Hanif Amelia Putri', 25, 'Perempuan', 'Dokter', NULL, 'STR-123456789'),
('PT4', 'dr. Vidya Arista Syahira', 25, 'Perempuan', 'Dokter', NULL, 'STR-221122');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `hasil_pemeriksaan`
--
ALTER TABLE `hasil_pemeriksaan`
  ADD PRIMARY KEY (`id_hasil`),
  ADD KEY `fk_hasil_pasien` (`id_pasien`),
  ADD KEY `fk_hasil_pemeriksaan` (`id_pemeriksaan`),
  ADD KEY `fk_hasil_petugas` (`id_petugas`);

--
-- Indexes for table `pasien`
--
ALTER TABLE `pasien`
  ADD PRIMARY KEY (`id_pasien`);

--
-- Indexes for table `pemeriksaan`
--
ALTER TABLE `pemeriksaan`
  ADD PRIMARY KEY (`id_pemeriksaan`);

--
-- Indexes for table `petugas`
--
ALTER TABLE `petugas`
  ADD PRIMARY KEY (`id_petugas`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `hasil_pemeriksaan`
--
ALTER TABLE `hasil_pemeriksaan`
  ADD CONSTRAINT `fk_hasil_pasien` FOREIGN KEY (`id_pasien`) REFERENCES `pasien` (`id_pasien`),
  ADD CONSTRAINT `fk_hasil_pemeriksaan` FOREIGN KEY (`id_pemeriksaan`) REFERENCES `pemeriksaan` (`id_pemeriksaan`),
  ADD CONSTRAINT `fk_hasil_petugas` FOREIGN KEY (`id_petugas`) REFERENCES `petugas` (`id_petugas`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
