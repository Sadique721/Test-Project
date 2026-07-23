-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmservices
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmservices`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmservices` (
  `serviceid` bigint NOT NULL AUTO_INCREMENT,
  `servicename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CREATEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  `icname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `iccode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `BUID` bigint DEFAULT NULL,
  `is_qosv` bit(1) DEFAULT b'1',
  `expiry` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ledger_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_dtv` bit(1) DEFAULT NULL,
  `investmentcode_id` bigint DEFAULT NULL,
  `feasibility` tinyint DEFAULT NULL,
  `poc` tinyint DEFAULT NULL,
  `installation` tinyint DEFAULT NULL,
  `provisioning` tinyint DEFAULT NULL,
  `is_price_editable` tinyint DEFAULT NULL,
  `feasibility_team_id` bigint DEFAULT NULL,
  `poc_team_id` bigint DEFAULT NULL,
  `installation_team_id` bigint DEFAULT NULL,
  `provisioning_team_id` bigint DEFAULT NULL,
  `displayname` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_service_through_lead` bit(1) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `mvno_name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`serviceid`),
  UNIQUE KEY `mservices_serviceid_unq` (`serviceid`),
  KEY `TBLMSERVICES_businessunitid_FK_1` (`BUID`),
  KEY `tblmservices_ibfk_1` (`MVNOID`),
  CONSTRAINT `tblmservices_ibfk_1` FOREIGN KEY (`MVNOID`) REFERENCES `tblmmvno` (`MVNOID`)
) ENGINE=InnoDB AUTO_INCREMENT=278 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
