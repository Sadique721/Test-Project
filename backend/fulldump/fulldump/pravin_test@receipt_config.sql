-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: pravin_test    Table: receipt_config
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `receipt_config`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `receipt_config` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `LAB_LIB_SPORTS_FEES` double DEFAULT NULL,
  `OTHER_FEES` double DEFAULT NULL,
  `RECEIPT_PREFIX` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `STUDENT_DEV_FEES` double DEFAULT NULL,
  `TUITION_FEES` double DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
