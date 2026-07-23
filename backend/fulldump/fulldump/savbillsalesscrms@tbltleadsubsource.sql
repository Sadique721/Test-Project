-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tbltleadsubsource
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltleadsubsource`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltleadsubsource` (
  `lead_sub_source_id` bigint NOT NULL AUTO_INCREMENT,
  `lead_sub_source_name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `lead_source_id` bigint DEFAULT NULL,
  PRIMARY KEY (`lead_sub_source_id`),
  KEY `lead_source_id_fk` (`lead_source_id`),
  CONSTRAINT `lead_source_id_fk` FOREIGN KEY (`lead_source_id`) REFERENCES `tblmleadsource` (`lead_source_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
