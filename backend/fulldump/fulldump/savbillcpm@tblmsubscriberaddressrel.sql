-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmsubscriberaddressrel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmsubscriberaddressrel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmsubscriberaddressrel` (
  `ADDRESSID` bigint NOT NULL AUTO_INCREMENT,
  `SUBSCRIBERID` bigint NOT NULL,
  `ADDRESSTYPE` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `ADDRESS1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ADDRESS2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CITYID` bigint DEFAULT NULL,
  `STATEID` bigint DEFAULT NULL,
  `COUNTRYID` bigint DEFAULT NULL,
  `is_delete` tinyint(1) NOT NULL DEFAULT '0',
  `landmark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` decimal(20,0) NOT NULL DEFAULT '1',
  `LASTMODIFIEDBYSTAFFID` decimal(20,0) NOT NULL DEFAULT '1',
  `createdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `PINCODEID` bigint DEFAULT NULL,
  `AREAID` bigint DEFAULT NULL,
  `next_team_hir_mapping` bigint DEFAULT NULL,
  `next_staff` bigint DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `version` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `landmark1` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `shifted_partner_id` bigint DEFAULT NULL,
  `shifted_service_area_id` bigint DEFAULT NULL,
  `shift_id` bigint DEFAULT NULL,
  `subareaid` bigint DEFAULT NULL,
  `building_mgmt_id` bigint DEFAULT NULL,
  `building_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ADDRESSID`,`SUBSCRIBERID`),
  UNIQUE KEY `msubscriberaddressrel_Id_unq` (`ADDRESSID`),
  KEY `tblmsubscriberaddressrel_ibfk_1` (`SUBSCRIBERID`),
  KEY `tblmsubscriberaddressrel_ibfk_4` (`COUNTRYID`),
  KEY `tblmsubscriberaddressrel_ibfk_3` (`STATEID`),
  KEY `tblmsubscriberaddressrel_ibfk_2` (`CITYID`),
  KEY `tblmsubscriberaddressrel_FK` (`PINCODEID`),
  KEY `tblmsubscriberaddressrel_FK_1` (`AREAID`),
  KEY `index_TBLMSUBSCRIBERADDRESSREL_addrType_custId_version` (`ADDRESSTYPE`,`SUBSCRIBERID`,`version`),
  CONSTRAINT `tblmsubscriberaddressrel_FK` FOREIGN KEY (`PINCODEID`) REFERENCES `tblmpincode` (`pincodeid`),
  CONSTRAINT `tblmsubscriberaddressrel_FK_1` FOREIGN KEY (`AREAID`) REFERENCES `tblmarea` (`areaid`),
  CONSTRAINT `tblmsubscriberaddressrel_ibfk_1` FOREIGN KEY (`SUBSCRIBERID`) REFERENCES `tblcustomers` (`custid`),
  CONSTRAINT `tblmsubscriberaddressrel_ibfk_2` FOREIGN KEY (`CITYID`) REFERENCES `tblmcity` (`cityid`),
  CONSTRAINT `tblmsubscriberaddressrel_ibfk_3` FOREIGN KEY (`STATEID`) REFERENCES `tblmstate` (`stateid`),
  CONSTRAINT `tblmsubscriberaddressrel_ibfk_4` FOREIGN KEY (`COUNTRYID`) REFERENCES `tblmcountry` (`countryid`)
) ENGINE=InnoDB AUTO_INCREMENT=190369 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
