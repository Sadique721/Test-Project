-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillnotification    Table: tblmnotificationconfigmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmnotificationconfigmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmnotificationconfigmapping` (
  `notificationconfigmappingid` bigint NOT NULL AUTO_INCREMENT,
  `notificationconfigid` bigint DEFAULT NULL,
  `parameter` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mvnoid` bigint DEFAULT NULL,
  `createdon` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifiedon` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`notificationconfigmappingid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
