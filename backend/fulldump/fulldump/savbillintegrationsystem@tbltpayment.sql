-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillintegrationsystem    Table: tbltpayment
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
  `customer_uuid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_scheduled` tinyint DEFAULT '0',
  `invoice_id` bigint DEFAULT NULL,
  `created_by_name` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `created_by_id` bigint DEFAULT NULL,
  `is_advance_payment` tinyint DEFAULT '0',
  `failure_description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `account_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wallet_amount` decimal(20,2) DEFAULT NULL,
  `plan_price` decimal(20,2) DEFAULT NULL,
  `gateway_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `payer_mobile_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `auto_payment_initiator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `commission` decimal(20,2) DEFAULT NULL,
  `child_id` bigint DEFAULT NULL,
  UNIQUE KEY `orderid` (`orderid`),
  UNIQUE KEY `uk_tbltpayment_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
