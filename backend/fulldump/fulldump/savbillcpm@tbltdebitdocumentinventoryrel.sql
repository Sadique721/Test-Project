-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltdebitdocumentinventoryrel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltdebitdocumentinventoryrel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltdebitdocumentinventoryrel` (
  `debitdocinvid` bigint NOT NULL AUTO_INCREMENT,
  `debitdocumentid` bigint DEFAULT NULL,
  `cust_inventory_mapping_id` bigint DEFAULT NULL,
  `product_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` decimal(20,4) DEFAULT NULL,
  `tax` decimal(20,4) DEFAULT NULL,
  `assigned_date` timestamp NULL DEFAULT NULL,
  `expiray_date` timestamp NULL DEFAULT NULL,
  `product_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `item_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `item_serial_number` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `item_mac` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `connection_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`debitdocinvid`),
  UNIQUE KEY `tdebitdocumentinventoryrel_debitdocinvid_unq` (`debitdocinvid`),
  KEY `tbltdebitdocumentinventoryrel_docin_1` (`debitdocumentid`),
  KEY `tbltdebitdocumentinventoryrel_custin_1` (`cust_inventory_mapping_id`),
  CONSTRAINT `tbltdebitdocumentinventoryrel_docin_1` FOREIGN KEY (`debitdocumentid`) REFERENCES `tbltdebitdocument` (`debitdocumentid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
