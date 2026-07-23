-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblworkflowassignstaffmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblworkflowassignstaffmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblworkflowassignstaffmapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `event_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `entity_id` bigint DEFAULT NULL,
  `staff_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_wasm_event_staff` (`event_name`,`staff_id`),
  KEY `idx_wasm_entity` (`entity_id`),
  KEY `idx_wasm_event_staff_entity` (`event_name`,`staff_id`,`entity_id`)
) ENGINE=InnoDB AUTO_INCREMENT=737 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
