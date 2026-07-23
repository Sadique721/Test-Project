-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillticketmanagement    Table: tblcasereasonconfig
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblcasereasonconfig`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblcasereasonconfig` (
  `config_id` bigint NOT NULL AUTO_INCREMENT,
  `staffid` bigint NOT NULL,
  `reasonid` bigint NOT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `serviceareaid` bigint DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  PRIMARY KEY (`config_id`),
  UNIQUE KEY `casereasonconfig_config_id_unq` (`config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
