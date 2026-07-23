-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tbltcreditdoc
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltcreditdoc`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltcreditdoc` (
  `CREDITDOCID` bigint NOT NULL AUTO_INCREMENT,
  `paymode` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `paymentdate` timestamp NULL DEFAULT NULL,
  `chequedate` timestamp NULL DEFAULT NULL,
  `paydetails1` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `paydetails2` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `paydetails3` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `paydetails4` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `amount` decimal(20,4) DEFAULT NULL,
  `status` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `approverid` bigint DEFAULT NULL,
  `remarks` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `referenceno` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `xmldocument` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `custId` bigint DEFAULT NULL,
  `isDelete` tinyint(1) DEFAULT '0',
  `chequeNo` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bankName` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `branch` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tdsflag` tinyint(1) DEFAULT NULL,
  `tdsamount` decimal(20,4) DEFAULT NULL,
  `is_reversed` tinyint(1) DEFAULT NULL,
  `resevrsed_date` timestamp NULL DEFAULT NULL,
  `resverse_debitdoc_id` bigint DEFAULT NULL,
  `tds_received` tinyint(1) DEFAULT NULL,
  `tds_received_date` timestamp NULL DEFAULT NULL,
  `tds_credit_doc_id` bigint DEFAULT NULL,
  `adjustedAmount` decimal(20,4) DEFAULT NULL,
  `customerName` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `serviceAreaId` bigint DEFAULT NULL,
  `invoiceId` bigint DEFAULT NULL,
  `type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `paytype` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `batchAssigned` tinyint(1) DEFAULT NULL,
  `lead_master_id` bigint DEFAULT NULL,
  PRIMARY KEY (`CREDITDOCID`),
  UNIQUE KEY `credit_doc_id_unq` (`CREDITDOCID`),
  KEY `TBLTCREDITDOC_lead_master_id_fk` (`lead_master_id`),
  CONSTRAINT `TBLTCREDITDOC_lead_master_id_fk` FOREIGN KEY (`lead_master_id`) REFERENCES `tblmleadmaster` (`lead_master_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
