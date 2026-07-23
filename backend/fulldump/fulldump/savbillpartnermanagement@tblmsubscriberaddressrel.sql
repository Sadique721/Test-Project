-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillpartnermanagement    Table: tblmsubscriberaddressrel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmsubscriberaddressrel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmsubscriberaddressrel` (
  `ADDRESSID` bigint NOT NULL,
  `SUBSCRIBERID` bigint DEFAULT NULL COMMENT 'tblmsubscriberaddressrel definition',
  `ADDRESSTYPE` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'tblmsubscriberaddressrel definition',
  `ADDRESS1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'tblmsubscriberaddressrel definition',
  `ADDRESS2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'tblmsubscriberaddressrel definition',
  `CITYID` bigint DEFAULT NULL COMMENT 'tblmsubscriberaddressrel definition',
  `STATEID` bigint DEFAULT NULL COMMENT 'tblmsubscriberaddressrel definition',
  `COUNTRYID` bigint DEFAULT NULL COMMENT 'tblmsubscriberaddressrel definition',
  `is_delete` tinyint DEFAULT '0' COMMENT 'tblmsubscriberaddressrel definition',
  `landmark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'tblmsubscriberaddressrel definition',
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'tblmsubscriberaddressrel definition',
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'tblmsubscriberaddressrel definition',
  `CREATEDBYSTAFFID` decimal(20,0) DEFAULT '1' COMMENT 'tblmsubscriberaddressrel definition',
  `LASTMODIFIEDBYSTAFFID` decimal(20,0) DEFAULT '1' COMMENT 'tblmsubscriberaddressrel definition',
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'tblmsubscriberaddressrel definition',
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'tblmsubscriberaddressrel definition',
  `PINCODEID` bigint DEFAULT NULL COMMENT 'tblmsubscriberaddressrel definition',
  `AREAID` bigint DEFAULT NULL COMMENT 'tblmsubscriberaddressrel definition',
  `next_team_hir_mapping` bigint DEFAULT NULL COMMENT 'tblmsubscriberaddressrel definition',
  `next_staff` bigint DEFAULT NULL COMMENT 'tblmsubscriberaddressrel definition',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'tblmsubscriberaddressrel definition',
  `version` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'tblmsubscriberaddressrel definition',
  `landmark1` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'tblmsubscriberaddressrel definition',
  `shifted_partner_id` bigint DEFAULT NULL COMMENT 'tblmsubscriberaddressrel definition',
  `shifted_service_area_id` bigint DEFAULT NULL COMMENT 'tblmsubscriberaddressrel definition',
  `shift_id` bigint DEFAULT NULL COMMENT 'tblmsubscriberaddressrel definition',
  PRIMARY KEY (`ADDRESSID`),
  UNIQUE KEY `msubscriberaddressrel_Id_unq` (`ADDRESSID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
