-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblleadcustpackagerel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblleadcustpackagerel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblleadcustpackagerel` (
  `cust_plan_mappping_id` bigint NOT NULL,
  `plan_id` bigint DEFAULT NULL,
  `custid` bigint DEFAULT NULL,
  `lead_master_id` bigint DEFAULT NULL,
  `start_date` timestamp NULL DEFAULT NULL,
  `end_date` timestamp NULL DEFAULT NULL,
  `expiry_date` timestamp NULL DEFAULT NULL,
  `start_date_string` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `end_date_string` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `expiry_date_string` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `qospolicy_id` bigint DEFAULT NULL,
  `uploadqos` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `downloadqos` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `uploadts` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `downloadts` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `service` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_delete` tinyint(1) DEFAULT '0',
  `offer_price` decimal(20,4) DEFAULT NULL,
  `tax_amount` decimal(20,4) DEFAULT NULL,
  `wallet_bal_used` decimal(20,4) DEFAULT '0.0000',
  `creditdocid` bigint DEFAULT NULL,
  `purchase_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `online_purchase_id` bigint DEFAULT NULL,
  `purchase_from` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `debitdocid` bigint DEFAULT NULL,
  `validity` decimal(20,4) DEFAULT NULL,
  `plan_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `discount` decimal(20,4) DEFAULT NULL,
  `plangroupid` bigint DEFAULT NULL,
  `plan_validity_days` bigint DEFAULT NULL,
  `is_invoice_to_org` tinyint(1) DEFAULT '0',
  `bill_to` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `new_amount` decimal(20,4) DEFAULT NULL,
  `renewal_id` bigint DEFAULT NULL,
  `cust_ref_id` bigint DEFAULT NULL,
  `is_trial_plan` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`cust_plan_mappping_id`),
  UNIQUE KEY `cust_package_rel_id_unq` (`cust_plan_mappping_id`),
  KEY `lead_cust_packeage_rel_lead_master_id_fk` (`lead_master_id`),
  CONSTRAINT `lead_cust_packeage_rel_lead_master_id_fk` FOREIGN KEY (`lead_master_id`) REFERENCES `tblmleadmaster` (`lead_master_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
