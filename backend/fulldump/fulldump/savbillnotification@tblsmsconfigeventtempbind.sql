-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillnotification    Table: tblsmsconfigeventtempbind
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblsmsconfigeventtempbind`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblsmsconfigeventtempbind` (
  `smsconfigeventtempbindid` bigint NOT NULL AUTO_INCREMENT,
  `eventid` bigint DEFAULT NULL,
  `smsconfigid` bigint DEFAULT NULL,
  PRIMARY KEY (`smsconfigeventtempbindid`),
  KEY `eventid_id_fk3` (`eventid`),
  KEY `smsconfigid_id_fk3` (`smsconfigid`),
  CONSTRAINT `eventid_id_fk3` FOREIGN KEY (`eventid`) REFERENCES `tblmevent` (`eventid`),
  CONSTRAINT `smsconfigid_id_fk3` FOREIGN KEY (`smsconfigid`) REFERENCES `tblmsmsconfig` (`smsconfigid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
