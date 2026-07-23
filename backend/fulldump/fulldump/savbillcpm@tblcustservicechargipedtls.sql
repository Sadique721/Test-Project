-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblcustservicechargipedtls
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblcustservicechargipedtls`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblcustservicechargipedtls` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `custid` bigint DEFAULT NULL,
  `custservicemappingid` bigint DEFAULT NULL,
  `static_ip_address` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `static_ip_start_date` timestamp NULL DEFAULT NULL,
  `static_ip_end_date` timestamp NULL DEFAULT NULL,
  `charge_id` bigint DEFAULT NULL,
  `CREATEDATE` timestamp NULL DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` bigint DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblcustservicechargipedtls_field_unq` (`id`),
  KEY `custservicechargipedtls_fk1` (`custservicemappingid`),
  KEY `custservicechargipedtls_fk2` (`custid`),
  KEY `custservicechargipedtls_fk3` (`charge_id`),
  CONSTRAINT `custservicechargipedtls_fk1` FOREIGN KEY (`custservicemappingid`) REFERENCES `tbltcustomerservicemapping` (`id`),
  CONSTRAINT `custservicechargipedtls_fk2` FOREIGN KEY (`custid`) REFERENCES `tblcustomers` (`custid`),
  CONSTRAINT `custservicechargipedtls_fk3` FOREIGN KEY (`charge_id`) REFERENCES `tblcharges` (`CHARGEID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
