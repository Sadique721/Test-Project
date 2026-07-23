-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tbltfollowupremark
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltfollowupremark`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltfollowupremark` (
  `follow_up_remark_id` bigint NOT NULL AUTO_INCREMENT,
  `remark` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `lead_follow_up_id` bigint DEFAULT NULL,
  `created_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`follow_up_remark_id`),
  KEY `tblt_follow_up_remark_lead_follow_up_id_fk` (`lead_follow_up_id`),
  CONSTRAINT `tblt_follow_up_remark_lead_follow_up_id_fk` FOREIGN KEY (`lead_follow_up_id`) REFERENCES `tbltleadfollowup` (`lead_follow_up_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
