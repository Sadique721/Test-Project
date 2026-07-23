-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmstaffledgerdetails
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmstaffledgerdetails`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmstaffledgerdetails` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `staff_id` bigint DEFAULT NULL,
  `cust_id` bigint DEFAULT NULL,
  `credit_doc_id` bigint DEFAULT NULL,
  `payment_mode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `transaction_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `amount` decimal(20,4) DEFAULT NULL,
  `action` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bank_id` bigint DEFAULT NULL,
  `remarks` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CREATEDATE` timestamp NULL DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` bigint DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` bigint DEFAULT NULL,
  `transferredamount` double(20,8) DEFAULT NULL,
  `status` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `chequedate` datetime DEFAULT NULL,
  `chequeno` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bankName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tblmstaffledgerdetails_cust_id_fk` (`cust_id`),
  KEY `tblmstaffledgerdetails_creditdoc_id_fk` (`credit_doc_id`),
  KEY `tblmstaffledgerdetails_staff_id_fk` (`staff_id`),
  CONSTRAINT `tblmstaffledgerdetails_cust_id_fk` FOREIGN KEY (`cust_id`) REFERENCES `tblcustomers` (`custid`),
  CONSTRAINT `tblmstaffledgerdetails_staff_id_fk` FOREIGN KEY (`staff_id`) REFERENCES `tblstaffuser` (`staffid`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
