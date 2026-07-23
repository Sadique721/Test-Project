-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblm_inventory_mapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblm_inventory_mapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblm_inventory_mapping` (
  `mapping_id` bigint NOT NULL AUTO_INCREMENT,
  `quantity` bigint DEFAULT NULL,
  `owner_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `owner_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `outward_id` bigint DEFAULT NULL,
  `staff_id` bigint DEFAULT NULL,
  `assigned_date_time` timestamp NULL DEFAULT NULL,
  `mvno_id` bigint DEFAULT NULL,
  `CREATEDATE` timestamp NULL DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` bigint DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `approval_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `expiry_date_time` timestamp NULL DEFAULT NULL,
  `next_approver` bigint DEFAULT NULL,
  `team_hierarchy_mapping_id` bigint DEFAULT NULL,
  `previous_approve_id` bigint DEFAULT NULL,
  `inward_id` bigint DEFAULT NULL,
  `approval_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`mapping_id`),
  UNIQUE KEY `mapping_id` (`mapping_id`),
  KEY `tblm_inventory_mapping_product_fk` (`product_id`),
  KEY `tblm_inventory_mapping_outward_fk` (`outward_id`),
  KEY `tblm_inventory_mapping_staff_id_fk` (`staff_id`),
  CONSTRAINT `tblm_inventory_mapping_outward_fk` FOREIGN KEY (`outward_id`) REFERENCES `tbltoutward` (`outward_id`),
  CONSTRAINT `tblm_inventory_mapping_product_fk` FOREIGN KEY (`product_id`) REFERENCES `tbltproduct` (`product_id`),
  CONSTRAINT `tblm_inventory_mapping_staff_id_fk` FOREIGN KEY (`staff_id`) REFERENCES `tblstaffuser` (`staffid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
