-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbilltaskmanagement    Table: tblmticketremark
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmticketremark`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmticketremark` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `custid` bigint DEFAULT NULL,
  `ticket_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ticket_id` bigint DEFAULT NULL,
  `staff_id` bigint DEFAULT NULL,
  `internal_remarks` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `external_remarks` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `is_from_customer` tinyint(1) DEFAULT '0',
  `CREATEDATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `common_domain` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ticketremark_id_unq` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
