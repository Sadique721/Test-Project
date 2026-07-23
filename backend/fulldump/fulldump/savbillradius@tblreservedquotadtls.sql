-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tblreservedquotadtls
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblreservedquotadtls`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblreservedquotadtls` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cust_id` bigint DEFAULT NULL,
  `custquotadtlsid` bigint DEFAULT NULL,
  `used_quota` decimal(20,4) DEFAULT NULL,
  `unused_quota` decimal(20,4) DEFAULT NULL,
  `reserved_quota` decimal(20,4) DEFAULT NULL,
  `parent_cust_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblreservedquotadtls_id_uni` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
