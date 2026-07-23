-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: diameter-sit-db    Table: tblm_attribute_mapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblm_attribute_mapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblm_attribute_mapping` (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `attribute_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `vendor_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_attribute_mapping_attribute` (`attribute_id`),
  KEY `fk_attribute_mapping_vendor` (`vendor_id`),
  CONSTRAINT `fk_attribute_mapping_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `tblm_attribute` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_attribute_mapping_vendor` FOREIGN KEY (`vendor_id`) REFERENCES `tblm_vendor_information` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
