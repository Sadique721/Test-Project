-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmcaspackagemapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmcaspackagemapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmcaspackagemapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `packagename` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT NULL,
  `packageid` bigint DEFAULT NULL,
  `casepackage_mapping_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tblmcaspackagemapping_id_fk` (`casepackage_mapping_id`),
  CONSTRAINT `tblmcaspackagemapping_id_fk` FOREIGN KEY (`casepackage_mapping_id`) REFERENCES `tbltcasmaster` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
