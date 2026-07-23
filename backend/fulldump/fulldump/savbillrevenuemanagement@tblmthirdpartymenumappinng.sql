-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillrevenuemanagement    Table: tblmthirdpartymenumappinng
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmthirdpartymenumappinng`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmthirdpartymenumappinng` (
  `id` bigint NOT NULL,
  `third_party_menu_id` bigint DEFAULT NULL,
  `third_party_param_name` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `third_party_param_value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `third_party_parame_desc` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
