-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: pravin_test_soci    Table: maintenance_payment
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `maintenance_payment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `maintenance_payment` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `PAYMENT_DATE` date DEFAULT NULL,
  `STATUS` enum('PAID','PENDING') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MAINTENANCE_ID` bigint DEFAULT NULL,
  `USER_ID` bigint DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKasf5by5v4dtplnf5um9xvbrup` (`MAINTENANCE_ID`),
  KEY `FKif4cn5cytxpgw9fe95hxgpiw8` (`USER_ID`),
  CONSTRAINT `FKasf5by5v4dtplnf5um9xvbrup` FOREIGN KEY (`MAINTENANCE_ID`) REFERENCES `maintenance` (`ID`),
  CONSTRAINT `FKif4cn5cytxpgw9fe95hxgpiw8` FOREIGN KEY (`USER_ID`) REFERENCES `user` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
