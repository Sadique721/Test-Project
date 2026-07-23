-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblttrialdebitdocumenttaxrel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblttrialdebitdocumenttaxrel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblttrialdebitdocumenttaxrel` (
  `trialdebitdoctaxid` bigint NOT NULL AUTO_INCREMENT,
  `trialdebitdocumentid` bigint NOT NULL,
  `taxid` bigint NOT NULL,
  `taxname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `percentage` decimal(20,4) DEFAULT '0.0000',
  `taxlevel` decimal(20,4) DEFAULT '0.0000',
  `startdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `enddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `amount` decimal(20,4) DEFAULT '0.0000',
  PRIMARY KEY (`trialdebitdoctaxid`),
  UNIQUE KEY `ttrialdebitdocumenttaxrel_trialdebitdoctaxid_unq` (`trialdebitdoctaxid`),
  KEY `tblttrialdebitdocumenttaxrel_ibfk_1` (`trialdebitdocumentid`),
  KEY `tblttrialdebitdocumenttaxrel_ibfk_2` (`taxid`),
  CONSTRAINT `tblttrialdebitdocumenttaxrel_ibfk_1` FOREIGN KEY (`trialdebitdocumentid`) REFERENCES `tblttrialdebitdocument` (`trialdebitdocumentid`),
  CONSTRAINT `tblttrialdebitdocumenttaxrel_ibfk_2` FOREIGN KEY (`taxid`) REFERENCES `tblmtax` (`TAXID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
