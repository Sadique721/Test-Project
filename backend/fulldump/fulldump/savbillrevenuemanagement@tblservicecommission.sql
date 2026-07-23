-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillrevenuemanagement    Table: tblservicecommission
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblservicecommission`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblservicecommission` (
  `id` bigint NOT NULL,
  `bookid` bigint DEFAULT NULL,
  `serviceid` bigint DEFAULT NULL,
  `revenue_share_percentage` bigint DEFAULT NULL,
  `service_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `royalty_percentage` decimal(20,4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `servicecommission_id_unq` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
