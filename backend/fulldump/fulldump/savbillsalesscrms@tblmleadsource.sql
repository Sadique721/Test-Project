-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tblmleadsource
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmleadsource`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmleadsource` (
  `lead_source_id` bigint NOT NULL AUTO_INCREMENT,
  `lead_source_name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `mvno_id` bigint DEFAULT NULL,
  `bu_id` bigint DEFAULT NULL,
  `status` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_delete` tinyint(1) NOT NULL DEFAULT '0',
  `view` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`lead_source_id`),
  KEY `lead_source_mvno_id_fk` (`mvno_id`),
  KEY `lead_source_bu_id_fk` (`bu_id`),
  CONSTRAINT `lead_source_bu_id_fk` FOREIGN KEY (`bu_id`) REFERENCES `tblmbusinessunit` (`businessunitid`),
  CONSTRAINT `lead_source_mvno_id_fk` FOREIGN KEY (`mvno_id`) REFERENCES `tblmmvno` (`MVNOID`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
