-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbilltaskmanagement    Table: tblt_resolution_file_mapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblt_resolution_file_mapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblt_resolution_file_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `resolution_id` bigint NOT NULL,
  `filename` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `uniquename` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `longitude` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `latitude` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `case_id` bigint DEFAULT NULL,
  `staff_id` bigint DEFAULT NULL,
  `resolution_time` timestamp NULL DEFAULT NULL,
  `remarks` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
