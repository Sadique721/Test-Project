-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblpartnerlocationrel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblpartnerlocationrel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblpartnerlocationrel` (
  `partnerlocid` bigint NOT NULL AUTO_INCREMENT,
  `locationid` bigint NOT NULL,
  `partnerid` bigint NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodified_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`partnerlocid`),
  UNIQUE KEY `partnerlocationrel_partnerlocid_unq` (`partnerlocid`),
  KEY `tblpartnerlocationrel_ibfk_1` (`locationid`),
  KEY `tblpartnerlocationrel_ibfk_2` (`partnerid`),
  CONSTRAINT `tblpartnerlocationrel_ibfk_1` FOREIGN KEY (`locationid`) REFERENCES `tbllocation` (`locationid`),
  CONSTRAINT `tblpartnerlocationrel_ibfk_2` FOREIGN KEY (`partnerid`) REFERENCES `tblpartners` (`PARTNERID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
