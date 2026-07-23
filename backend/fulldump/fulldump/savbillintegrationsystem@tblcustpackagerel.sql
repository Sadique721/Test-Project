-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillintegrationsystem    Table: tblcustpackagerel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblcustpackagerel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblcustpackagerel` (
  `custpackageid` bigint NOT NULL AUTO_INCREMENT,
  `custid` bigint NOT NULL,
  `planid` bigint NOT NULL,
  `startdate` timestamp NOT NULL,
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
  `createdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifiedbystaffid` decimal(20,0) DEFAULT NULL,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_delete` tinyint(1) NOT NULL DEFAULT '0',
  `offer_price` double DEFAULT NULL,
  `tax_amount` double DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `creditdocid` bigint DEFAULT NULL,
  `debitdocid` bigint DEFAULT NULL,
  `wallet_bal_used` double NOT NULL DEFAULT '0',
  `purchase_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'New',
  `online_purchase_id` bigint DEFAULT NULL,
  `purchase_from` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Admin',
  `discount` double DEFAULT NULL,
  `dbr` double DEFAULT NULL,
  `plangroupid` bigint DEFAULT NULL,
  `bill_to` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'CUSTOMER',
  `is_invoice_to_org` tinyint(1) DEFAULT '0',
  `new_amount` decimal(20,4) DEFAULT NULL,
  `renewal_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cust_ref_id` bigint DEFAULT NULL,
  `next_approver` bigint DEFAULT NULL,
  `next_staff` bigint DEFAULT NULL,
  `staff_approver_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cust_ref_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cust_plan_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'Active',
  `isinvoicestop` tinyint(1) DEFAULT '0',
  `istrialplan` tinyint(1) DEFAULT '0',
  `traildebitdocid` bigint DEFAULT NULL,
  `is_trial_validity` decimal(4,0) DEFAULT NULL,
  `trial_plan_validity_count` bigint DEFAULT '0',
  `billable_cust_id` bigint DEFAULT NULL,
  `custservicemappingid` bigint DEFAULT NULL,
  `invoice_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`custpackageid`),
  UNIQUE KEY `custpackagerel_custpackageid_unq` (`custpackageid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
