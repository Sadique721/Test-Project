-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblttrialdebitdocument
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblttrialdebitdocument`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblttrialdebitdocument` (
  `trialdebitdocumentid` bigint NOT NULL AUTO_INCREMENT,
  `trialdebitdocumentnumber` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `subscriberid` bigint DEFAULT NULL,
  `billdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
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
  `trialbillrunid` bigint DEFAULT NULL,
  `billrunstatus` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `xmldocument` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `email` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_delete` tinyint(1) NOT NULL DEFAULT '0',
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `custpackrelid` bigint DEFAULT NULL,
  `purchaseorder_id` bigint DEFAULT NULL,
  `xmldocumentindividual` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `billable_to_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `adjustedamount` decimal(20,6) DEFAULT '0.000000',
  PRIMARY KEY (`trialdebitdocumentid`),
  UNIQUE KEY `ttrialdebitdocument_trialdebitdocumentid_unq` (`trialdebitdocumentid`),
  KEY `tblttrialdebitdocument_ibfk_1` (`subscriberid`),
  KEY `tblttrialdebitdocument_ibfk_2` (`trialbillrunid`),
  KEY `tblttrialdebitdocument_ibfk_12` (`purchaseorder_id`),
  CONSTRAINT `tblttrialdebitdocument_ibfk_12` FOREIGN KEY (`purchaseorder_id`) REFERENCES `tbltpurchaseorder` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
