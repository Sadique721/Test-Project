-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tblcustchargedtls
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblcustchargedtls`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblcustchargedtls` (
  `cstchargeid` bigint NOT NULL AUTO_INCREMENT,
  `planid` bigint DEFAULT NULL,
  `chargeid` bigint DEFAULT NULL,
  `chargeName` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `chargetype` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `validity` decimal(20,4) DEFAULT NULL,
  `price` decimal(20,4) DEFAULT NULL,
  `actualprice` decimal(20,4) DEFAULT NULL,
  `lead_master_id` bigint DEFAULT NULL,
  `remarks` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `charge_date` date DEFAULT NULL,
  `chargeDateString` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `startdate` date DEFAULT NULL,
  `startdateString` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `enddate` date DEFAULT NULL,
  `enddateString` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `taxamount` decimal(20,4) DEFAULT NULL,
  `is_reversed` tinyint(1) DEFAULT NULL,
  `rev_date` timestamp NULL DEFAULT NULL,
  `revdateString` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rev_amt` decimal(20,4) DEFAULT NULL,
  `rev_remarks` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `isUsed` tinyint(1) DEFAULT NULL,
  `purchaseEntityId` bigint DEFAULT NULL,
  `ippooldtlsid` bigint DEFAULT NULL,
  `debitdocid` bigint DEFAULT NULL,
  `createDateString` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updateDateString` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `planValidity` bigint DEFAULT NULL,
  `unitsOfValidity` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `taxId` bigint DEFAULT NULL,
  `custPlanMapppingId` bigint DEFAULT NULL,
  `lastBillDate` timestamp NULL DEFAULT NULL,
  `nextBillDate` timestamp NULL DEFAULT NULL,
  `billingCycle` bigint DEFAULT NULL,
  `discount` double(5,2) DEFAULT '0.00',
  PRIMARY KEY (`cstchargeid`),
  UNIQUE KEY `cust_charge_dtls_id_unq` (`cstchargeid`),
  KEY `tblcustchargedtls_lead_master_id_fk` (`lead_master_id`),
  CONSTRAINT `tblcustchargedtls_lead_master_id_fk` FOREIGN KEY (`lead_master_id`) REFERENCES `tblmleadmaster` (`lead_master_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
