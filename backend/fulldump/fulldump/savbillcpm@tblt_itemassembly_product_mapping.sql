-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblt_itemassembly_product_mapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblt_itemassembly_product_mapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblt_itemassembly_product_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `itemassemblyid` bigint DEFAULT NULL,
  `mac_mapping_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
