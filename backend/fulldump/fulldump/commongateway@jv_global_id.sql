-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: commongateway    Table: jv_global_id
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `jv_global_id`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `jv_global_id` (
  `global_id_pk` bigint NOT NULL AUTO_INCREMENT,
  `local_id` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fragment` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `owner_id_fk` bigint DEFAULT NULL,
  PRIMARY KEY (`global_id_pk`),
  KEY `jv_global_id_local_id_idx` (`local_id`),
  KEY `jv_global_id_owner_id_fk_idx` (`owner_id_fk`),
  CONSTRAINT `jv_global_id_owner_id_fk` FOREIGN KEY (`owner_id_fk`) REFERENCES `jv_global_id` (`global_id_pk`)
) ENGINE=InnoDB AUTO_INCREMENT=66819 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
