-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillticketmanagement    Table: tbltcustomer_ticket_file_mapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltcustomer_ticket_file_mapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltcustomer_ticket_file_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `customer_ticket_mapping_id` bigint NOT NULL,
  `section` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `filename` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `uniquename` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `longitude` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `latitude` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `optical_range` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
