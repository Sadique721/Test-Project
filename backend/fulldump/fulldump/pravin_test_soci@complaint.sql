-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: pravin_test_soci    Table: complaint
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `complaint`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `complaint` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `COMPLAINT_DATE` datetime(6) DEFAULT NULL,
  `DESCRIPTION` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `FLAT_NUMBER` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `RESOLVED_DATE` datetime(6) DEFAULT NULL,
  `STATUS` enum('OPEN','PENDING','RESOLVED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `USER_ID` bigint DEFAULT NULL,
  `IMAGE_ID` bigint DEFAULT NULL,
  `RESOLVED_BY_ID` bigint DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UKnf4qpmfu0bf67lj0xnxn4o1jm` (`IMAGE_ID`),
  KEY `FKnbsnpi4jxaw4q0cbc4o6erio8` (`USER_ID`),
  KEY `FKqpuk3itmjrmicg3rpvuooksby` (`RESOLVED_BY_ID`),
  CONSTRAINT `FKnbsnpi4jxaw4q0cbc4o6erio8` FOREIGN KEY (`USER_ID`) REFERENCES `user` (`ID`),
  CONSTRAINT `FKqpuk3itmjrmicg3rpvuooksby` FOREIGN KEY (`RESOLVED_BY_ID`) REFERENCES `user` (`ID`),
  CONSTRAINT `FKtl2rs6tqan4mp290w34x8r03w` FOREIGN KEY (`IMAGE_ID`) REFERENCES `complaint_image` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
