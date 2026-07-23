-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: pravin_test_soci    Table: user
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `user`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `user` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `CREATED_AT` datetime(6) DEFAULT NULL,
  `EMAIL` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `GENDER` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `LAST_LOGIN` datetime(6) DEFAULT NULL,
  `MOBILE_NUMBER` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `PASSWORD` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `PROFILE_PICTURE` longblob,
  `ROLE` enum('ADMIN','SUPER_ADMIN','USER') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `STATUS` enum('ACTIVE','DELETE','INACTIVE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `SUPER_ADMIN` tinyint(1) DEFAULT '0',
  `UPDATED_AT` datetime(6) DEFAULT NULL,
  `FLAT_ID` bigint DEFAULT NULL,
  `SOCIETY_ID` bigint DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UKc23jkj330b0khhv3v45lscqkp` (`EMAIL`),
  UNIQUE KEY `UK5g5h82heohqxdfh4kco92vjlk` (`MOBILE_NUMBER`),
  UNIQUE KEY `UKso0sqx19ynumkrme53oc1xbjw` (`FLAT_ID`),
  KEY `FKokaiqt5fhuha7d6m2iek4jsod` (`SOCIETY_ID`),
  CONSTRAINT `FK9x8c9hpopgukc12q9p01ksnar` FOREIGN KEY (`FLAT_ID`) REFERENCES `flat` (`ID`),
  CONSTRAINT `FKokaiqt5fhuha7d6m2iek4jsod` FOREIGN KEY (`SOCIETY_ID`) REFERENCES `society` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
