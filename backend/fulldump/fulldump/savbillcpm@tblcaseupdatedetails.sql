-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblcaseupdatedetails
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblcaseupdatedetails`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblcaseupdatedetails` (
  `updatedtlsid` bigint NOT NULL AUTO_INCREMENT,
  `updateid` bigint DEFAULT NULL,
  `operation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `entitytype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `oldvalue` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `newvalue` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `remarktype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `resolutionid` bigint DEFAULT NULL,
  `attachment` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `filename` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`updatedtlsid`),
  UNIQUE KEY `caseupdatedetails_updatedtlsid_unq` (`updatedtlsid`),
  KEY `tblcaseupdatedetails_updateid_fk` (`updateid`),
  KEY `tblcaseupdatedetails_resolutionid_fk` (`resolutionid`),
  CONSTRAINT `tblcaseupdatedetails_resolutionid_fk` FOREIGN KEY (`resolutionid`) REFERENCES `tblcaseresolutions` (`res_id`),
  CONSTRAINT `tblcaseupdatedetails_updateid_fk` FOREIGN KEY (`updateid`) REFERENCES `tblcaseupdates` (`updateid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
