-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tblmdictionary
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmdictionary`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmdictionary` (
  `dictionaryid` bigint NOT NULL AUTO_INCREMENT,
  `vendor` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `vendorid` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `vendortype` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT NULL,
  `lastmodificationdate` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`dictionaryid`),
  UNIQUE KEY `dictionary_vendor_mvno_unq` (`vendor`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
