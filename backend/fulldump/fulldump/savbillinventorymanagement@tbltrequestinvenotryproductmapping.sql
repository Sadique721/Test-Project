-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillinventorymanagement    Table: tbltrequestinvenotryproductmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltrequestinvenotryproductmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltrequestinvenotryproductmapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `inventory_request_id` bigint DEFAULT NULL,
  `product_category_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `quantity` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdbystaffid` bigint DEFAULT NULL,
  `lastmodifiedbystaffid` bigint DEFAULT NULL,
  `requeststatus` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `item_Type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltrequestinvenotryproductmapping_id_unq` (`id`),
  KEY `idx_productmapping_inventoryrequest_id` (`inventory_request_id`),
  KEY `idx_productmapping_req_deleted` (`inventory_request_id`,`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
