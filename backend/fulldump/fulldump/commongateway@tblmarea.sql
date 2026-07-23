-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: commongateway    Table: tblmarea
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmarea`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmarea` (
  `areaid` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `countryid` bigint DEFAULT NULL,
  `stateid` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `createdbystaffid` bigint DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifiedbystaffid` bigint DEFAULT NULL,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cityid` bigint DEFAULT NULL,
  `status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pincodeid` bigint DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  PRIMARY KEY (`areaid`),
  UNIQUE KEY `tblmarea_areaid_unq` (`areaid`),
  KEY `tblmarea_FK` (`countryid`),
  KEY `tblmarea_FK_2` (`stateid`),
  KEY `tblmarea_FK_1` (`cityid`),
  KEY `tblmarea_FK_3` (`pincodeid`),
  KEY `tblmarea_ibfk_1` (`MVNOID`),
  KEY `idx_area_pincodeid_deleted` (`pincodeid`,`is_deleted`),
  CONSTRAINT `area_mvnoid_fk` FOREIGN KEY (`MVNOID`) REFERENCES `tblmmvno` (`MVNOID`),
  CONSTRAINT `tblmarea_FK` FOREIGN KEY (`countryid`) REFERENCES `tblmcountry` (`countryid`),
  CONSTRAINT `tblmarea_FK_1` FOREIGN KEY (`cityid`) REFERENCES `tblmcity` (`cityid`),
  CONSTRAINT `tblmarea_FK_2` FOREIGN KEY (`stateid`) REFERENCES `tblmstate` (`stateid`),
  CONSTRAINT `tblmarea_FK_3` FOREIGN KEY (`pincodeid`) REFERENCES `tblmpincode` (`pincodeid`),
  CONSTRAINT `tblmarea_ibfk_1` FOREIGN KEY (`MVNOID`) REFERENCES `tblmmvno` (`MVNOID`)
) ENGINE=InnoDB AUTO_INCREMENT=4896 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
