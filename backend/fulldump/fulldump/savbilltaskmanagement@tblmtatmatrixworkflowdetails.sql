-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbilltaskmanagement    Table: tblmtatmatrixworkflowdetails
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmtatmatrixworkflowdetails`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmtatmatrixworkflowdetails` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `mtime` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `munit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `action` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `order_no` bigint DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT NULL,
  `level` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `work_flow_id` bigint DEFAULT NULL,
  `tat_matrix_id` bigint DEFAULT NULL,
  `parent_id` bigint DEFAULT NULL,
  `staff_id` bigint DEFAULT NULL,
  `current_team_heirarchy_mapping_id` bigint DEFAULT NULL,
  `entity_id` bigint DEFAULT NULL,
  `event_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `event_id` bigint DEFAULT NULL,
  `start_date_time` timestamp NULL DEFAULT NULL,
  `notification_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `team_id` bigint DEFAULT NULL,
  `is_overdue_reminder` bit(1) DEFAULT b'0',
  `ticket_hold_time_init` timestamp NULL DEFAULT NULL,
  `ticket_hold_time_end` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblmtatmatrixworkflowdetails_id_unq` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
