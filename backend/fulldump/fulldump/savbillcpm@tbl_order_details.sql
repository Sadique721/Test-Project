-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbl_order_details
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbl_order_details`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbl_order_details` (
  `orderid` bigint NOT NULL AUTO_INCREMENT,
  `entityid` bigint DEFAULT NULL,
  `pgid` bigint DEFAULT NULL,
  `ordertype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `finalamount` decimal(10,2) DEFAULT NULL,
  `basicamount` decimal(10,2) DEFAULT NULL,
  `taxamount` decimal(10,2) DEFAULT NULL,
  `orderdesc` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `createdbystaffid` decimal(20,0) DEFAULT NULL,
  `createdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifiedbystaffid` decimal(20,0) DEFAULT NULL,
  `lastmodifieddate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `cust_id` bigint DEFAULT NULL,
  `partner_id` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `balanced_used` double DEFAULT NULL,
  `is_balance_used` tinyint(1) DEFAULT NULL,
  `ledger_details_id` bigint DEFAULT NULL,
  `is_settled` tinyint(1) DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `purchase_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  PRIMARY KEY (`orderid`),
  UNIQUE KEY `order_details_orderid_unq` (`orderid`),
  KEY `order_details_pgid_fk` (`pgid`),
  KEY `tbl_order_details_ibfk_1` (`MVNOID`),
  CONSTRAINT `order_details_pgid_fk` FOREIGN KEY (`pgid`) REFERENCES `tbl_payment_gateway` (`pgid`),
  CONSTRAINT `tbl_order_details_ibfk_1` FOREIGN KEY (`MVNOID`) REFERENCES `tblmmvno` (`MVNOID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
