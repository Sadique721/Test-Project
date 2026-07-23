-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbldiscountaudit
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbldiscountaudit`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbldiscountaudit` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `staffid` int DEFAULT NULL,
  `custpackgeid` bigint DEFAULT NULL,
  `olddiscount` double DEFAULT NULL,
  `newdiscount` double DEFAULT NULL,
  `updateddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `staffname` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `remarks` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `olddiscounttype` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `newdiscounttype` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `olddiscountexpirydate` date DEFAULT NULL,
  `newdiscountexpirydate` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbldiscountaudit_unq` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
