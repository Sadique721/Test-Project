-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillinventorymanagement    Table: tblmslabtax
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmslabtax`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmslabtax` (
  `slabtaxid` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `rangefrom` decimal(16,4) DEFAULT NULL,
  `rangeupto` decimal(16,4) DEFAULT NULL,
  `rate` decimal(10,2) DEFAULT NULL,
  `taxid` bigint DEFAULT NULL,
  `before_discount` bit(1) DEFAULT b'0',
  PRIMARY KEY (`slabtaxid`),
  UNIQUE KEY `tblmslabtax_slabtaxid_unq` (`slabtaxid`),
  KEY `tblmslabtax_taxidfk_1` (`taxid`),
  CONSTRAINT `tblmslabtax_taxidfk_1` FOREIGN KEY (`taxid`) REFERENCES `tblmtax` (`taxid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
