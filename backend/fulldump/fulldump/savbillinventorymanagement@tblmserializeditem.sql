-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillinventorymanagement    Table: tblmserializeditem
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmserializeditem`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmserializeditem` (
  `serialized_item_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mac` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `serial_number` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `item_condition` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mvno_id` bigint DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdbystaffid` bigint DEFAULT NULL,
  `lastmodifiedbystaffid` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `product_id` bigint DEFAULT NULL,
  `current_inward_id` bigint DEFAULT NULL,
  `current_inward_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `warranty` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `warranty_period` bigint DEFAULT NULL,
  `owner_id` bigint DEFAULT NULL,
  `owner_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `item_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ownership_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `external_item_id` bigint DEFAULT NULL,
  `remaining_days` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `intransiant_warrenty` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `remarks` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `intransiant_ownership` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `intransiant_warrenty_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `expiry_date` timestamp NULL DEFAULT NULL,
  `intransiant_expiry_date` timestamp NULL DEFAULT NULL,
  `inven_spec_id` bigint DEFAULT NULL,
  `assetid` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `oemstartdate` timestamp NULL DEFAULT NULL,
  `oemenddate` timestamp NULL DEFAULT NULL,
  `oem_warranty_days` bigint DEFAULT NULL,
  `oem_warranty_status` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `imsi` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `iccid` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pin1` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `puk1` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pin2` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `puk2` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ki_encrypted` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `acc` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `adm` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `kic` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `kid` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `kik` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `msisdn` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`serialized_item_id`),
  UNIQUE KEY `tblmserializeditem_serialized_item_id_unq` (`serialized_item_id`),
  KEY `idxserializeditemownerid` (`owner_id`),
  KEY `idxserializeditemproductid` (`product_id`),
  KEY `serializeditem_mvno_id_fk` (`mvno_id`),
  KEY `tblmserializeditem_inwardId_productId` (`current_inward_id`,`product_id`),
  KEY `index_tblmserializeditem_isdelete_mac_currentinward` (`is_deleted`,`mac`,`current_inward_id`),
  CONSTRAINT `serializeditem_mvno_id_fk` FOREIGN KEY (`mvno_id`) REFERENCES `tblmmvno` (`MVNOID`)
) ENGINE=InnoDB AUTO_INCREMENT=164 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
