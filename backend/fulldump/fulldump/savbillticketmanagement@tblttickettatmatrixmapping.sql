-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillticketmanagement    Table: tblttickettatmatrixmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblttickettatmatrixmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblttickettatmatrixmapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `time_p1` bigint DEFAULT NULL,
  `time_p2` bigint DEFAULT NULL,
  `time_p3` bigint DEFAULT NULL,
  `munit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `action` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `order_no` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT NULL,
  `level` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tat_mapping_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tblttickettatmatrixmapping_tat_mapping_id_fk` (`tat_mapping_id`),
  CONSTRAINT `tblttickettatmatrixmapping_tat_mapping_id_fk` FOREIGN KEY (`tat_mapping_id`) REFERENCES `tblttickettatmatrix` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
