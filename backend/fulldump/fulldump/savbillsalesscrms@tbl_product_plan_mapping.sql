-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tbl_product_plan_mapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbl_product_plan_mapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbl_product_plan_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `plan_id` bigint DEFAULT NULL,
  `product_category_id` bigint DEFAULT NULL,
  `product_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `revised_charge` decimal(20,4) DEFAULT NULL,
  `ownership_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `product_quantity` bigint DEFAULT NULL,
  `apig_product_plan_mapping_id` bigint DEFAULT NULL,
  `CREATEDATE` timestamp NULL DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` bigint DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pro_map_f1` (`product_id`),
  KEY `pro_map_f2` (`plan_id`),
  CONSTRAINT `pro_map_f1` FOREIGN KEY (`product_id`) REFERENCES `tbltproduct` (`product_id`),
  CONSTRAINT `pro_map_f2` FOREIGN KEY (`plan_id`) REFERENCES `tblmpostpaidplan` (`POSTPAIDPLANID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
