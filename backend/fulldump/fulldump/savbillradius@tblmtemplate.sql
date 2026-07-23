-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tblmtemplate
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmtemplate`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmtemplate` (
  `templateid` bigint NOT NULL AUTO_INCREMENT,
  `eventid` bigint DEFAULT NULL,
  `templatename` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `smstemplatedata` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `smseventconfigured` bit(1) DEFAULT NULL,
  `emailtemplatedata` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `emaileventconfigured` bit(1) DEFAULT NULL,
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodificationdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `mvnoid` bigint DEFAULT NULL,
  `appendurl` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`templateid`),
  KEY `eventid_id_fk` (`eventid`),
  CONSTRAINT `eventid_id_fk` FOREIGN KEY (`eventid`) REFERENCES `tblmevent` (`eventid`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
