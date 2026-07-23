-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltreplacementitemhistory
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltreplacementitemhistory`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltreplacementitemhistory` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `customerid` bigint DEFAULT NULL,
  `oldcustomerinventoryid` bigint DEFAULT NULL,
  `newcustomerinventoryid` bigint DEFAULT NULL,
  `olditemid` bigint DEFAULT NULL,
  `newitemid` bigint DEFAULT NULL,
  `oldmac` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `newmac` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `olderialnumber` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `newerialnumber` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `raisedrequeststaffid` bigint DEFAULT NULL,
  `status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDATE` timestamp NULL DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` bigint DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltreplacementitemhistory_field_unq` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
