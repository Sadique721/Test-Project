-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tbltmacaddressmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltmacaddressmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltmacaddressmapping` (
  `macaddressid` bigint NOT NULL AUTO_INCREMENT,
  `custid` bigint DEFAULT NULL,
  `macaddress` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT NULL,
  `lastmodificationdate` timestamp NULL DEFAULT NULL,
  `createdby` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `lastmodifiedby` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `custsermappingid` bigint DEFAULT NULL,
  `macretentiondate` timestamp NULL DEFAULT NULL,
  `normalizemac` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`macaddressid`),
  KEY `tbltmacaddressmapping_custid_macaddress` (`custid`,`macaddress`),
  KEY `mac_un_tbltmacaddressmapping` (`macaddress`),
  KEY `mac_un_tbltmacaddressmappingnorm` (`normalizemac`),
  KEY `mac_un_tbltmacaddressmappingcustid` (`custid`),
  KEY `idx_normalizemac_custid` (`normalizemac`,`custid`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
