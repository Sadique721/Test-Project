-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillinventorymanagement    Table: tblmoltportdetails
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmoltportdetails`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmoltportdetails` (
  `portid` bigint NOT NULL AUTO_INCREMENT,
  `portname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `slotid` bigint DEFAULT NULL,
  `deviceid` bigint DEFAULT NULL,
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createdbystaffid` bigint DEFAULT NULL,
  `lastmodifiedbystaffid` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`portid`),
  UNIQUE KEY `tblmoltportdetails_portid_unq` (`portid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
