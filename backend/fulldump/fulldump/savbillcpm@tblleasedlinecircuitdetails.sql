-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblleasedlinecircuitdetails
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblleasedlinecircuitdetails`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblleasedlinecircuitdetails` (
  `llcdetailsid` bigint NOT NULL AUTO_INCREMENT,
  `llcidentifier` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `llclabel` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `llctype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `llcstaticip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `llcdevicetype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_delete` tinyint(1) NOT NULL DEFAULT '0',
  `llcustid` bigint DEFAULT NULL,
  `CREATEDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CREATEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `package_id` bigint DEFAULT NULL,
  PRIMARY KEY (`llcdetailsid`),
  UNIQUE KEY `TBLLEASEDLINECIRCUITDETAILS_llcdetailsid_unq` (`llcdetailsid`),
  KEY `TBLLEASEDLINECIRCUITDETAILS_ibfk_1` (`llcustid`),
  KEY `TBLLEASEDLINECIRCUITDETAILS_ibfk_2` (`package_id`),
  CONSTRAINT `TBLLEASEDLINECIRCUITDETAILS_ibfk_1` FOREIGN KEY (`llcustid`) REFERENCES `tblleasedlinecustomers` (`llcustid`),
  CONSTRAINT `TBLLEASEDLINECIRCUITDETAILS_ibfk_2` FOREIGN KEY (`package_id`) REFERENCES `tblmpostpaidplan` (`POSTPAIDPLANID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
