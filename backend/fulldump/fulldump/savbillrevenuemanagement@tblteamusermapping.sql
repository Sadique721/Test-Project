-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillrevenuemanagement    Table: tblteamusermapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblteamusermapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblteamusermapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `team_id` bigint DEFAULT NULL,
  `staffid` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `teams_team_id_unq` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=205 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
