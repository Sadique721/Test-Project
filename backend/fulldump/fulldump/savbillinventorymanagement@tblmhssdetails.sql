-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillinventorymanagement    Table: tblmhssdetails
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmhssdetails`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmhssdetails` (
  `id` int NOT NULL AUTO_INCREMENT,
  `customer_id` int DEFAULT NULL,
  `inward_id` bigint DEFAULT NULL,
  `imsi` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ki_encrypted` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `msisdn` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mvnoid` int DEFAULT NULL,
  `CREATEDATE` datetime NOT NULL,
  `LASTMODIFIEDDATE` datetime DEFAULT NULL,
  `createbyname` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updatebyname` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `CREATEDBYSTAFFID` int NOT NULL,
  `LASTMODIFIEDBYSTAFFID` int NOT NULL,
  `mapping_id1` int DEFAULT NULL,
  `mapping_id2` int DEFAULT NULL,
  `remarks` text COLLATE utf8mb4_unicode_ci,
  `hss_call_count` int DEFAULT '0',
  `hss_deprovision_call_count` int DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
