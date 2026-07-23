-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: commongateway    Table: tblmsubbusinessvertical
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmsubbusinessvertical`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmsubbusinessvertical` (
  `sbvid` bigint NOT NULL AUTO_INCREMENT,
  `sbvname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bu_verticals_id` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createdbystaffid` bigint DEFAULT NULL,
  `lastmodifiedbystaffid` bigint DEFAULT NULL,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  PRIMARY KEY (`sbvid`),
  UNIQUE KEY `sbvid_unq` (`sbvid`),
  KEY `subbusinessvertical_ibfk_1` (`bu_verticals_id`),
  CONSTRAINT `subbusinessvertical_ibfk_1` FOREIGN KEY (`bu_verticals_id`) REFERENCES `tblmbusinessverticals` (`bu_verticals_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
