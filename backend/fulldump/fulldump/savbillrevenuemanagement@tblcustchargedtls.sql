-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillrevenuemanagement    Table: tblcustchargedtls
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblcustchargedtls`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblcustchargedtls` (
  `cstchargeid` bigint NOT NULL,
  `custid` bigint NOT NULL,
  `planid` bigint DEFAULT NULL,
  `chargeid` bigint NOT NULL,
  `chargetype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` decimal(20,4) DEFAULT NULL,
  `actual_price` decimal(20,4) DEFAULT NULL,
  `validity` decimal(20,4) DEFAULT NULL,
  `CREATEDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CREATEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `charge_date` datetime DEFAULT NULL,
  `taxamount` decimal(20,4) DEFAULT NULL,
  `is_reversed` tinyint(1) DEFAULT '0',
  `rev_date` date DEFAULT NULL,
  `rev_amt` decimal(20,4) DEFAULT NULL,
  `rev_remarks` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `startdate` datetime DEFAULT NULL,
  `enddate` datetime DEFAULT NULL,
  `is_used` tinyint(1) DEFAULT NULL,
  `purchase_entity_id` bigint DEFAULT NULL,
  `debitdocid` bigint DEFAULT NULL,
  `ippooldtlsid` bigint DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `remarks` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `custpackageid` bigint DEFAULT NULL,
  `taxid` bigint DEFAULT NULL,
  `type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `planvalidity` bigint DEFAULT NULL,
  `unitsofvalidity` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `lastbilldate` timestamp NULL DEFAULT NULL,
  `nextbilldate` timestamp NULL DEFAULT NULL,
  `billingcycle` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `dbr` double DEFAULT NULL,
  `discount` bigint DEFAULT NULL,
  `bill_to` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'CUSTOMER',
  `is_invoice_to_org` tinyint(1) DEFAULT '0',
  `new_amount` decimal(20,4) DEFAULT NULL,
  `billable_cust_id` bigint DEFAULT NULL,
  `static_ip_address` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `connection_no` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `installment_frequency` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `installment_no` int DEFAULT NULL,
  `total_installments` int DEFAULT NULL,
  `installment_start_date` date DEFAULT NULL,
  `amount_per_installment` decimal(20,4) DEFAULT NULL,
  `next_installment_date` date DEFAULT NULL,
  `last_installment_date` date DEFAULT NULL,
  `installment_enabled` bit(1) DEFAULT NULL,
  PRIMARY KEY (`cstchargeid`),
  UNIQUE KEY `custchargedtls_cstchargeid_unq` (`cstchargeid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
