-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillrevenuemanagement    Table: tblmbillrun
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmbillrun`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmbillrun` (
  `billrunid` bigint NOT NULL AUTO_INCREMENT,
  `billruncreatedate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `billrundate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `billruncount` decimal(10,0) DEFAULT NULL,
  `amount` decimal(20,4) DEFAULT NULL,
  `status` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `billruncompletedate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `SUCCESSCOUNT` decimal(20,0) DEFAULT NULL,
  `failcount` decimal(20,0) DEFAULT NULL,
  `is_delete` tinyint(1) NOT NULL DEFAULT '0',
  `MVNOID` bigint DEFAULT NULL,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `BUID` bigint DEFAULT NULL,
  `lcoid` bigint DEFAULT NULL,
  PRIMARY KEY (`billrunid`),
  UNIQUE KEY `mbillrun_billrunid_unq` (`billrunid`)
) ENGINE=InnoDB AUTO_INCREMENT=146 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
