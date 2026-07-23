-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tblcustipmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblcustipmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblcustipmapping` (
  `id` bigint NOT NULL,
  `custid` bigint NOT NULL,
  `ip_address` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `ip_type` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `createdate` timestamp NULL DEFAULT NULL,
  `lastmodificationdate` timestamp NULL DEFAULT NULL,
  `createdby` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `lastmodifiedby` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `custsermappingid` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblcustipmapping_id_unq` (`id`),
  KEY `custid_tblcustipmapping` (`custid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
