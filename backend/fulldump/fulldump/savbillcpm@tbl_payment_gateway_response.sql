-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbl_payment_gateway_response
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbl_payment_gateway_response`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbl_payment_gateway_response` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `pg_id` bigint NOT NULL,
  `purchase_id` bigint NOT NULL,
  `response` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `response_date` datetime DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT NULL,
  `createdbystaffid` bigint DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT NULL,
  `lastmodifieddate` timestamp NULL DEFAULT NULL,
  `lastmodifiedbystaffid` bigint DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `payment_gateway_response_id_unq` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
