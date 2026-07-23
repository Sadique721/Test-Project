-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tblmclientgroup
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmclientgroup`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmclientgroup` (
  `clientgroupid` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cgstatus` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT NULL,
  `lastmodificationdate` timestamp NULL DEFAULT NULL,
  `mvnoid` bigint DEFAULT NULL,
  `coadmprofileid` bigint DEFAULT NULL,
  `dmprofileid` bigint DEFAULT NULL,
  `permanent_disconnect_prfoile_id` bigint DEFAULT NULL,
  `start_stop_attribute_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `input_packet_attribute_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `output_packet_attribute_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `packet_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `standard_attribute_checked` bit(1) DEFAULT b'0',
  `authentication_profile` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `customer_mac_attribute` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'Calling-Station-Id',
  `vlan_check_required` bit(1) DEFAULT b'0',
  `checkconcurrency` bit(1) DEFAULT b'1',
  `logoutoldsessiononnew` bit(1) DEFAULT b'0',
  `dynamic_acct_session_attribute` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'Acct-Session-Id',
  `triggercoadmonmacremove` bit(1) DEFAULT b'1',
  PRIMARY KEY (`clientgroupid`),
  UNIQUE KEY `clientgroup_name_mvno_unq` (`mvnoid`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
