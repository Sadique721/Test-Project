-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblnetworkdevicesservicearearel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblnetworkdevicesservicearearel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblnetworkdevicesservicearearel` (
  `serviceareaid` bigint NOT NULL,
  `deviceid` bigint NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodified_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `id` bigint NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblnetworkdevicesservicearearel_uni` (`id`),
  KEY `tblstaffservicearearel_unqique15` (`deviceid`),
  KEY `tblstaffservicearearel_unique16` (`serviceareaid`),
  CONSTRAINT `tblstaffservicearearel_unique16` FOREIGN KEY (`serviceareaid`) REFERENCES `tblservicearea` (`service_area_id`),
  CONSTRAINT `tblstaffservicearearel_unqique15` FOREIGN KEY (`deviceid`) REFERENCES `tblnetworkdevices` (`deviceid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
