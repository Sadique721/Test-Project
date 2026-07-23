-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillnotification    Table: tblsmsreceivereventtempbind
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblsmsreceivereventtempbind`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblsmsreceivereventtempbind` (
  `smsreceivereventtempbindingid` bigint NOT NULL AUTO_INCREMENT,
  `eventid` bigint DEFAULT NULL,
  `staffid` bigint DEFAULT NULL,
  `stafffullname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `staffusername` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mobilenumber` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`smsreceivereventtempbindingid`),
  KEY `eventid_id_fk2` (`eventid`),
  CONSTRAINT `eventid_id_fk2` FOREIGN KEY (`eventid`) REFERENCES `tblmevent` (`eventid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
