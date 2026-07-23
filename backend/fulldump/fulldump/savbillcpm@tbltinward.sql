-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltinward
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltinward`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltinward` (
  `inward_id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint DEFAULT NULL,
  `destination_id` bigint DEFAULT NULL,
  `mvno_id` bigint DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `quantity` bigint DEFAULT NULL,
  `used_qty` bigint DEFAULT '0',
  `unused_qty` bigint DEFAULT NULL,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `inward_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `inward_date_time` timestamp NULL DEFAULT NULL,
  `CREATEDATE` timestamp NULL DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` bigint DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `source_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `source_id` bigint DEFAULT NULL,
  `destination_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `in_transit_qty` bigint DEFAULT NULL,
  `service_area_id` bigint DEFAULT NULL,
  `outward_id` bigint DEFAULT NULL,
  `out_transit_qty` bigint DEFAULT NULL,
  `rejected_qty` bigint DEFAULT NULL,
  `approval_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `category_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rms_inward_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nav_inward_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `total_mac_serial` bigint DEFAULT NULL,
  `approval_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `assign_non_serialized_item_qty` bigint DEFAULT NULL,
  `request_inventory_id` bigint DEFAULT NULL,
  PRIMARY KEY (`inward_id`),
  UNIQUE KEY `inward_id` (`inward_id`),
  KEY `inward_product_fk` (`product_id`),
  KEY `inward_warehouse_fk` (`destination_id`),
  KEY `inward_outward_id_fk` (`outward_id`),
  KEY `tbltinward_servicearea_id_fk` (`service_area_id`),
  CONSTRAINT `inward_outward_id_fk` FOREIGN KEY (`outward_id`) REFERENCES `tbltoutward` (`outward_id`),
  CONSTRAINT `inward_product_fk` FOREIGN KEY (`product_id`) REFERENCES `tbltproduct` (`product_id`),
  CONSTRAINT `tbltinward_servicearea_id_fk` FOREIGN KEY (`service_area_id`) REFERENCES `tblservicearea` (`service_area_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
