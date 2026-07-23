-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: commongateway    Table: tbltcreditdoc
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltcreditdoc`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltcreditdoc` (
  `CREDITDOCID` bigint NOT NULL AUTO_INCREMENT,
  `CUSTID` bigint NOT NULL,
  `PAYMENTDATE` timestamp NULL DEFAULT NULL,
  `PAYMODE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `PAYDETAILS1` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `PAYDETAILS2` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `PAYDETAILS3` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `PAYDETAILS4` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `AMOUNT` decimal(20,4) DEFAULT NULL,
  `STATUS` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `APPROVEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `REMARKS` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CREATEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `referenceno` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `xmldocument` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `chequedate` timestamp NULL DEFAULT NULL,
  `is_delete` tinyint(1) NOT NULL DEFAULT '0',
  `tdsflag` tinyint(1) DEFAULT NULL,
  `tdsamount` decimal(20,0) DEFAULT NULL,
  `is_reversed` tinyint(1) DEFAULT NULL,
  `resevrsed_date` datetime DEFAULT NULL,
  `resverse_debitdoc_id` bigint DEFAULT NULL,
  `tds_received` tinyint(1) DEFAULT NULL,
  `tds_received_date` datetime DEFAULT NULL,
  `tds_credit_doc_id` bigint DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  `payment_status` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `invoiceid` bigint DEFAULT NULL,
  `type` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `adjustedamount` double DEFAULT NULL,
  `paytype` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bankid` bigint DEFAULT NULL,
  `BUID` bigint DEFAULT NULL,
  `next_team_hir_mapping` bigint DEFAULT NULL,
  `receipt_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `filename` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `uniquename` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `barteramount` decimal(20,4) DEFAULT NULL,
  `lcoid` bigint DEFAULT NULL,
  `tds_amount` decimal(20,4) DEFAULT NULL,
  `abbs_amount` double DEFAULT NULL,
  `print_counter` bigint DEFAULT '0',
  `branch` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `destination_bank` bigint DEFAULT NULL,
  `onlinesource` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `creditdocumentno` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `paymentreferenceno` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ledger_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`CREDITDOCID`),
  UNIQUE KEY `tcreditdoc_creditdocid_unq` (`CREDITDOCID`),
  KEY `CREDITDOC_mvno_id_fk` (`MVNOID`),
  CONSTRAINT `CREDITDOC_mvno_id_fk` FOREIGN KEY (`MVNOID`) REFERENCES `tblmmvno` (`MVNOID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
