-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: commongateway    Table: tblmcity
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmcity`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmcity` (
  `cityid` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `stateid` decimal(20,0) DEFAULT NULL,
  `status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createdbystaffid` bigint DEFAULT NULL,
  `lastmodifiedbystaffid` bigint DEFAULT NULL,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `countryid` bigint NOT NULL,
  `is_delete` tinyint(1) NOT NULL DEFAULT '0',
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  PRIMARY KEY (`cityid`),
  UNIQUE KEY `tblmcity_cityid_unq` (`cityid`),
  KEY `TBLMCITY_ibfk_1` (`MVNOID`),
  KEY `idx_city_stateid_deleted` (`stateid`,`is_delete`),
  CONSTRAINT `TBLMCITY_ibfk_1` FOREIGN KEY (`MVNOID`) REFERENCES `tblmmvno` (`MVNOID`)
) ENGINE=InnoDB AUTO_INCREMENT=2268 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
