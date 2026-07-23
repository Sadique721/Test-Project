-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tbltclients
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltclients`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltclients` (
  `clientid` bigint NOT NULL AUTO_INCREMENT,
  `clientip` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sharedkey` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `timeout` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `iptype` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `clientgroupid` bigint DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT NULL,
  `lastmodificationdate` timestamp NULL DEFAULT NULL,
  `mvnoid` bigint DEFAULT NULL,
  `accept_on_ip_not_found` bit(1) DEFAULT b'0',
  `radius_attribute` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `idle_timeout` bigint DEFAULT NULL,
  `snmpclientid` bigint DEFAULT NULL,
  `snmpenable` bit(1) DEFAULT b'0',
  `session_purge_interval` bigint DEFAULT NULL,
  `vendor` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `deviceid` bigint DEFAULT NULL,
  `acctonattribute` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'NAS-Identifier',
  `clientname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`clientid`),
  UNIQUE KEY `clientip_mvno_unq` (`mvnoid`,`clientip`),
  KEY `clientgroupid_id_fk` (`clientgroupid`),
  KEY `snmpclientid_id_fk` (`snmpclientid`),
  KEY `device_client_fk` (`deviceid`),
  CONSTRAINT `clientgroupid_id_fk` FOREIGN KEY (`clientgroupid`) REFERENCES `tblmclientgroup` (`clientgroupid`),
  CONSTRAINT `device_client_fk` FOREIGN KEY (`deviceid`) REFERENCES `tblmdevice` (`deviceid`),
  CONSTRAINT `snmpclientid_id_fk` FOREIGN KEY (`snmpclientid`) REFERENCES `tbltsnmpclientprofile` (`snmpclientid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
