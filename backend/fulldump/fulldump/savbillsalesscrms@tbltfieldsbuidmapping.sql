-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tbltfieldsbuidmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltfieldsbuidmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltfieldsbuidmapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `field_id` bigint DEFAULT NULL,
  `field_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `buid` bigint DEFAULT NULL,
  `is_mandatory` tinyint(1) DEFAULT '0',
  `default_mandatory` tinyint(1) DEFAULT NULL,
  `screen` bigint DEFAULT NULL,
  `module` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `data_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `serviceparamid` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `CREATEDATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'admin',
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'admin',
  `CREATEDBYSTAFFID` bigint DEFAULT '2',
  `LASTMODIFIEDBYSTAFFID` bigint DEFAULT '2',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltfieldsBuidMapping_field_unq` (`id`),
  KEY `fk_buid_tbltfieldsbuidmapping` (`buid`),
  KEY `fk_tbltfieldsbuidmapping_screen` (`screen`),
  CONSTRAINT `fk_buid_tbltfieldsbuidmapping` FOREIGN KEY (`buid`) REFERENCES `tblmbusinessunit` (`businessunitid`),
  CONSTRAINT `fk_tbltfieldsbuidmapping_screen` FOREIGN KEY (`screen`) REFERENCES `tblmscreens` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
