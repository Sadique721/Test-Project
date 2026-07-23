-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tblmippoolmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmippoolmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmippoolmapping` (
  `ippool_mapping_id` bigint NOT NULL AUTO_INCREMENT,
  `clientid` bigint DEFAULT NULL,
  `ippool_id` bigint NOT NULL,
  PRIMARY KEY (`ippool_mapping_id`),
  UNIQUE KEY `tblmippoolmapping_ippool_mapping_id_uk` (`ippool_mapping_id`),
  KEY `tblmippoolmapping_clientid_fk` (`clientid`),
  CONSTRAINT `tblmippoolmapping_clientid_fk` FOREIGN KEY (`clientid`) REFERENCES `tbltclients` (`clientid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
