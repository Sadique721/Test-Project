-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltnetworkdevicebindings
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltnetworkdevicebindings`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltnetworkdevicebindings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `deviceid` bigint DEFAULT NULL,
  `porttype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parentdeviceid` bigint DEFAULT NULL,
  `CREATEDATE` timestamp NULL DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` bigint DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` bigint DEFAULT NULL,
  `inbind` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `outbind` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `current_device_port` bigint DEFAULT NULL,
  `other_device_port` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltnetworkdevicebindings_id_unq` (`id`),
  KEY `networkdevice_bindings_fk` (`deviceid`),
  KEY `networkdevice_parent_bindings_fk` (`parentdeviceid`),
  CONSTRAINT `networkdevice_bindings_fk` FOREIGN KEY (`deviceid`) REFERENCES `tblnetworkdevices` (`deviceid`),
  CONSTRAINT `networkdevice_parent_bindings_fk` FOREIGN KEY (`parentdeviceid`) REFERENCES `tblnetworkdevices` (`deviceid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
