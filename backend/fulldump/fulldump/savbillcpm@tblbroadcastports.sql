-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblbroadcastports
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblbroadcastports`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblbroadcastports` (
  `broadportid` bigint NOT NULL AUTO_INCREMENT,
  `is_deleted` bit(1) DEFAULT NULL,
  `portid` bigint DEFAULT NULL,
  `broadcast_id` bigint DEFAULT NULL,
  PRIMARY KEY (`broadportid`),
  UNIQUE KEY `broadcastports_broadportid_unq` (`broadportid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
