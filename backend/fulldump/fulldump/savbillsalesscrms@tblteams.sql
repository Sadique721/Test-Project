-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tblteams
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblteams`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblteams` (
  `team_id` bigint NOT NULL,
  `team_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `team_status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `partnerid` bigint DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  `parentteamid` bigint DEFAULT NULL,
  `lcoid` bigint DEFAULT NULL,
  PRIMARY KEY (`team_id`),
  UNIQUE KEY `teams_team_id_unq` (`team_id`),
  KEY `tblteams_ibfk_1` (`MVNOID`),
  KEY `parentteamid_id_fk` (`parentteamid`),
  CONSTRAINT `parentteamid_id_fk` FOREIGN KEY (`parentteamid`) REFERENCES `tblteams` (`team_id`),
  CONSTRAINT `tblteams_ibfk_1` FOREIGN KEY (`MVNOID`) REFERENCES `tblmmvno` (`MVNOID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
