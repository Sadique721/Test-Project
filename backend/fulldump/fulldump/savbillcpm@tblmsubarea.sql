-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmsubarea
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmsubarea`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmsubarea` (
  `subareaid` bigint NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `countryid` int NOT NULL,
  `cityid` int NOT NULL,
  `stateid` int NOT NULL,
  `areaid` bigint DEFAULT NULL,
  `mvnoid` int NOT NULL,
  `buid` bigint DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `createdbystaffid` bigint DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifiedbystaffid` bigint DEFAULT NULL,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`subareaid`),
  KEY `idx_subarea_areaid_deleted` (`areaid`,`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
