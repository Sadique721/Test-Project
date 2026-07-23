-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: commongateway    Table: tbltstaffaccessiblerole
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltstaffaccessiblerole`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltstaffaccessiblerole` (
  `accessibleroleid` bigint NOT NULL AUTO_INCREMENT,
  `staffid` bigint NOT NULL,
  `staffaccessibleroleid` bigint NOT NULL,
  PRIMARY KEY (`accessibleroleid`),
  UNIQUE KEY `tbltstaffaccessiblerole_accessibleroleid_unq` (`accessibleroleid`),
  KEY `tbltstaffaccessiblerole_staffid_fk` (`staffid`),
  CONSTRAINT `tbltstaffaccessiblerole_staffid_fk` FOREIGN KEY (`staffid`) REFERENCES `tblmstaffuser` (`staffid`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
