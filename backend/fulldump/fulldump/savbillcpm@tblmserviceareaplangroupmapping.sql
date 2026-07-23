-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmserviceareaplangroupmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmserviceareaplangroupmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmserviceareaplangroupmapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `plangroupid` bigint NOT NULL,
  `service_area_id` bigint NOT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblmserviceareaplangroupmapping_uniq` (`id`),
  KEY `tblmserviceareaplangroupmapping_fk2` (`plangroupid`),
  KEY `tblmserviceareaplangroupmapping_fk1` (`service_area_id`),
  CONSTRAINT `tblmserviceareaplangroupmapping_fk1` FOREIGN KEY (`service_area_id`) REFERENCES `tblservicearea` (`service_area_id`),
  CONSTRAINT `tblmserviceareaplangroupmapping_fk2` FOREIGN KEY (`plangroupid`) REFERENCES `tblmplangroup` (`plangroupid`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
