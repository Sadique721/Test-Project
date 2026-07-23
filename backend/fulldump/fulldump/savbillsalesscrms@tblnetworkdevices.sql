-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tblnetworkdevices
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblnetworkdevices`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblnetworkdevices` (
  `deviceid` bigint NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `servicearea_id` bigint DEFAULT NULL,
  `devicetype` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `MVNOID` bigint DEFAULT NULL,
  `latitude` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `longitude` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_network_device_id` bigint DEFAULT NULL,
  `total_in_ports` bigint DEFAULT NULL,
  `available_in_ports` bigint DEFAULT NULL,
  `total_out_ports` bigint DEFAULT NULL,
  `available_out_ports` bigint DEFAULT NULL,
  PRIMARY KEY (`deviceid`),
  UNIQUE KEY `networkdevices_deviceid_unq` (`deviceid`),
  KEY `tblnetworkdevices_fk` (`servicearea_id`),
  CONSTRAINT `tblnetworkdevices_fk` FOREIGN KEY (`servicearea_id`) REFERENCES `tblservicearea` (`service_area_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
