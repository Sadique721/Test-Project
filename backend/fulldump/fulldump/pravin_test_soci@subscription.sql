-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: pravin_test_soci    Table: subscription
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `subscription`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `subscription` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `END_DATE` date DEFAULT NULL,
  `IS_FREE_TRIAL` bit(1) NOT NULL,
  `LAST_UPDATED` date DEFAULT NULL,
  `START_DATE` date DEFAULT NULL,
  `STATUS` enum('EXPIRED','FREE_TRIAL_ACTIVE','PAID') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SOCIETY_ID` bigint DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK1jbddyhys283xx9ixd9syusio` (`SOCIETY_ID`),
  CONSTRAINT `FKihqi7nhkb6gsprg1x692ox2ms` FOREIGN KEY (`SOCIETY_ID`) REFERENCES `society` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
