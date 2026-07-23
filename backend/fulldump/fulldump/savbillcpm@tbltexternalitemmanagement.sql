-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltexternalitemmanagement
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltexternalitemmanagement`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltexternalitemmanagement` (
  `external_item_id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint DEFAULT NULL,
  `servicearea_id` bigint DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `quantity` bigint DEFAULT NULL,
  `used_qty` bigint DEFAULT '0',
  `unused_qty` bigint DEFAULT NULL,
  `ownership_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDATE` timestamp NULL DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` bigint DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `in_transit_qty` bigint DEFAULT NULL,
  `rejected_qty` bigint DEFAULT NULL,
  `approval_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mvno_id` bigint DEFAULT NULL,
  `external_item_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `total_mac_serial` bigint DEFAULT NULL,
  `approval_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `owner_id` bigint DEFAULT NULL,
  PRIMARY KEY (`external_item_id`),
  UNIQUE KEY `external_item_id` (`external_item_id`),
  KEY `external_item_product_fk` (`product_id`),
  KEY `tbltexternalitemmanagement_servicearea_id_fk` (`servicearea_id`),
  CONSTRAINT `external_item_product_fk` FOREIGN KEY (`product_id`) REFERENCES `tbltproduct` (`product_id`),
  CONSTRAINT `tbltexternalitemmanagement_servicearea_id_fk` FOREIGN KEY (`servicearea_id`) REFERENCES `tblservicearea` (`service_area_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
