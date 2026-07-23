-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmpartnerledger
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmpartnerledger`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmpartnerledger` (
  `partnerledger_id` bigint NOT NULL AUTO_INCREMENT,
  `totaldue` decimal(20,4) DEFAULT NULL,
  `totalpaid` decimal(20,4) DEFAULT NULL,
  `partner_id` bigint DEFAULT NULL,
  `CREATEDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `LASTMODIFIEDDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`partnerledger_id`),
  UNIQUE KEY `mpartnerledger_partnerledger_id_unq` (`partnerledger_id`),
  KEY `tblmpartnerledger_ibfk_1` (`partner_id`),
  CONSTRAINT `tblmpartnerledger_ibfk_1` FOREIGN KEY (`partner_id`) REFERENCES `tblpartners` (`PARTNERID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
