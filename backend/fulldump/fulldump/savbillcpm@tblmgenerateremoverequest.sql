-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmgenerateremoverequest
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmgenerateremoverequest`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmgenerateremoverequest` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `macmappingid` bigint DEFAULT NULL,
  `customerinventoryid` bigint DEFAULT NULL,
  `customerid` bigint DEFAULT NULL,
  `staffid` bigint DEFAULT NULL,
  `isflag` tinyint(1) DEFAULT '0',
  `requeststatus` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `revisedcharge` bigint DEFAULT NULL,
  `is_deleted` bigint DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
