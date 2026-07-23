-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmsubbusinessunit
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmsubbusinessunit`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmsubbusinessunit` (
  `sub_business_unit_id` bigint NOT NULL,
  `subBuName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `subBuCode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `businessunitid` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `createdbystaffid` decimal(20,0) DEFAULT NULL,
  `createdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifiedbystaffid` decimal(20,0) DEFAULT NULL,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  PRIMARY KEY (`sub_business_unit_id`),
  UNIQUE KEY `sub_bu_businessunitid_unq` (`sub_business_unit_id`),
  KEY `tblmsubbusinessunit_ibfk_1` (`MVNOID`),
  KEY `tblmsubbusinessunit_ibfk_2` (`businessunitid`),
  CONSTRAINT `tblmsubbusinessunit_ibfk_1` FOREIGN KEY (`MVNOID`) REFERENCES `tblmmvno` (`MVNOID`),
  CONSTRAINT `tblmsubbusinessunit_ibfk_2` FOREIGN KEY (`businessunitid`) REFERENCES `tblmbusinessunit` (`businessunitid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
