-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillintegrationsystem    Table: tblmitemhistory
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmitemhistory`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmitemhistory` (
  `mac_mapping_id` bigint NOT NULL AUTO_INCREMENT,
  `inward_id` bigint DEFAULT NULL,
  `outward_id` bigint DEFAULT NULL,
  `cust_inventory_mapping_id` bigint DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mac` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `serial_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `current_approve_id` bigint DEFAULT NULL,
  `previous_approve_id` bigint DEFAULT NULL,
  `team_hierarchy_mapping_id` bigint DEFAULT NULL,
  `used_count` bigint DEFAULT NULL,
  `inward_id_of_outward` bigint DEFAULT NULL,
  `is_forwarded` bigint DEFAULT NULL,
  `is_returned` bigint DEFAULT NULL,
  `external_item_id` bigint DEFAULT NULL,
  `item_id` bigint DEFAULT NULL,
  `inventory_mapping_id` bigint DEFAULT NULL,
  `bulkconsumption_id` bigint DEFAULT NULL,
  `non_serialized_item_id` bigint DEFAULT NULL,
  `remark` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDATE` timestamp NULL DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` bigint DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` bigint DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  PRIMARY KEY (`mac_mapping_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
