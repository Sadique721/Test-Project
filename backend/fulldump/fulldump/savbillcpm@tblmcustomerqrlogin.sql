-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmcustomerqrlogin
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmcustomerqrlogin`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmcustomerqrlogin` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `username` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `password` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `status` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `MVNOID` bigint DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblmcustomerqrlogin_unq` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
