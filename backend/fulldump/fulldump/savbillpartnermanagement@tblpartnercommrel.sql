-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillpartnermanagement    Table: tblpartnercommrel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblpartnercommrel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblpartnercommrel` (
  `PARNTERCOMMRELID` bigint NOT NULL,
  `CUSTOMERID` bigint DEFAULT NULL,
  `PARTNERID` bigint DEFAULT NULL,
  `COMM_TYPE` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `comm_rel_value` decimal(20,4) DEFAULT NULL,
  `COMM_VALUE` decimal(20,4) DEFAULT NULL,
  `CREATEDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `BILLDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `PROCESS_STATUS` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`PARNTERCOMMRELID`),
  UNIQUE KEY `partnercommrel_id_unq` (`PARNTERCOMMRELID`),
  KEY `tblpartnercommrel_ibfk_1` (`PARTNERID`),
  KEY `tblpartnercommrel_ibfk_2` (`CUSTOMERID`),
  CONSTRAINT `tblpartnercommrel_ibfk_1` FOREIGN KEY (`PARTNERID`) REFERENCES `tblpartners` (`PARTNERID`),
  CONSTRAINT `tblpartnercommrel_ibfk_2` FOREIGN KEY (`CUSTOMERID`) REFERENCES `tblcustomers` (`custid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
