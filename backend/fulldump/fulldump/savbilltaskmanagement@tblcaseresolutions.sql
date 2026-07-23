-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbilltaskmanagement    Table: tblcaseresolutions
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblcaseresolutions`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblcaseresolutions` (
  `res_id` bigint NOT NULL AUTO_INCREMENT,
  `res_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `res_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_delete` tinyint(1) NOT NULL DEFAULT '0',
  `CREATEDATE` timestamp NULL DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT NULL,
  `CREATEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  `BUID` bigint DEFAULT NULL,
  `lcoid` bigint DEFAULT NULL,
  PRIMARY KEY (`res_id`),
  UNIQUE KEY `caseresolutions_res_id_unq` (`res_id`),
  KEY `tblcaseresolutions_ibfk_1` (`MVNOID`),
  KEY `businessunit_casereasonsoltuions_fk` (`BUID`),
  CONSTRAINT `businessunit_casereasonsoltuions_fk` FOREIGN KEY (`BUID`) REFERENCES `tblmbusinessunit` (`businessunitid`),
  CONSTRAINT `tblcaseresolutions_ibfk_1` FOREIGN KEY (`MVNOID`) REFERENCES `tblmmvno` (`MVNOID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
