-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltservicearealocationmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltservicearealocationmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltservicearealocationmapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `service_area_id` bigint NOT NULL,
  `location_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_location` (`location_id`),
  KEY `fk_service_area` (`service_area_id`),
  CONSTRAINT `fk_location` FOREIGN KEY (`location_id`) REFERENCES `tblmlocationmaster` (`locationid`),
  CONSTRAINT `fk_service_area` FOREIGN KEY (`service_area_id`) REFERENCES `tblservicearea` (`service_area_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
