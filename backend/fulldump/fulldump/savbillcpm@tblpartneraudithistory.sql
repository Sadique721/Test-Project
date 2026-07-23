-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblpartneraudithistory
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblpartneraudithistory`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblpartneraudithistory` (
  `partnerauditid` bigint NOT NULL AUTO_INCREMENT,
  `partnerid` bigint DEFAULT NULL,
  `partnername` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `new_customer_count` bigint DEFAULT NULL,
  `renew_customer_count` bigint DEFAULT NULL,
  `total_customer_count` bigint DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT NULL,
  `lastauditdate` timestamp NULL DEFAULT NULL,
  `BUID` bigint DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`partnerauditid`),
  UNIQUE KEY `tblpartneraudithistory_id_unq` (`partnerauditid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
