-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltpayment
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltpayment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltpayment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `orderid` bigint NOT NULL,
  `custid` bigint DEFAULT NULL,
  `payment` decimal(20,2) DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pgtransactionid` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `linkid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `payment_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `plan_id` bigint DEFAULT NULL,
  `is_from_captive` bit(1) DEFAULT b'0',
  `merchant_name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `transaction_date` timestamp NULL DEFAULT NULL,
  `customer_user_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mvnoid` bigint DEFAULT NULL,
  `buid` bigint DEFAULT NULL,
  `creditdocid` bigint DEFAULT NULL,
  `paymentlink` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `checksum` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `partnerid` bigint DEFAULT NULL,
  `partner_payment_id` bigint DEFAULT NULL,
  `account_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `orderid` (`orderid`),
  UNIQUE KEY `tbltpayment_field_unq` (`id`),
  KEY `tblcust_payment_mapping_fk` (`custid`),
  CONSTRAINT `tblcust_payment_mapping_fk` FOREIGN KEY (`custid`) REFERENCES `tblcustomers` (`custid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
