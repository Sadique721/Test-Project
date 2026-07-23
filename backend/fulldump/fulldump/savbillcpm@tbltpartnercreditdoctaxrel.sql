-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltpartnercreditdoctaxrel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltpartnercreditdoctaxrel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltpartnercreditdoctaxrel` (
  `creditdoctaxid` bigint NOT NULL AUTO_INCREMENT,
  `creditdocumentid` bigint NOT NULL,
  `taxid` bigint NOT NULL,
  `taxname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `percentage` decimal(20,4) DEFAULT '0.0000',
  `taxlevel` decimal(20,4) DEFAULT '0.0000',
  `startdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `enddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `amount` decimal(20,4) DEFAULT '0.0000',
  PRIMARY KEY (`creditdoctaxid`),
  UNIQUE KEY `tpartnercreditdoctaxrel_creditdoctaxid_unq` (`creditdoctaxid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
