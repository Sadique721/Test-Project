-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: pravin_test_soci    Table: society
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `society`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `society` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `ADDRESS` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TOTAL_FLATS` int NOT NULL,
  `BLOCK_TYPE` enum('ALPHABETICAL','NO_BLOCK','NUMERICAL') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `FREE_TRIAL_MONTHS` int DEFAULT NULL,
  PRIMARY KEY (`ID`),
  CONSTRAINT `society_chk_1` CHECK ((`TOTAL_FLATS` >= 1))
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
