-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmbulkconsumption
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmbulkconsumption`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmbulkconsumption` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mvno_id` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `CREATEDATE` timestamp NULL DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` bigint DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `inward_id` bigint DEFAULT NULL,
  `approval_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `approval_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `qty` bigint DEFAULT NULL,
  `itemtype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ownerid` bigint DEFAULT NULL,
  `ownertype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tblmbulkconsumption_product_id_fk` (`product_id`),
  KEY `tblmbulkconsumption_inward_id_fk` (`inward_id`),
  CONSTRAINT `tblmbulkconsumption_inward_id_fk` FOREIGN KEY (`inward_id`) REFERENCES `tbltinward` (`inward_id`),
  CONSTRAINT `tblmbulkconsumption_product_id_fk` FOREIGN KEY (`product_id`) REFERENCES `tbltproduct` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
