-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltcustchargehistory
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltcustchargehistory`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltcustchargehistory` (
  `history_id` bigint NOT NULL AUTO_INCREMENT,
  `cust_id` bigint DEFAULT NULL,
  `plan_id` bigint DEFAULT NULL,
  `charge_id` bigint DEFAULT NULL,
  `charge_amount` double DEFAULT NULL,
  `tax_id` bigint DEFAULT NULL,
  `tax_amount` double DEFAULT NULL,
  `CREATEDATE` timestamp NULL DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` bigint DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` bigint DEFAULT NULL,
  `discount` double DEFAULT NULL,
  `cust_plan_mapping_id` bigint DEFAULT NULL,
  `plan_group_id` bigint DEFAULT NULL,
  `plan_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `charge_name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `charge_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `charge_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `billing_cycle` bigint DEFAULT NULL,
  `saccode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `next_charge_billdate` timestamp NULL DEFAULT NULL,
  `last_charge_billdate` timestamp NULL DEFAULT NULL,
  `charge_bill_day` bigint DEFAULT NULL,
  `is_first_charge_apply` bit(1) DEFAULT NULL,
  `is_royalty_apply` bit(1) DEFAULT b'0',
  PRIMARY KEY (`history_id`),
  UNIQUE KEY `custchargehistory_history_id_unq` (`history_id`),
  KEY `tbltcustchargehistory_FK_02` (`plan_id`),
  KEY `tbltcustchargehistory_FK_03` (`charge_id`),
  KEY `tbltcustchargehistory_FK_04` (`tax_id`),
  KEY `cust_plan_mapping_id_fk` (`cust_plan_mapping_id`),
  KEY `plan_group_id_fk` (`plan_group_id`),
  KEY `idx_custid_chargeid` (`cust_id`,`charge_id`),
  KEY `index_tbltcustchargehistory_custId_chargeId` (`cust_id`,`charge_id`),
  KEY `index_tbltcustchargehistory_custId` (`cust_id`),
  KEY `tbltcustchargehistory_custId` (`cust_id`),
  CONSTRAINT `cust_plan_mapping_id_fk` FOREIGN KEY (`cust_plan_mapping_id`) REFERENCES `tblcustpackagerel` (`custpackageid`),
  CONSTRAINT `plan_group_id_fk` FOREIGN KEY (`plan_group_id`) REFERENCES `tblmplangroup` (`plangroupid`),
  CONSTRAINT `tbltcustchargehistory_FK_01` FOREIGN KEY (`cust_id`) REFERENCES `tblcustomers` (`custid`),
  CONSTRAINT `tbltcustchargehistory_FK_02` FOREIGN KEY (`plan_id`) REFERENCES `tblmpostpaidplan` (`POSTPAIDPLANID`),
  CONSTRAINT `tbltcustchargehistory_FK_03` FOREIGN KEY (`charge_id`) REFERENCES `tblcharges` (`CHARGEID`),
  CONSTRAINT `tbltcustchargehistory_FK_04` FOREIGN KEY (`tax_id`) REFERENCES `tblmtax` (`TAXID`)
) ENGINE=InnoDB AUTO_INCREMENT=56969 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
