-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblpopmanagemengservicearearel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblpopmanagemengservicearearel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblpopmanagemengservicearearel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `servicearea_id` bigint NOT NULL,
  `pop_id` bigint NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodified_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblpopmanagemengservicearearel_unique` (`id`),
  KEY `tblpopmanagemengservicearearel_pop_id_fk` (`pop_id`),
  KEY `tblpopmanagemengservicearearel_servicearea_id_fk` (`servicearea_id`),
  CONSTRAINT `tblpopmanagemengservicearearel_pop_id_fk` FOREIGN KEY (`pop_id`) REFERENCES `tblmpopmanagement` (`pop_id`),
  CONSTRAINT `tblpopmanagemengservicearearel_servicearea_id_fk` FOREIGN KEY (`servicearea_id`) REFERENCES `tblservicearea` (`service_area_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
