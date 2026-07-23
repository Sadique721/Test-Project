-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillpartnermanagement    Table: tblmclientservice
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmclientservice`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmclientservice` (
  `serviceid` bigint NOT NULL,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  PRIMARY KEY (`serviceid`),
  UNIQUE KEY `tblmclientservice_serviceid_unq` (`serviceid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
