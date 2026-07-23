-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillticketmanagement    Table: tbltticketreasoncategorytatmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltticketreasoncategorytatmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltticketreasoncategorytatmapping` (
  `mapping_id` bigint NOT NULL AUTO_INCREMENT,
  `time` bigint DEFAULT NULL,
  `time_unit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `action` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ticket_reason_category_id` bigint DEFAULT NULL,
  `order_number` bigint DEFAULT NULL,
  `escalated_time` bigint DEFAULT NULL,
  `medium_time` bigint DEFAULT NULL,
  `level` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`mapping_id`),
  KEY `tbltticketreasoncategorytatmapping_ticket_reason_category_id_fk` (`ticket_reason_category_id`),
  CONSTRAINT `tbltticketreasoncategorytatmapping_ticket_reason_category_id_fk` FOREIGN KEY (`ticket_reason_category_id`) REFERENCES `tblmticketreasoncategory` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
