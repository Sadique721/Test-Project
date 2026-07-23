-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblleadcustchargedtls
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblleadcustchargedtls`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblleadcustchargedtls` (
  `cstchargeid` bigint NOT NULL,
  `planid` bigint DEFAULT NULL,
  `chargeid` bigint DEFAULT NULL,
  `charge_name` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `chargetype` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `validity` decimal(20,4) DEFAULT NULL,
  `price` decimal(20,4) DEFAULT NULL,
  `actualprice` decimal(20,4) DEFAULT NULL,
  `lead_master_id` bigint DEFAULT NULL,
  `remarks` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `charge_date` date DEFAULT NULL,
  `charge_date_string` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `startdate` date DEFAULT NULL,
  `startdate_string` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `enddate` date DEFAULT NULL,
  `enddate_string` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `taxamount` decimal(20,4) DEFAULT NULL,
  `is_reversed` tinyint(1) DEFAULT NULL,
  `rev_date` timestamp NULL DEFAULT NULL,
  `revdate_string` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rev_amt` decimal(20,4) DEFAULT NULL,
  `rev_remarks` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_used` tinyint(1) DEFAULT NULL,
  `purchase_entity_id` bigint DEFAULT NULL,
  `ippooldtlsid` bigint DEFAULT NULL,
  `debitdocid` bigint DEFAULT NULL,
  `create_date_string` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_date_string` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `plan_validity` bigint DEFAULT NULL,
  `units_of_validity` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tax_id` bigint DEFAULT NULL,
  `cust_plan_mappping_id` bigint DEFAULT NULL,
  `last_bill_date` timestamp NULL DEFAULT NULL,
  `next_bill_date` timestamp NULL DEFAULT NULL,
  `billing_cycle` bigint DEFAULT NULL,
  PRIMARY KEY (`cstchargeid`),
  UNIQUE KEY `cust_charge_dtls_id_unq` (`cstchargeid`),
  KEY `tblleadcustchargedtls_lead_master_id_fk` (`lead_master_id`),
  CONSTRAINT `tblleadcustchargedtls_lead_master_id_fk` FOREIGN KEY (`lead_master_id`) REFERENCES `tblmleadmaster` (`lead_master_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
