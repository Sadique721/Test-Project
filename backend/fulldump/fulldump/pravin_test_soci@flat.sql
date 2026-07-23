-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: pravin_test_soci    Table: flat
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `flat`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `flat` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `BLOCK` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `FLAT_NUMBER` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `OWNER_NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `SOCIETY_ID` bigint DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKglk63p0tnt01tloiibb6k2rui` (`SOCIETY_ID`),
  CONSTRAINT `FKglk63p0tnt01tloiibb6k2rui` FOREIGN KEY (`SOCIETY_ID`) REFERENCES `society` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
