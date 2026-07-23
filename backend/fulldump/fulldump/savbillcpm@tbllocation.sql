-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbllocation
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbllocation`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbllocation` (
  `locationid` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodified_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_delete` tinyint(1) NOT NULL DEFAULT '0',
  `MVNOID` bigint DEFAULT NULL,
  PRIMARY KEY (`locationid`),
  UNIQUE KEY `location_locationid_unq` (`locationid`),
  KEY `tbllocation_ibfk_1` (`MVNOID`),
  CONSTRAINT `tbllocation_ibfk_1` FOREIGN KEY (`MVNOID`) REFERENCES `tblmmvno` (`MVNOID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
