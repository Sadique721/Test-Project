-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillintegrationsystem    Table: tbltnavmasteraggreagtionparammapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltnavmasteraggreagtionparammapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltnavmasteraggreagtionparammapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nav_master_id` bigint DEFAULT NULL,
  `param_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltnavmasteraggreagtionparammapping_id` (`id`),
  KEY `fk_tbltnavmasteraggreagtionparammapping_nav_master_id` (`nav_master_id`),
  CONSTRAINT `fk_tbltnavmasteraggreagtionparammapping_nav_master_id` FOREIGN KEY (`nav_master_id`) REFERENCES `tblmnavmaster` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
