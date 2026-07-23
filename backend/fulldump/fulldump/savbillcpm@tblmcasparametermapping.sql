-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmcasparametermapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmcasparametermapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmcasparametermapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parameter_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parameter_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cas_param_mapping_id` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `cas_param_mapping_id_id_fk` (`cas_param_mapping_id`),
  CONSTRAINT `cas_param_mapping_id_id_fk` FOREIGN KEY (`cas_param_mapping_id`) REFERENCES `tbltcasmaster` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
