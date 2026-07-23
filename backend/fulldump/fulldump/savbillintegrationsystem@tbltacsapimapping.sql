-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillintegrationsystem    Table: tbltacsapimapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltacsapimapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltacsapimapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `api_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `api_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `acs_master_id` bigint DEFAULT NULL,
  `endpoint` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltacsapimapping_uniq` (`id`),
  KEY `tbltacsapimapping_fk_acs_master_id` (`acs_master_id`),
  CONSTRAINT `tbltacsapimapping_fk_acs_master_id` FOREIGN KEY (`acs_master_id`) REFERENCES `tblmacsmaster` (`acs_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
