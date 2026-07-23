-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillinventorymanagement    Table: tbltwarehousemanagmentservicearearel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltwarehousemanagmentservicearearel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltwarehousemanagmentservicearearel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `serviceareaid` bigint NOT NULL,
  `warehouse_id` bigint NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodified_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltwarehousemanagmentservicearearel_id_unq` (`id`),
  KEY `idxwarehousemanagmentserviceareaid` (`serviceareaid`),
  KEY `tbltwarehousemanagmentservicearearel_warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
