-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblippooldtls
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblippooldtls`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblippooldtls` (
  `pool_details_id` bigint NOT NULL AUTO_INCREMENT,
  `pool_id` bigint DEFAULT NULL,
  `ip_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_delete` tinyint(1) NOT NULL,
  `CREATEDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `LASTMODIFIEDDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CREATEDBYSTAFFID` bigint NOT NULL,
  `LASTMODIFIEDBYSTAFFID` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `allocated_id` bigint DEFAULT NULL,
  `unblock_time` datetime DEFAULT NULL,
  `block_by_cust_id` bigint DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`pool_details_id`),
  UNIQUE KEY `ippooldtls_pool_details_id_unq` (`pool_details_id`),
  KEY `tblippooldtls_ibfk_1` (`pool_id`),
  CONSTRAINT `tblippooldtls_ibfk_1` FOREIGN KEY (`pool_id`) REFERENCES `tblippool` (`pool_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
