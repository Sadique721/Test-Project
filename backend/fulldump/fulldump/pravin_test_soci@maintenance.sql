-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: pravin_test_soci    Table: maintenance
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `maintenance`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `maintenance` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `AMOUNT` double NOT NULL,
  `DESCRIPTION` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `DUE_DATE` date NOT NULL,
  `STATUS` enum('PAID','PENDING') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SOCIETY_ID` bigint NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKqngqsf5msxdujbmp8n01ah8l3` (`SOCIETY_ID`),
  CONSTRAINT `FKqngqsf5msxdujbmp8n01ah8l3` FOREIGN KEY (`SOCIETY_ID`) REFERENCES `society` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
