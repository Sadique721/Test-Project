-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillinventorymanagement    Table: tblmnetworkdevices
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmnetworkdevices`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmnetworkdevices` (
  `deviceid` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `servicearea_id` bigint DEFAULT NULL,
  `devicetype` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `parent_network_device_id` bigint DEFAULT NULL,
  `total_in_ports` bigint DEFAULT '-1',
  `available_in_ports` bigint DEFAULT '-1',
  `total_out_ports` bigint DEFAULT NULL,
  `available_out_ports` bigint DEFAULT NULL,
  `inward_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `item_id` bigint DEFAULT NULL,
  `customer_inventory_id` bigint DEFAULT NULL,
  `inventory_mapping_id` bigint DEFAULT NULL,
  `total_ports` bigint DEFAULT NULL,
  `available_ports` bigint DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MVNOID` bigint DEFAULT NULL,
  `latitude` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `longitude` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createdbystaffid` bigint DEFAULT NULL,
  `lastmodifiedbystaffid` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `displayname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`deviceid`),
  UNIQUE KEY `tblmnetworkdevices_deviceid_unq` (`deviceid`),
  KEY `networkdevices_mvno_id_fk` (`MVNOID`),
  CONSTRAINT `networkdevices_mvno_id_fk` FOREIGN KEY (`MVNOID`) REFERENCES `tblmmvno` (`MVNOID`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
