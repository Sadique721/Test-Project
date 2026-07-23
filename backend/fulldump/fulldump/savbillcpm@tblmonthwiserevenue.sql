-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmonthwiserevenue
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmonthwiserevenue`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmonthwiserevenue` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `year` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `month` bigint DEFAULT NULL,
  `revenue` double DEFAULT NULL,
  `outstanding` double DEFAULT NULL,
  `mvnoid` bigint DEFAULT NULL,
  `buid` bigint DEFAULT NULL,
  `service_area_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblmonthwiserevenue_id_unq` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
