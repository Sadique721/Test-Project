-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillinventorymanagement    Table: tblhinventoryspecificationhistory
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblhinventoryspecificationhistory`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblhinventoryspecificationhistory` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `itemid` bigint NOT NULL,
  `param_id` bigint NOT NULL,
  `param_value` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_mandatory` tinyint(1) DEFAULT '0',
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdbystaffid` bigint DEFAULT NULL,
  `lastmodifiedbystaffid` bigint DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `inven_id` bigint DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblhinventoryspecificationhistory_id_unq` (`id`),
  KEY `tblhinventoryspecificationhistory_itemid_fk` (`itemid`),
  KEY `tblhinventoryspecificationhistory_param_id_fk` (`param_id`),
  KEY `inventoryspecificationhistory_inven_id_fk` (`inven_id`),
  CONSTRAINT `inventoryspecificationhistory_inven_id_fk` FOREIGN KEY (`inven_id`) REFERENCES `tblminventoryspecification` (`id`),
  CONSTRAINT `tblhinventoryspecificationhistory_itemid_fk` FOREIGN KEY (`itemid`) REFERENCES `tblmserializeditem` (`serialized_item_id`),
  CONSTRAINT `tblhinventoryspecificationhistory_param_id_fk` FOREIGN KEY (`param_id`) REFERENCES `tblmspecificationparameter` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
