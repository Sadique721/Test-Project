-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillticketmanagement    Table: tblttickettatsubcategorymapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblttickettatsubcategorymapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblttickettatsubcategorymapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sub_category_mapping_id` bigint DEFAULT NULL,
  `ticket_tat_mapping_id` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `order_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblttickettatsubcategorymapping_id_unq` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
