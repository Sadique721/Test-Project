-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltserviceinventorymapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltserviceinventorymapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltserviceinventorymapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `serviceid` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltserviceinventorymapping_uniq` (`id`),
  KEY `tbltserviceinventorymapping_fk1` (`serviceid`),
  KEY `tbltserviceinventorymapping_fk2` (`product_id`),
  CONSTRAINT `tbltserviceinventorymapping_fk1` FOREIGN KEY (`serviceid`) REFERENCES `tblmservices` (`serviceid`),
  CONSTRAINT `tbltserviceinventorymapping_fk2` FOREIGN KEY (`product_id`) REFERENCES `tblmproductcategory` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
