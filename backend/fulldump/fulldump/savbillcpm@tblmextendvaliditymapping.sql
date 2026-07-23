-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmextendvaliditymapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmextendvaliditymapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmextendvaliditymapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `downtime_start_date` date DEFAULT NULL,
  `downtime_expiry_date` date DEFAULT NULL,
  `extend_validity_remarks` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `custservicemappingid` bigint DEFAULT NULL,
  `custpackageid` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblmextendvaliditymapping_id_uniq` (`id`),
  KEY `custpackageid_ext_fk` (`custpackageid`),
  CONSTRAINT `custpackageid_ext_fk` FOREIGN KEY (`custpackageid`) REFERENCES `tblcustpackagerel` (`custpackageid`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
