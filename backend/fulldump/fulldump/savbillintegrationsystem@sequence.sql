-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillintegrationsystem    Table: sequence
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `sequence`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `sequence` (
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `increment` bigint NOT NULL DEFAULT '1',
  `min_value` bigint NOT NULL DEFAULT '1',
  `max_value` bigint NOT NULL DEFAULT '9223372036854775807',
  `cur_value` bigint NOT NULL DEFAULT '1',
  `cycle` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
