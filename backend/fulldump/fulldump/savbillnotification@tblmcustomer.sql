-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillnotification    Table: tblmcustomer
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmcustomer`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmcustomer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `custid` bigint DEFAULT NULL,
  `username` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_notification_enable` tinyint(1) DEFAULT '1',
  `mvnoid` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblmcustomer_field_unq` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=54806 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
