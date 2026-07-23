-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbluser_disconnect_details
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbluser_disconnect_details`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbluser_disconnect_details` (
  `disuserdtlid` bigint NOT NULL,
  `sessionid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `NASIPAddress` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `FramedIPAddress` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `disuserid` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`disuserdtlid`),
  UNIQUE KEY `user_disconnect_details_id_unq` (`disuserdtlid`),
  KEY `tbluser_disconnect_details_ibfk_1` (`disuserid`),
  CONSTRAINT `tbluser_disconnect_details_ibfk_1` FOREIGN KEY (`disuserid`) REFERENCES `tbluser_disconnect` (`disuserid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
