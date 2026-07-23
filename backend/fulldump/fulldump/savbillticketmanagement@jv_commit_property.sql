-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillticketmanagement    Table: jv_commit_property
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `jv_commit_property`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `jv_commit_property` (
  `property_name` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `property_value` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `commit_fk` bigint NOT NULL,
  PRIMARY KEY (`commit_fk`,`property_name`),
  KEY `jv_commit_property_commit_fk_idx` (`commit_fk`),
  KEY `jv_commit_property_property_name_property_value_idx` (`property_name`,`property_value`(191)),
  CONSTRAINT `jv_commit_property_commit_fk` FOREIGN KEY (`commit_fk`) REFERENCES `jv_commit` (`commit_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
