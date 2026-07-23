-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblipallocationdtls
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblipallocationdtls`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblipallocationdtls` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `is_delete` tinyint(1) NOT NULL,
  `CREATEDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `LASTMODIFIEDDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CREATEDBYSTAFFID` bigint NOT NULL,
  `LASTMODIFIEDBYSTAFFID` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `cust_id` bigint DEFAULT NULL,
  `terminated_date` datetime DEFAULT NULL,
  `is_system_updated` tinyint(1) DEFAULT NULL,
  `termination_reason` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pool_details_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ipallocationdtls_id_unq` (`id`),
  KEY `tblipallocationdtls_FK` (`pool_details_id`),
  CONSTRAINT `tblipallocationdtls_FK` FOREIGN KEY (`pool_details_id`) REFERENCES `tblippooldtls` (`pool_details_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
