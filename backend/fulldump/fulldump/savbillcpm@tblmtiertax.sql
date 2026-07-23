-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmtiertax
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmtiertax`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmtiertax` (
  `TIERTAXID` bigint NOT NULL AUTO_INCREMENT,
  `NAME` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TAXGROUP` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `RATE` decimal(10,2) DEFAULT NULL,
  `TAXID` bigint DEFAULT NULL,
  `is_delete` tinyint(1) NOT NULL DEFAULT '0',
  `before_discount` bit(1) DEFAULT b'0',
  `ledger_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`TIERTAXID`),
  UNIQUE KEY `mtiertax_tiertaxId_unq` (`TIERTAXID`),
  KEY `tblmtiertax_ibfk_1` (`TAXID`),
  CONSTRAINT `tblmtiertax_ibfk_1` FOREIGN KEY (`TAXID`) REFERENCES `tblmtax` (`TAXID`)
) ENGINE=InnoDB AUTO_INCREMENT=140 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
