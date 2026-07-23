-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tblcustquotaresetdtls
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblcustquotaresetdtls`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblcustquotaresetdtls` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cprid` bigint DEFAULT NULL,
  `custid` bigint DEFAULT NULL,
  `totalquotaused` double DEFAULT '0',
  `quotaunit` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `totaltimequota` double DEFAULT '0',
  `createdate` timestamp NOT NULL,
  `lastmodifieddate` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
