-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: commongateway    Table: tblmpaymentconfigmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmpaymentconfigmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmpaymentconfigmapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `payment_config_id` bigint DEFAULT NULL,
  `payment_parameter_name` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `payment_parameter_value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `payment_parameter_description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `payment_parameter_for` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblmpaymentconfigmapping_unique` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=248 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
