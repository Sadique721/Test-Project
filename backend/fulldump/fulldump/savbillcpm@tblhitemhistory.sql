-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblhitemhistory
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblhitemhistory`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblhitemhistory` (
  `mac_mapping_id` bigint NOT NULL AUTO_INCREMENT,
  `inward_id` bigint DEFAULT NULL,
  `outward_id` bigint DEFAULT NULL,
  `cust_inventory_mapping_id` bigint DEFAULT NULL,
  `mac` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mvno_id` bigint DEFAULT NULL,
  `CREATEDATE` timestamp NULL DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` bigint DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `serial_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `current_approve_id` bigint DEFAULT NULL,
  `previous_approve_id` bigint DEFAULT NULL,
  `team_hierarchy_mapping_id` bigint DEFAULT NULL,
  `used_count` bigint DEFAULT '0',
  `inward_id_of_outward` bigint DEFAULT NULL,
  `is_forwarded` tinyint(1) DEFAULT '0',
  `is_returned` tinyint(1) DEFAULT '0',
  `remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `external_item_id` bigint DEFAULT NULL,
  `item_id` bigint DEFAULT NULL,
  `inventory_mapping_id` bigint DEFAULT NULL,
  `bulkconsumption_id` bigint DEFAULT NULL,
  `non_serialized_item_id` bigint DEFAULT NULL,
  PRIMARY KEY (`mac_mapping_id`),
  UNIQUE KEY `mac_mapping_id` (`mac_mapping_id`),
  KEY `idxitemhistoryid` (`item_id`),
  KEY `idxitemhistorymacid` (`mac_mapping_id`),
  KEY `tblm_in_out_ward_mac_mapping_cust_inventory_mapping_id_new_fk` (`cust_inventory_mapping_id`),
  KEY `tblm_in_out_ward_mac_mapping_inventory_mapping_id_new_fk` (`inventory_mapping_id`),
  KEY `tblm_in_out_ward_mac_mapping_inward_id_new_fk` (`inward_id`),
  KEY `tblm_in_out_ward_mac_mapping_inward_id_of_outward_new_fk` (`inward_id_of_outward`),
  KEY `tblm_in_out_ward_mac_mapping_outward_id_new_fk` (`outward_id`),
  CONSTRAINT `tblm_in_out_ward_mac_mapping_inventory_mapping_id_new_fk` FOREIGN KEY (`inventory_mapping_id`) REFERENCES `tblm_inventory_mapping` (`mapping_id`),
  CONSTRAINT `tblm_in_out_ward_mac_mapping_inward_id_new_fk` FOREIGN KEY (`inward_id`) REFERENCES `tbltinward` (`inward_id`),
  CONSTRAINT `tblm_in_out_ward_mac_mapping_inward_id_of_outward_new_fk` FOREIGN KEY (`inward_id_of_outward`) REFERENCES `tbltinward` (`inward_id`),
  CONSTRAINT `tblm_in_out_ward_mac_mapping_outward_id_new_fk` FOREIGN KEY (`outward_id`) REFERENCES `tbltoutward` (`outward_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
