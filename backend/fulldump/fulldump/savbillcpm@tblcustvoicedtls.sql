-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblcustvoicedtls
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblcustvoicedtls`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblcustvoicedtls` (
  `voicedtlsid` bigint NOT NULL AUTO_INCREMENT,
  `custid` bigint NOT NULL,
  `planid` bigint NOT NULL,
  `custpackageid` int DEFAULT NULL,
  `voicetype` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `totalvoice` decimal(20,4) DEFAULT NULL,
  `usedvoice` decimal(20,4) DEFAULT NULL,
  `createdbystaffid` decimal(20,0) DEFAULT NULL,
  `createdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifiedbystaffid` decimal(20,0) DEFAULT NULL,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createbyname` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pulse` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`voicedtlsid`),
  UNIQUE KEY `custvoicedtls_voicedtlsid_unq` (`voicedtlsid`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
