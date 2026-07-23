-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblclientservice
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblclientservice`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblclientservice` (
  `SERVICEID` bigint NOT NULL,
  `NAME` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `VALUE` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  PRIMARY KEY (`SERVICEID`),
  UNIQUE KEY `clientservice_serviceId_unq` (`SERVICEID`),
  KEY `tblclientservice_ibfk_1` (`MVNOID`),
  KEY `index_tblclientservice_name_mvno` (`NAME`,`MVNOID`),
  KEY `tblclientservice_name_mvno` (`NAME`,`MVNOID`),
  CONSTRAINT `tblclientservice_ibfk_1` FOREIGN KEY (`MVNOID`) REFERENCES `tblmmvno` (`MVNOID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
