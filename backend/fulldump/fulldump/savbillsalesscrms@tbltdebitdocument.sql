-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tbltdebitdocument
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltdebitdocument`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltdebitdocument` (
  `debitdocumentid` bigint NOT NULL AUTO_INCREMENT,
  `docnumber` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `lead_master_id` bigint DEFAULT NULL,
  `planId` bigint DEFAULT NULL,
  `billdate` timestamp NULL DEFAULT NULL,
  `startdate` timestamp NULL DEFAULT NULL,
  `endate` timestamp NULL DEFAULT NULL,
  `duedate` timestamp NULL DEFAULT NULL,
  `latepaymentdate` timestamp NULL DEFAULT NULL,
  `subtotal` decimal(20,4) DEFAULT NULL,
  `tax` decimal(20,4) DEFAULT NULL,
  `discount` decimal(20,4) DEFAULT NULL,
  `totalamount` decimal(20,4) DEFAULT NULL,
  `previousbalance` decimal(20,4) DEFAULT NULL,
  `latepaymentfee` decimal(20,4) DEFAULT NULL,
  `currentpayment` decimal(20,4) DEFAULT NULL,
  `currentdebit` decimal(20,4) DEFAULT NULL,
  `currentcredit` decimal(20,4) DEFAULT NULL,
  `totaldue` decimal(20,4) DEFAULT NULL,
  `amountinwords` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dueinwords` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `billrunid` bigint DEFAULT NULL,
  `billrunstatus` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `document` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `isDelete` tinyint(1) DEFAULT NULL,
  `cstchargeid` bigint DEFAULT NULL,
  `custid` bigint DEFAULT NULL,
  `customerName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `custType` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `paymentStatus` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `adjustedAmount` decimal(20,4) DEFAULT NULL,
  PRIMARY KEY (`debitdocumentid`),
  UNIQUE KEY `debit_document_id_unq` (`debitdocumentid`),
  KEY `TBLTDEBITDOCUMENT_lead_master_id_fk` (`lead_master_id`),
  CONSTRAINT `TBLTDEBITDOCUMENT_lead_master_id_fk` FOREIGN KEY (`lead_master_id`) REFERENCES `tblmleadmaster` (`lead_master_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
