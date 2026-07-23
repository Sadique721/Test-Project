-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillintegrationsystem    Table: tbltgovernmentapimapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltgovernmentapimapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltgovernmentapimapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `api_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `endpoint` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `government_master_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltgovernmentapimapping_uniq` (`id`),
  KEY `tbltgovernmentapimapping_fk2` (`government_master_id`),
  CONSTRAINT `tbltgovernmentapimapping_fk2` FOREIGN KEY (`government_master_id`) REFERENCES `tblmgovernmentintegrationmaster` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
