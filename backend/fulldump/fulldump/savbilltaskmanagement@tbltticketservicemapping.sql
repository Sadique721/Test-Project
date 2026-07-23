-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbilltaskmanagement    Table: tbltticketservicemapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltticketservicemapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltticketservicemapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ticket_id` bigint DEFAULT NULL,
  `service_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltticketservicemapping_uniq` (`id`),
  KEY `tbltticketservicemapping_fk2` (`ticket_id`),
  KEY `tbltticketservicemapping_fk1` (`service_id`),
  CONSTRAINT `tbltticketservicemapping_fk1` FOREIGN KEY (`service_id`) REFERENCES `tblmservices` (`serviceid`),
  CONSTRAINT `tbltticketservicemapping_fk2` FOREIGN KEY (`ticket_id`) REFERENCES `tblcases` (`case_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
