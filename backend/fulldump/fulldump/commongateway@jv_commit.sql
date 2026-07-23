-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: commongateway    Table: jv_commit
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `jv_commit`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `jv_commit` (
  `commit_pk` bigint NOT NULL AUTO_INCREMENT,
  `author` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `commit_date` timestamp(3) NULL DEFAULT NULL,
  `commit_date_instant` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `commit_id` decimal(22,2) DEFAULT NULL,
  PRIMARY KEY (`commit_pk`),
  KEY `jv_commit_commit_id_idx` (`commit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=41541 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
