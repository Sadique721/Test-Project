-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tblmdevicedriver
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmdevicedriver`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmdevicedriver` (
  `devicedriverid` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `username` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `password` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `address` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `user_dn` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mvnoid` bigint DEFAULT NULL,
  `is_delete` tinyint(1) DEFAULT '0',
  `createdby` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodificationdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `username_attr` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `password_attr` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`devicedriverid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
