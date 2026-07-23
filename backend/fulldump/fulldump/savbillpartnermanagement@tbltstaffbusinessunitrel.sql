-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillpartnermanagement    Table: tbltstaffbusinessunitrel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltstaffbusinessunitrel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltstaffbusinessunitrel` (
  `staffbuid` bigint NOT NULL AUTO_INCREMENT,
  `businessunitid` bigint NOT NULL,
  `staffid` bigint NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodified_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createdbystaffid` bigint DEFAULT NULL,
  `lastmodifiedbystaffid` bigint DEFAULT NULL,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`staffbuid`),
  UNIQUE KEY `tbltstaffbusinessunitrel_staffbuid_uni` (`staffbuid`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
