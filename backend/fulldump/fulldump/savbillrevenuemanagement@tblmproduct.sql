-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillrevenuemanagement    Table: tblmproduct
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmproduct`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmproduct` (
  `product_id` bigint NOT NULL,
  `name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `unit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mvno_id` bigint DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdbystaffid` bigint DEFAULT NULL,
  `lastmodifiedbystaffid` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `expiry_time` decimal(10,0) DEFAULT NULL,
  `expiry_time_unit` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pc_id` bigint DEFAULT NULL,
  `total_in_ports` bigint DEFAULT '-1',
  `available_in_ports` bigint DEFAULT '-1',
  `total_out_ports` bigint DEFAULT '-1',
  `available_out_ports` bigint DEFAULT '-1',
  `rms_product_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nav_ledger_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `refurb_prod_charge_id` bigint DEFAULT NULL,
  `refurb_pra_in_wrty` double DEFAULT NULL,
  `new_prod_charge_id` bigint DEFAULT NULL,
  `refurb_pra_post_wrty` double DEFAULT NULL,
  `new_pra_in_wrty` double DEFAULT NULL,
  `new_pra_post_wrty` double DEFAULT NULL,
  `case_id` bigint DEFAULT NULL,
  `vendorid` bigint DEFAULT NULL,
  `actualpricenewproduct` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `actualpricerefurbishedproduct` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `tblmproduct_product_id_unq` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
