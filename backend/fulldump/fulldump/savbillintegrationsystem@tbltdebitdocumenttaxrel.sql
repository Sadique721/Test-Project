-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillintegrationsystem    Table: tbltdebitdocumenttaxrel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltdebitdocumenttaxrel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltdebitdocumenttaxrel` (
  `debitdoctaxid` bigint NOT NULL,
  `debitdocumentid` bigint DEFAULT NULL,
  `taxid` bigint DEFAULT NULL,
  `taxname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `percentage` decimal(10,2) DEFAULT NULL,
  `taxlevel` decimal(10,2) DEFAULT NULL,
  `startdate` timestamp NULL DEFAULT NULL,
  `enddate` timestamp NULL DEFAULT NULL,
  `amount` decimal(10,2) DEFAULT NULL,
  `tax_ledger_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`debitdoctaxid`),
  KEY `fk_tbltdebitdocumenttaxrel_debitdocumentid` (`debitdocumentid`),
  CONSTRAINT `fk_tbltdebitdocumenttaxrel_debitdocumentid` FOREIGN KEY (`debitdocumentid`) REFERENCES `tbltdebitdocument` (`debitdocumentid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
