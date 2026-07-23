-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillintegrationsystem    Table: tbltnmsintegrationparameters
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltnmsintegrationparameters`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltnmsintegrationparameters` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `item_id` bigint DEFAULT NULL,
  `integration_id` bigint DEFAULT NULL,
  `param_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `param_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tbltnmsintegrationparameters_integration_id_fk_1` (`integration_id`),
  CONSTRAINT `tbltnmsintegrationparameters_integration_id_fk_1` FOREIGN KEY (`integration_id`) REFERENCES `tblmnmsintegration` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
