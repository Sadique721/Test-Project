-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblnetworkdevices
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblnetworkdevices`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblnetworkdevices` (
  `deviceid` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `servicearea_id` bigint DEFAULT NULL,
  `devicetype` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT NULL,
  `CREATEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  `latitude` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `longitude` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_network_device_id` bigint DEFAULT NULL,
  `total_in_ports` bigint DEFAULT NULL,
  `available_in_ports` bigint DEFAULT NULL,
  `total_out_ports` bigint DEFAULT NULL,
  `available_out_ports` bigint DEFAULT NULL,
  `inward_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `item_id` bigint DEFAULT NULL,
  `customer_inventory_id` bigint DEFAULT NULL,
  `inventory_mapping_id` bigint DEFAULT NULL,
  `total_ports` bigint DEFAULT NULL,
  `available_ports` bigint DEFAULT NULL,
  PRIMARY KEY (`deviceid`),
  UNIQUE KEY `networkdevices_deviceid_unq` (`deviceid`),
  KEY `parent_network_device_id_fk` (`parent_network_device_id`),
  KEY `tblnetworkdevices_inward_inward_fk` (`inward_id`),
  KEY `tblnetworkdevices_product_product_fk` (`product_id`),
  KEY `idxnetworkdevicesid` (`servicearea_id`),
  KEY `tblnetworkdevices_ibfk_1` (`MVNOID`),
  CONSTRAINT `parent_network_device_id_fk` FOREIGN KEY (`parent_network_device_id`) REFERENCES `tblnetworkdevices` (`deviceid`),
  CONSTRAINT `tblnetworkdevices_ibfk_1` FOREIGN KEY (`MVNOID`) REFERENCES `tblmmvno` (`MVNOID`),
  CONSTRAINT `tblnetworkdevices_inward_inward_fk` FOREIGN KEY (`inward_id`) REFERENCES `tbltinward` (`inward_id`),
  CONSTRAINT `tblnetworkdevices_product_product_fk` FOREIGN KEY (`product_id`) REFERENCES `tbltproduct` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
