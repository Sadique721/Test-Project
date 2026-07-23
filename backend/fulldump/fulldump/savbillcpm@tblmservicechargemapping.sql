-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmservicechargemapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmservicechargemapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmservicechargemapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `chargeid` bigint NOT NULL,
  `servicesid` bigint NOT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblmservicechargemapping_uniq` (`id`),
  KEY `tblmservicechargemapping_fk2` (`chargeid`),
  KEY `tblmservicechargemapping_fk1` (`servicesid`),
  CONSTRAINT `tblmservicechargemapping_fk1` FOREIGN KEY (`servicesid`) REFERENCES `tblmservices` (`serviceid`),
  CONSTRAINT `tblmservicechargemapping_fk2` FOREIGN KEY (`chargeid`) REFERENCES `tblcharges` (`CHARGEID`)
) ENGINE=InnoDB AUTO_INCREMENT=358 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
