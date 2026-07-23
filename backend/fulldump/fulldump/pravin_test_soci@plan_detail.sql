-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: pravin_test_soci    Table: plan_detail
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `plan_detail`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `plan_detail` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `DESCRIPTION` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `DURATION_IN_DAYS` int NOT NULL,
  `PLAN_NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `PRICE` double NOT NULL,
  PRIMARY KEY (`ID`),
  CONSTRAINT `plan_detail_chk_1` CHECK (((`DURATION_IN_DAYS` <= 3650) and (`DURATION_IN_DAYS` >= 1)))
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
