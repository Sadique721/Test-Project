-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: diameterdb    Table: tblcustpackagerel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblcustpackagerel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblcustpackagerel` (
  `custpackageid` bigint NOT NULL AUTO_INCREMENT,
  `custid` bigint DEFAULT NULL,
  `planid` bigint DEFAULT NULL,
  `startdate` timestamp NULL DEFAULT NULL,
  `enddate` timestamp NULL DEFAULT NULL,
  `expirydate` timestamp NULL DEFAULT NULL,
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `service` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `qospolicyid` bigint DEFAULT NULL,
  `uploadqos` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `downloadqos` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `uploadts` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `downloadts` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdbystaffid` decimal(20,0) DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifiedbystaffid` decimal(20,0) DEFAULT NULL,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_delete` tinyint(1) DEFAULT '0',
  `offer_price` double DEFAULT NULL,
  `tax_amount` double DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `creditdocid` bigint DEFAULT NULL,
  `debitdocid` bigint DEFAULT NULL,
  `wallet_bal_used` double DEFAULT '0',
  `purchase_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'New',
  `online_purchase_id` bigint DEFAULT NULL,
  `purchase_from` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'Admin',
  `grace_days` bigint DEFAULT '0',
  `cust_plan_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'Active',
  `notificationlevel` bigint DEFAULT '0',
  `istriggercoadm` bit(1) DEFAULT b'1',
  `onquotaexhausteventname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`custpackageid`),
  UNIQUE KEY `custpackagerel_custpackageid_unq` (`custpackageid`),
  KEY `tblcustpackagerel_custid_planid_custpackageid` (`custid`,`planid`,`custpackageid`),
  KEY `idx_custpackagerel_custid_status_purchase` (`custid`,`cust_plan_status`,`purchase_type`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
