-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tbltimebasepolicydetails
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltimebasepolicydetails`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltimebasepolicydetails` (
  `details_id` bigint NOT NULL,
  `policy_id` bigint DEFAULT NULL,
  `from_day` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `to_day` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `from_time` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `to_time` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `qqsid` bigint DEFAULT NULL,
  `access` bit(1) DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `is_free_quota` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`details_id`),
  UNIQUE KEY `timebasepolicydetails_details_id_unq` (`details_id`),
  KEY `tbltimebasepolicydetails_ibfk_1` (`policy_id`),
  CONSTRAINT `tbltimebasepolicydetails_ibfk_1` FOREIGN KEY (`policy_id`) REFERENCES `tblmtimebasepolicy` (`policy_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
