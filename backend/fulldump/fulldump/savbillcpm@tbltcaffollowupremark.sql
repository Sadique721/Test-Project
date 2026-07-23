-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltcaffollowupremark
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltcaffollowupremark`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltcaffollowupremark` (
  `caf_follow_up_remark_id` bigint NOT NULL AUTO_INCREMENT,
  `remark` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `caf_follow_up_id` bigint DEFAULT NULL,
  `created_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`caf_follow_up_remark_id`),
  UNIQUE KEY `caf_follow_up_remark_id_uni` (`caf_follow_up_remark_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
