-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillpartnermanagement    Table: tblmservices
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmservices`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmservices` (
  `serviceid` bigint NOT NULL,
  `servicename` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createdbystaffid` bigint DEFAULT NULL,
  `lastmodifiedbystaffid` bigint DEFAULT NULL,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  `BUID` bigint DEFAULT NULL,
  `is_dtv` bit(1) DEFAULT NULL,
  `expiry` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `feasibility` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ledger_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `poc` tinyint DEFAULT NULL,
  `installation` tinyint DEFAULT NULL,
  `provisioning` tinyint DEFAULT NULL,
  `is_price_editable` tinyint DEFAULT NULL,
  `feasibility_team_id` bigint DEFAULT NULL,
  `poc_team_id` bigint DEFAULT NULL,
  `installation_team_id` bigint DEFAULT NULL,
  `provisioning_team_id` bigint DEFAULT NULL,
  `icname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `iccode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `businessunitid` bigint DEFAULT NULL,
  `investmentcode_id` bigint DEFAULT NULL,
  `is_qosv` tinyint DEFAULT NULL,
  `serviceparamname` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `isdeleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`serviceid`),
  UNIQUE KEY `tblmservices_serviceid_unq` (`serviceid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
