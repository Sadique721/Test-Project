-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: pravin_test_soci    Table: plan_benefits
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `plan_benefits`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `plan_benefits` (
  `PLAN_ID` bigint NOT NULL,
  `BENEFIT` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  KEY `FKl8if7sspoiusn2a95sap5a9js` (`PLAN_ID`),
  CONSTRAINT `FKl8if7sspoiusn2a95sap5a9js` FOREIGN KEY (`PLAN_ID`) REFERENCES `plan_detail` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
