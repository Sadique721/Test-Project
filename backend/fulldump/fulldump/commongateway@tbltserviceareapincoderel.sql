-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: commongateway    Table: tbltserviceareapincoderel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltserviceareapincoderel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltserviceareapincoderel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `serviceareaid` bigint NOT NULL,
  `pincodeid` bigint NOT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `cityid` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltserviceareapincoderel_uniq` (`id`),
  KEY `tblserviceareapincoderel_fk2` (`pincodeid`),
  KEY `tblserviceareapincoderel_fk1` (`serviceareaid`),
  CONSTRAINT `tblserviceareapincoderel_fk1` FOREIGN KEY (`serviceareaid`) REFERENCES `tblmservicearea` (`service_area_id`),
  CONSTRAINT `tblserviceareapincoderel_fk2` FOREIGN KEY (`pincodeid`) REFERENCES `tblmpincode` (`pincodeid`)
) ENGINE=InnoDB AUTO_INCREMENT=18477 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
