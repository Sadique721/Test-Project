-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: commongateway    Table: tblmpaymentconfig
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmpaymentconfig`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmpaymentconfig` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `payment_config_name` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `is_active` tinyint(1) DEFAULT '0',
  `mvnoid` bigint DEFAULT NULL,
  `is_delete` tinyint(1) DEFAULT '0',
  `create_date` datetime DEFAULT NULL,
  `payment_gateway_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblmpaymentconfig_unique` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
