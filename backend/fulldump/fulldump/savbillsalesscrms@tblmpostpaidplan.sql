-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tblmpostpaidplan
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmpostpaidplan`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmpostpaidplan` (
  `POSTPAIDPLANID` bigint NOT NULL AUTO_INCREMENT,
  `NAME` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `DISPLAYNAME` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `PLANCATEGORY` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `STARTDATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ENDDATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `QUOTA` decimal(14,4) DEFAULT NULL,
  `STATUS` char(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE',
  `MVNOID` bigint DEFAULT NULL,
  `plantype` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `plangroup` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `validity` decimal(4,0) DEFAULT NULL,
  `quotatype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `quotaunittime` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `quotatime` decimal(20,4) DEFAULT NULL,
  `offerprice` decimal(20,4) DEFAULT NULL,
  `is_delete` tinyint(1) NOT NULL DEFAULT '0',
  `mode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `unitsOfValidity` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `BUID` bigint NOT NULL,
  `productId` bigint DEFAULT NULL,
  `bandwidth` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `apig_plan_id` bigint DEFAULT NULL,
  `DESCRIPTION` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `service_area_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `quotarestinterval` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `quotaunit` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `timebasepolicyid` bigint DEFAULT NULL,
  `qospolicyid` bigint DEFAULT NULL,
  PRIMARY KEY (`POSTPAIDPLANID`),
  UNIQUE KEY `mpostpaidplan_id_unq` (`POSTPAIDPLANID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
