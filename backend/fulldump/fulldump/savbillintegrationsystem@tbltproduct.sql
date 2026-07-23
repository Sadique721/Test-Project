-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillintegrationsystem    Table: tbltproduct
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltproduct`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltproduct` (
  `product_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mvno_id` bigint DEFAULT NULL,
  `total_in_ports` bigint DEFAULT NULL,
  `available_in_ports` bigint DEFAULT NULL,
  `total_out_ports` bigint DEFAULT NULL,
  `available_out_ports` bigint DEFAULT NULL,
  `rms_product_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nav_ledger_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `refurb_prod_charge_id` bigint DEFAULT NULL,
  `new_prod_charge_id` bigint DEFAULT NULL,
  `refurb_pra_in_wrty` bigint DEFAULT NULL,
  `refurb_pra_post_wrty` bigint DEFAULT NULL,
  `new_pra_in_wrty` bigint DEFAULT NULL,
  `new_pra_post_wrty` bigint DEFAULT NULL,
  `case_id` bigint DEFAULT NULL,
  `vendorid` bigint DEFAULT NULL,
  `actualpricenewproduct` bigint DEFAULT NULL,
  `actualpricerefurbishedproduct` bigint DEFAULT NULL,
  `expiry_time` bigint DEFAULT NULL,
  `expiry_time_unit` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pc_id` bigint DEFAULT NULL,
  `CREATEDATE` timestamp NULL DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` bigint DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` bigint DEFAULT NULL,
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
