-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillinventorymanagement    Table: tbltwarehousemanagmentteamsmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltwarehousemanagmentteamsmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltwarehousemanagmentteamsmapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `team_id` bigint DEFAULT NULL,
  `warehouse_id` bigint DEFAULT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodified_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltwarehousemanagmentteamsmapping_unique` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
