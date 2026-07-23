-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillnotification    Table: tblmsmsconfigmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmsmsconfigmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmsmsconfigmapping` (
  `smsconfigmappingid` bigint NOT NULL AUTO_INCREMENT,
  `smsconfigid` bigint DEFAULT NULL,
  `parameter` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `value` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mvnoid` bigint DEFAULT NULL,
  `createdon` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifiedon` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`smsconfigmappingid`),
  KEY `smsconfigid_id_fk` (`smsconfigid`),
  CONSTRAINT `smsconfigid_id_fk` FOREIGN KEY (`smsconfigid`) REFERENCES `tblmsmsconfig` (`smsconfigid`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
