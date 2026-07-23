-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tblttlsprofilemapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblttlsprofilemapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblttlsprofilemapping` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `PROFILEID` bigint DEFAULT NULL,
  `PASSWORD` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `FILE_PATH` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `FILE_TYPE` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `TLSPROFILEMAPPING_fk1` (`PROFILEID`),
  CONSTRAINT `TLSPROFILEMAPPING_fk1` FOREIGN KEY (`PROFILEID`) REFERENCES `tblmradiusprofile` (`radiusprofileid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
