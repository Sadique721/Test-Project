-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillrevenuemanagement    Table: tbltdebitdocumentdetail
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltdebitdocumentdetail`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltdebitdocumentdetail` (
  `debitdocdetailid` bigint NOT NULL AUTO_INCREMENT,
  `debitdocumentid` bigint NOT NULL,
  `chargeid` bigint DEFAULT NULL,
  `chargename` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `chargetype` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `chargecycle` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `subtotal` decimal(20,4) DEFAULT '0.0000',
  `tax` decimal(20,4) DEFAULT '0.0000',
  `discount` decimal(20,4) DEFAULT '0.0000',
  `totalamount` decimal(20,4) DEFAULT '0.0000',
  `startdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `enddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `prorationtype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `noofcycle` decimal(20,4) DEFAULT '0.0000',
  `planid` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `inventory_mapping_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ledger_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `iccode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pushable_ledger_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cust_service_id` bigint DEFAULT NULL,
  `service_id` bigint DEFAULT NULL,
  `discount_percentage` double(10,2) DEFAULT '0.00',
  `offer_price` double(20,6) DEFAULT '0.000000',
  `mvnodebitdocumentid` bigint DEFAULT NULL,
  `current_installment_no` int DEFAULT NULL,
  `installment_charge` decimal(20,4) DEFAULT NULL,
  `installment_interest` decimal(20,4) DEFAULT '0.0000',
  PRIMARY KEY (`debitdocdetailid`),
  UNIQUE KEY `tdebitdocumentdetail_debitdocdetailid_unq` (`debitdocdetailid`),
  KEY `tbltdebitdocumentdetail_ibfk_1` (`debitdocumentid`),
  KEY `tbltdebitdocumentdetail_ibfk_2` (`chargeid`),
  KEY `index_tbltdebitdocumentdetail_mvnodebitdoc` (`mvnodebitdocumentid`),
  CONSTRAINT `tbltdebitdocumentdetail_ibfk_1` FOREIGN KEY (`debitdocumentid`) REFERENCES `tbltdebitdocument` (`debitdocumentid`),
  CONSTRAINT `tbltdebitdocumentdetail_ibfk_2` FOREIGN KEY (`chargeid`) REFERENCES `tblmcharges` (`chargeid`)
) ENGINE=InnoDB AUTO_INCREMENT=56354 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
