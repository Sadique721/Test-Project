-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: diameterdb    Table: tblcustquotadtls
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblcustquotadtls`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblcustquotadtls` (
  `quotadtlsid` bigint NOT NULL AUTO_INCREMENT,
  `custid` bigint NOT NULL,
  `planid` bigint NOT NULL,
  `quotatype` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `totalquota` decimal(20,8) DEFAULT NULL,
  `usedquota` decimal(20,8) DEFAULT NULL,
  `quotaunit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `timetotalquota` decimal(20,8) DEFAULT NULL,
  `timequotaused` decimal(20,8) DEFAULT NULL,
  `timequotaunit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `createdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifiedbystaffid` decimal(20,0) DEFAULT NULL,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `totalquotakb` decimal(38,8) DEFAULT NULL,
  `usedquotakb` decimal(20,8) DEFAULT NULL,
  `timeusedquotasec` decimal(20,8) DEFAULT NULL,
  `timetotalquotasec` decimal(20,8) DEFAULT NULL,
  `custpackageid` int DEFAULT NULL,
  `didtotalquota` decimal(50,0) DEFAULT NULL,
  `didusedquota` decimal(50,0) DEFAULT NULL,
  `intercomtotalquota` decimal(50,0) DEFAULT NULL,
  `intercomusedquota` decimal(50,0) DEFAULT NULL,
  `did_quota_unit` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `intercom_quota_unit` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `speeddowngradeflag` tinyint(1) NOT NULL DEFAULT '0',
  `isfupapllied` tinyint(1) DEFAULT '0',
  `fupapplieddate` timestamp NULL DEFAULT NULL,
  `currentsessionusagetime` decimal(20,8) DEFAULT '0.00000000',
  `currentsessionusagevolume` decimal(20,8) DEFAULT '0.00000000',
  `parnet_quota_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'individual',
  `is_chunk_available` bit(1) DEFAULT b'0',
  `reserved_quota_in_per` double DEFAULT NULL,
  `total_reserved_quota` double(20,8) DEFAULT NULL,
  `usage_quota_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'TOTAL',
  `skip_quota_update` bit(1) DEFAULT b'0',
  `last_quota_reset` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `isquotaupdateskipped` bit(1) DEFAULT b'0',
  PRIMARY KEY (`quotadtlsid`),
  UNIQUE KEY `custquotadtls_quotadtlsid_unq` (`quotadtlsid`),
  KEY `indexcustquotadtlscustid` (`custid`),
  KEY `tblcustquotadtls_custid_planid_custpackageid` (`custid`,`planid`,`custpackageid`),
  KEY `tblcustquotadtls_custid_parnet_quota_type` (`custid`,`parnet_quota_type`),
  KEY `idx_custquotadtls_custpackageid` (`custpackageid`),
  KEY `cpr_idtblcustquotadtls` (`custpackageid`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
