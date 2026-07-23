-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillrevenuemanagement    Table: tblmslabtax
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmslabtax`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmslabtax` (
  `SLABTAXID` bigint NOT NULL AUTO_INCREMENT,
  `NAME` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `RANGEFROM` decimal(16,4) DEFAULT NULL,
  `RANGEUPTO` decimal(16,4) DEFAULT NULL,
  `RATE` decimal(10,2) DEFAULT NULL,
  `TAXID` bigint DEFAULT NULL,
  `BUID` bigint DEFAULT NULL,
  `before_discount` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`SLABTAXID`),
  UNIQUE KEY `mslabtax_slabtaxId_unq` (`SLABTAXID`),
  KEY `tblmslabtax_ibfk_1` (`TAXID`),
  CONSTRAINT `tblmslabtax_ibfk_1` FOREIGN KEY (`TAXID`) REFERENCES `tblmtax` (`TAXID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
