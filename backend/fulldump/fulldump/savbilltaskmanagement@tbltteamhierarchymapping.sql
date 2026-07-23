-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbilltaskmanagement    Table: tbltteamhierarchymapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltteamhierarchymapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltteamhierarchymapping` (
  `id` bigint NOT NULL,
  `team_id` bigint DEFAULT NULL,
  `hierarchy_id` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `next_team_id` bigint DEFAULT NULL,
  `order_number` bigint DEFAULT NULL,
  `team_action` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `team_condition` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tat_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hierarchymapping_id_unq` (`id`),
  KEY `tbltteamhierarchymapping_hierarcy_id_fk` (`hierarchy_id`),
  CONSTRAINT `tbltteamhierarchymapping_hierarcy_id_fk` FOREIGN KEY (`hierarchy_id`) REFERENCES `tblmhierarchy` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
