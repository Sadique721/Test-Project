-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tblmradiusprofile
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmradiusprofile`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmradiusprofile` (
  `radiusprofileid` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `checkitem` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `accountcdrstatus` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sessionstatus` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mappingmasterid` bigint DEFAULT NULL,
  `priority` bigint DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT NULL,
  `lastmodificationdate` timestamp NULL DEFAULT NULL,
  `requesttype` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `authaudit` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `proxyserverid` bigint DEFAULT NULL,
  `mvnoid` bigint DEFAULT NULL,
  `auto_provision_mac` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'Disable',
  `device_driver_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'Savbill BSS',
  `authentication_mode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'username',
  `authentication_type` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'PAP',
  `authentication_sub_type` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ispasswordcheckrequired` bit(1) DEFAULT b'1',
  `username_identity_regex` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `customer_username_attribute` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'User-Name',
  `terminatesessiononduplicatemac` bit(1) DEFAULT b'0',
  `addlivesessiononinterim` bit(1) DEFAULT b'0',
  `disconnectsessiononinterim` bit(1) DEFAULT b'0',
  PRIMARY KEY (`radiusprofileid`),
  UNIQUE KEY `radiusprofile_name_mvno_unq` (`mvnoid`,`name`),
  KEY `proxyserverid_id_fk` (`proxyserverid`),
  CONSTRAINT `proxyserverid_id_fk` FOREIGN KEY (`proxyserverid`) REFERENCES `tbltproxyserver` (`proxyserverid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
