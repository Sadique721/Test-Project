-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltshiftlocation
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltshiftlocation`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltshiftlocation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `customer_id` bigint DEFAULT NULL,
  `service_area_id` bigint DEFAULT NULL,
  `partner_id` double DEFAULT NULL,
  `transferable_commission` double DEFAULT NULL,
  `charge_id` bigint DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `discount` double DEFAULT NULL,
  `billable_customer_id` bigint DEFAULT NULL,
  `payment_owner` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `payment_owner_id` bigint DEFAULT NULL,
  `requested_by_id` bigint DEFAULT NULL,
  `requested_date` datetime DEFAULT NULL,
  `requested_by_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `transferable_balance` double(20,6) DEFAULT NULL,
  `branch_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltshiftlocation_field_unq` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
