-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: commongateway    Table: tblmpincode
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmpincode`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmpincode` (
  `pincodeid` bigint NOT NULL AUTO_INCREMENT,
  `pincode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `countryid` bigint DEFAULT NULL,
  `stateid` bigint DEFAULT NULL,
  `cityid` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `createdbystaffid` bigint DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifiedbystaffid` bigint DEFAULT NULL,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  PRIMARY KEY (`pincodeid`),
  UNIQUE KEY `tblmpincode_id_unq` (`pincodeid`),
  KEY `tblmpincode_FK` (`countryid`),
  KEY `tblmpincode_FK_2` (`stateid`),
  KEY `tblmpincode_FK_1` (`cityid`),
  KEY `tblmpincode_ibfk_1` (`MVNOID`),
  KEY `idx_pincode_cityid_deleted` (`cityid`,`is_deleted`),
  CONSTRAINT `tblmpincode_FK` FOREIGN KEY (`countryid`) REFERENCES `tblmcountry` (`countryid`),
  CONSTRAINT `tblmpincode_FK_1` FOREIGN KEY (`cityid`) REFERENCES `tblmcity` (`cityid`),
  CONSTRAINT `tblmpincode_FK_2` FOREIGN KEY (`stateid`) REFERENCES `tblmstate` (`stateid`),
  CONSTRAINT `tblmpincode_ibfk_1` FOREIGN KEY (`MVNOID`) REFERENCES `tblmmvno` (`MVNOID`)
) ENGINE=InnoDB AUTO_INCREMENT=10176 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
