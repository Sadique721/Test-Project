-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblwarehousemanagmentparentservicearearel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblwarehousemanagmentparentservicearearel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblwarehousemanagmentparentservicearearel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parentserviceareaid` bigint NOT NULL,
  `warehouse_id` bigint NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodified_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblwarehousemanagmentparentservicearearel_unique` (`id`),
  KEY `tblwarehousemanagmentparentservicearearel_warehouse_id_fk` (`warehouse_id`),
  KEY `tblwarehousemanagmentparentservicearearel_parentserviceareaid_fk` (`parentserviceareaid`),
  CONSTRAINT `tblwarehousemanagmentparentservicearearel_parentserviceareaid_fk` FOREIGN KEY (`parentserviceareaid`) REFERENCES `tblservicearea` (`service_area_id`),
  CONSTRAINT `tblwarehousemanagmentparentservicearearel_warehouse_id_fk` FOREIGN KEY (`warehouse_id`) REFERENCES `tbltwarehousemanagement` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
