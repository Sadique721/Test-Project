-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: diameter-sit-db    Table: tblmpostpaidplan
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmpostpaidplan`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmpostpaidplan` (
  `POSTPAIDPLANID` bigint NOT NULL,
  `NAME` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DISPLAYNAME` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `PLANCODE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DESCRIPTION` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `PLANCATEGORY` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MAXALLOWEDCHILD` decimal(8,0) DEFAULT NULL,
  `STARTDATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ENDDATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `QUOTA` decimal(14,4) DEFAULT NULL,
  `QUOTAUNIT` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `UPLOADQOS` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DOWNLOADQOS` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `STATUS` char(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE',
  `PLANSTATUS` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'INACTIVE',
  `CHILDQUOTA` decimal(14,4) DEFAULT NULL,
  `CHILDQUOTAUNIT` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SLICE` decimal(14,4) DEFAULT NULL,
  `SLICEUNIT` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `PARAM1` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `PARAM2` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `PARAM3` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ATTACHEDTOALLHOTSPOT` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'false',
  `CREATEDATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `CREATEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `MVNOID` bigint DEFAULT NULL,
  `TAXID` bigint DEFAULT NULL,
  `serviceid` bigint DEFAULT NULL,
  `plantype` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dbr` decimal(20,4) DEFAULT '0.0000',
  `plangroup` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `validity` decimal(4,0) DEFAULT NULL,
  `UPLOADTS` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DOWNLOADTS` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `allowoverusage` tinyint(1) DEFAULT '0',
  `saccode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `quotatype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `quotaunittime` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `quotatime` decimal(20,4) DEFAULT NULL,
  `maxconcurrentsession` decimal(10,0) DEFAULT NULL,
  `qospolicy_id` bigint DEFAULT NULL,
  `radiusprofile_id` bigint DEFAULT NULL,
  `offerprice` decimal(14,4) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `is_delete` tinyint(1) DEFAULT '0',
  `quotadid` decimal(50,0) DEFAULT NULL,
  `quotaintercom` decimal(50,0) DEFAULT NULL,
  `quotaunitdid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `quotaunitintercom` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `taxamount` decimal(16,4) DEFAULT NULL,
  `datacategory` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `quotarestinterval` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `unitsofvalidity` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'Days',
  `timebasepolicyid` bigint DEFAULT NULL,
  `chunk` double DEFAULT NULL,
  `use_quota` bit(1) DEFAULT b'0',
  `usage_quota_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'TOTAL',
  `addon_to_base` bit(1) DEFAULT b'0',
  PRIMARY KEY (`POSTPAIDPLANID`),
  UNIQUE KEY `mpostpaidplan_id_unq` (`POSTPAIDPLANID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
