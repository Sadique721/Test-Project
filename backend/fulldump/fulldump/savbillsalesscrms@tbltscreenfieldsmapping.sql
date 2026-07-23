-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tbltscreenfieldsmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltscreenfieldsmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltscreenfieldsmapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `screenid` bigint DEFAULT NULL,
  `fieldid` bigint DEFAULT NULL,
  `parentfieldid` bigint DEFAULT NULL,
  `index` bigint DEFAULT NULL,
  `fieldtype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `endpoint` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dependantfieldname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `backendrequired` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `isdependant` tinyint DEFAULT NULL,
  `ispostrequest` tinyint DEFAULT NULL,
  `regex` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_temp_display` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltscreenfieldsmapping_unq` (`id`),
  KEY `fk_parentfieldid` (`parentfieldid`),
  KEY `fk_tbltscreenfieldsmapping_fieldid` (`fieldid`),
  KEY `fk_tbltscreenfieldsmapping_screenid` (`screenid`),
  CONSTRAINT `fk_parentfieldid` FOREIGN KEY (`parentfieldid`) REFERENCES `tblmfields` (`id`),
  CONSTRAINT `fk_tbltscreenfieldsmapping_fieldid` FOREIGN KEY (`fieldid`) REFERENCES `tblmfields` (`id`),
  CONSTRAINT `fk_tbltscreenfieldsmapping_screenid` FOREIGN KEY (`screenid`) REFERENCES `tblmscreens` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=317 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
