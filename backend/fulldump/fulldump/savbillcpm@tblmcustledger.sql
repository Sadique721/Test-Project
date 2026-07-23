-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmcustledger
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmcustledger`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmcustledger` (
  `CUSTLEDGERID` bigint NOT NULL AUTO_INCREMENT,
  `TOTALDUE` decimal(20,4) DEFAULT NULL,
  `TOTALPAID` decimal(20,4) DEFAULT NULL,
  `CUSTID` bigint DEFAULT NULL,
  `CREATEDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` decimal(20,0) NOT NULL DEFAULT '1',
  `LASTMODIFIEDBYSTAFFID` decimal(20,0) NOT NULL DEFAULT '1',
  PRIMARY KEY (`CUSTLEDGERID`),
  UNIQUE KEY `mcustledger_custledgerId_unq` (`CUSTLEDGERID`),
  UNIQUE KEY `idx_ledger_cust_uni` (`CUSTID`),
  CONSTRAINT `tblmcustledger_ibfk_1` FOREIGN KEY (`CUSTID`) REFERENCES `tblcustomers` (`custid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
