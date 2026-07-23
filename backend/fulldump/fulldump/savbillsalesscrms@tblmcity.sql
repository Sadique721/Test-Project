-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tblmcity
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmcity`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmcity` (
  `CITYID` bigint NOT NULL,
  `NAME` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `STATEID` bigint DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  `countryid` bigint NOT NULL,
  `is_delete` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`CITYID`),
  UNIQUE KEY `mcity_cityId_unq` (`CITYID`),
  KEY `city_id_fk` (`STATEID`),
  CONSTRAINT `city_id_fk` FOREIGN KEY (`STATEID`) REFERENCES `tblmstate` (`STATEID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
