-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbl_purchase_details
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbl_purchase_details`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbl_purchase_details` (
  `purchaseid` bigint NOT NULL AUTO_INCREMENT,
  `orderid` bigint NOT NULL,
  `custid` bigint DEFAULT NULL,
  `pgid` bigint NOT NULL,
  `partnerid` bigint DEFAULT NULL,
  `amount` decimal(10,2) DEFAULT NULL,
  `paymentstatus` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `transid` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pg_res_status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `purchase_status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `purchasedate` timestamp NULL DEFAULT NULL,
  `trans_res_date` timestamp NULL DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `createdbystaffid` decimal(20,0) DEFAULT NULL,
  `createdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifiedbystaffid` decimal(20,0) DEFAULT NULL,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `pgtransid` bigint DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`purchaseid`),
  UNIQUE KEY `purchase_details_purchaseid_unq` (`purchaseid`),
  KEY `tbl_purchase_details_orderid_fk` (`orderid`),
  KEY `tbl_purchase_details_custid_fk` (`custid`),
  KEY `tbl_purchase_details_pgid_fk` (`pgid`),
  KEY `tbl_purchase_details_partnerid_fk` (`partnerid`),
  CONSTRAINT `tbl_purchase_details_custid_fk` FOREIGN KEY (`custid`) REFERENCES `tblcustomers` (`custid`),
  CONSTRAINT `tbl_purchase_details_orderid_fk` FOREIGN KEY (`orderid`) REFERENCES `tbl_order_details` (`orderid`),
  CONSTRAINT `tbl_purchase_details_partnerid_fk` FOREIGN KEY (`partnerid`) REFERENCES `tblpartners` (`PARTNERID`),
  CONSTRAINT `tbl_purchase_details_pgid_fk` FOREIGN KEY (`pgid`) REFERENCES `tbl_payment_gateway` (`pgid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
