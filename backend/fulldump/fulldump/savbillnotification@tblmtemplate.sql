-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillnotification    Table: tblmtemplate
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
  `mvnoid` bigint DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodificationdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `appendurl` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `buid` bigint DEFAULT NULL,
  `is_active` bit(1) DEFAULT b'1',
  `is_delete` bit(1) DEFAULT b'0',
  `templatefilepath` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contenttype` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `isemailtemplate` bit(1) DEFAULT b'0',
  `issmstemplate` bit(1) DEFAULT b'0',
  `servicetype` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'BSS',
  `filename` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `is_append_required` bit(1) DEFAULT b'0',
  PRIMARY KEY (`templateid`),
  KEY `eventid_id_fk` (`eventid`),
  CONSTRAINT `eventid_id_fk` FOREIGN KEY (`eventid`) REFERENCES `tblmevent` (`eventid`)
) ENGINE=InnoDB AUTO_INCREMENT=3112 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
