-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: commongateway    Table: tbltbusinessverticalsmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltbusinessverticalsmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltbusinessverticalsmapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `buverticalsid` bigint NOT NULL,
  `region_id` bigint NOT NULL,
  `is_deleted` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltbusinessverticalsmapping_uniq` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
