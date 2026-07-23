-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltpartnercreditdocdtls
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltpartnercreditdocdtls`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltpartnercreditdocdtls` (
  `creditdocdetailid` bigint NOT NULL AUTO_INCREMENT,
  `creditdocumentid` bigint NOT NULL,
  `CUSTOMERID` bigint DEFAULT NULL,
  `PARTNERID` bigint DEFAULT NULL,
  `COMM_TYPE` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `COMM_REL_VALUE` decimal(20,4) DEFAULT NULL,
  `COMM_VALUE` decimal(20,4) DEFAULT NULL,
  `tax` decimal(20,4) DEFAULT '0.0000',
  `totalamount` decimal(20,4) DEFAULT '0.0000',
  `startdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `enddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `prorationtype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `noofcycle` decimal(20,4) DEFAULT '0.0000',
  PRIMARY KEY (`creditdocdetailid`),
  UNIQUE KEY `tpartnercreditdocdtls_creditdocdetailid_unq` (`creditdocdetailid`),
  KEY `tbltpartnercreditdocdtls_ibfk_1` (`CUSTOMERID`),
  KEY `tbltpartnercreditdocdtls_ibfk_2` (`PARTNERID`),
  KEY `tbltpartnercreditdocdtls_ibfk_3` (`creditdocumentid`),
  CONSTRAINT `tbltpartnercreditdocdtls_ibfk_1` FOREIGN KEY (`CUSTOMERID`) REFERENCES `tblcustomers` (`custid`),
  CONSTRAINT `tbltpartnercreditdocdtls_ibfk_2` FOREIGN KEY (`PARTNERID`) REFERENCES `tblpartners` (`PARTNERID`),
  CONSTRAINT `tbltpartnercreditdocdtls_ibfk_3` FOREIGN KEY (`creditdocumentid`) REFERENCES `tblpartnerdebitdocument` (`creditdocumentid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
