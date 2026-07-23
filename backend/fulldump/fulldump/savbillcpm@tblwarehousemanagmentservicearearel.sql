-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblwarehousemanagmentservicearearel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblwarehousemanagmentservicearearel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblwarehousemanagmentservicearearel` (
  `serviceareaid` bigint NOT NULL,
  `warehouse_id` bigint NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodified_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `id` bigint NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblwarehousemanagmentservicearearel_unique` (`id`),
  KEY `tblstaffservicearearel_unqique12` (`warehouse_id`),
  KEY `tblstaffservicearearel_unique11` (`serviceareaid`),
  CONSTRAINT `tblstaffservicearearel_unique11` FOREIGN KEY (`serviceareaid`) REFERENCES `tblservicearea` (`service_area_id`),
  CONSTRAINT `tblstaffservicearearel_unqique12` FOREIGN KEY (`warehouse_id`) REFERENCES `tbltwarehousemanagement` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
