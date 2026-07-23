-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: commongateway    Table: tbltteamusermapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltteamusermapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltteamusermapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `team_id` bigint NOT NULL,
  `staffid` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltteamusermapping_id_unq` (`id`),
  KEY `tblteamusermapping_ibfk_1` (`team_id`),
  KEY `tblteamusermapping_ibfk_2` (`staffid`),
  CONSTRAINT `tblteamusermapping_ibfk_1` FOREIGN KEY (`team_id`) REFERENCES `tblmteams` (`team_id`),
  CONSTRAINT `tblteamusermapping_ibfk_2` FOREIGN KEY (`staffid`) REFERENCES `tblmstaffuser` (`staffid`)
) ENGINE=InnoDB AUTO_INCREMENT=208 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
