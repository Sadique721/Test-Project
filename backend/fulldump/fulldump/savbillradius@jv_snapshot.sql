-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: jv_snapshot
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `jv_snapshot`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `jv_snapshot` (
  `snapshot_pk` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `state` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `changed_properties` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `managed_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `global_id_fk` bigint DEFAULT NULL,
  `commit_fk` bigint DEFAULT NULL,
  PRIMARY KEY (`snapshot_pk`),
  KEY `jv_snapshot_global_id_fk_idx` (`global_id_fk`),
  KEY `jv_snapshot_commit_fk_idx` (`commit_fk`),
  CONSTRAINT `jv_snapshot_commit_fk` FOREIGN KEY (`commit_fk`) REFERENCES `jv_commit` (`commit_pk`),
  CONSTRAINT `jv_snapshot_global_id_fk` FOREIGN KEY (`global_id_fk`) REFERENCES `jv_global_id` (`global_id_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
