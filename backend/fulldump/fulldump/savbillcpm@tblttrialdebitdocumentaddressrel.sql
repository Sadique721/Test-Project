-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblttrialdebitdocumentaddressrel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblttrialdebitdocumentaddressrel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblttrialdebitdocumentaddressrel` (
  `trialdebitdocaddrid` bigint NOT NULL AUTO_INCREMENT,
  `trialdebitdocumentid` bigint NOT NULL,
  `addresstype` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `city` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `state` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `country` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pincode` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`trialdebitdocaddrid`),
  UNIQUE KEY `ttrialdebitdocumentaddressrel_id_unq` (`trialdebitdocaddrid`),
  KEY `tblttrialdebitdocumentaddressrel_ibfk_1` (`trialdebitdocumentid`),
  CONSTRAINT `tblttrialdebitdocumentaddressrel_ibfk_1` FOREIGN KEY (`trialdebitdocumentid`) REFERENCES `tblttrialdebitdocument` (`trialdebitdocumentid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
