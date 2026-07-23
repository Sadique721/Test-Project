-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblradiusprofilecheckitm
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblradiusprofilecheckitm`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblradiusprofilecheckitm` (
  `radiuscheckitmid` bigint NOT NULL AUTO_INCREMENT,
  `checkitem` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `radiusprofileid` int NOT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` decimal(20,0) NOT NULL DEFAULT '1',
  `LASTMODIFIEDBYSTAFFID` decimal(20,0) NOT NULL DEFAULT '1',
  `createdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`radiuscheckitmid`),
  UNIQUE KEY `radiusprofilecheckitm_radiuscheckitmid_unq` (`radiuscheckitmid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
