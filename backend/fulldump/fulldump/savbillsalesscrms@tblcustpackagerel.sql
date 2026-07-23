-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tblcustpackagerel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblcustpackagerel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblcustpackagerel` (
  `cust_plan_mappping_id` bigint NOT NULL AUTO_INCREMENT,
  `planId` bigint DEFAULT NULL,
  `custid` bigint DEFAULT NULL,
  `lead_master_id` bigint DEFAULT NULL,
  `startDate` timestamp NULL DEFAULT NULL,
  `endDate` timestamp NULL DEFAULT NULL,
  `expiryDate` timestamp NULL DEFAULT NULL,
  `startDateString` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `endDateString` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `expiryDateString` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `qospolicyId` bigint DEFAULT NULL,
  `uploadqos` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `downloadqos` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `uploadts` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `downloadts` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `service` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `isDelete` tinyint(1) DEFAULT '0',
  `offerPrice` decimal(20,4) DEFAULT NULL,
  `taxAmount` decimal(20,4) DEFAULT NULL,
  `walletBalUsed` decimal(20,4) DEFAULT '0.0000',
  `creditdocid` bigint DEFAULT NULL,
  `purchaseType` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `onlinePurchaseId` bigint DEFAULT NULL,
  `purchaseFrom` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `debitdocid` bigint DEFAULT NULL,
  `validity` decimal(20,4) DEFAULT NULL,
  `planName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `discount` decimal(20,4) DEFAULT NULL,
  `plangroupid` bigint DEFAULT NULL,
  `planValidityDays` bigint DEFAULT NULL,
  `isInvoiceToOrg` tinyint(1) DEFAULT '0',
  `billTo` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `newAmount` decimal(20,4) DEFAULT NULL,
  `renewalId` bigint DEFAULT NULL,
  `custRefId` bigint DEFAULT NULL,
  `isTrialPlan` tinyint(1) DEFAULT '0',
  `discount_expiry_date` date DEFAULT NULL,
  `s_discount_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'One-time',
  PRIMARY KEY (`cust_plan_mappping_id`),
  UNIQUE KEY `cust_package_rel_id_unq` (`cust_plan_mappping_id`),
  KEY `cust_package_rel_lead_master_id_fk` (`lead_master_id`),
  CONSTRAINT `cust_package_rel_lead_master_id_fk` FOREIGN KEY (`lead_master_id`) REFERENCES `tblmleadmaster` (`lead_master_id`)
) ENGINE=InnoDB AUTO_INCREMENT=127 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
