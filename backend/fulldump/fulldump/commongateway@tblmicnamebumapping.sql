-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: commongateway    Table: tblmicnamebumapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmicnamebumapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmicnamebumapping` (
  `icname_bu_mappingid` bigint NOT NULL AUTO_INCREMENT,
  `businessunitid` bigint DEFAULT NULL,
  `investmentcode_id` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`icname_bu_mappingid`),
  KEY `investmentcode_id_fk` (`investmentcode_id`),
  KEY `businessunit_id_fk` (`businessunitid`),
  CONSTRAINT `businessunit_id_fk` FOREIGN KEY (`businessunitid`) REFERENCES `tblmbusinessunit` (`businessunitid`),
  CONSTRAINT `investmentcode_id_fk` FOREIGN KEY (`investmentcode_id`) REFERENCES `tbltinvestmentcode` (`investmentcode_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
