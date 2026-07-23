-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillticketmanagement    Table: tbltbranchservicemapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltbranchservicemapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltbranchservicemapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `branch_mapping_id` bigint DEFAULT NULL,
  `serviceid` bigint DEFAULT NULL,
  `revenue_share_percentage` double DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltbranchservicemapping_field_unq` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
