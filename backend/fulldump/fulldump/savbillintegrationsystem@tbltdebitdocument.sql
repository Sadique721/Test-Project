-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillintegrationsystem    Table: tbltdebitdocument
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltdebitdocument`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltdebitdocument` (
  `debitdocumentid` bigint NOT NULL,
  `debitdocumentnumber` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `subscriberid` bigint DEFAULT NULL,
  `billdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `startdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `enddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `duedate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `latepaymentdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `subtotal` decimal(20,4) DEFAULT '0.0000',
  `tax` decimal(20,4) DEFAULT '0.0000',
  `discount` decimal(20,4) DEFAULT '0.0000',
  `totalamount` decimal(20,4) DEFAULT '0.0000',
  `previousbalance` decimal(20,4) DEFAULT '0.0000',
  `latepaymentfee` decimal(20,4) DEFAULT '0.0000',
  `currentpayment` decimal(20,4) DEFAULT '0.0000',
  `currentdebit` decimal(20,4) DEFAULT '0.0000',
  `currentcredit` decimal(20,4) DEFAULT '0.0000',
  `totaldue` decimal(20,4) DEFAULT '0.0000',
  `totalamountinwords` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `totaldueinwords` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `billrunid` bigint DEFAULT NULL,
  `billrunstatus` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_delete` tinyint(1) NOT NULL DEFAULT '0',
  `credit_doc_id` bigint DEFAULT NULL,
  `custpackrelid` bigint DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` decimal(20,0) NOT NULL DEFAULT '1',
  `LASTMODIFIEDBYSTAFFID` decimal(20,0) NOT NULL DEFAULT '1',
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `cstchargeid` bigint DEFAULT NULL,
  `status` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `paymentowner` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cust_ref_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ird_sync` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ird_resp_code` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `inventory_mapping_id` bigint DEFAULT NULL,
  PRIMARY KEY (`debitdocumentid`),
  UNIQUE KEY `tbltdebitdocument_debitdocumentid` (`debitdocumentid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
