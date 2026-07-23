-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillticketmanagement    Table: tblnotificationemplate
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblnotificationemplate`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblnotificationemplate` (
  `templateid` bigint NOT NULL AUTO_INCREMENT,
  `eventid` bigint DEFAULT NULL,
  `templatename` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `smstemplatedata` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `smseventconfigured` bit(1) DEFAULT NULL,
  `emailtemplatedata` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `emaileventconfigured` bit(1) DEFAULT NULL,
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mvnoid` bigint DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodificationdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `appendUrl` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`templateid`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
