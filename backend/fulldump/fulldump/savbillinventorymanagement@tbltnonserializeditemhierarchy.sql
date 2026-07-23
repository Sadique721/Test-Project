-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillinventorymanagement    Table: tbltnonserializeditemhierarchy
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltnonserializeditemhierarchy`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltnonserializeditemhierarchy` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_item_id` bigint DEFAULT NULL,
  `child_item_id` bigint DEFAULT NULL,
  `qty` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `mvno_id` bigint DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdbystaffid` bigint DEFAULT NULL,
  `lastmodifiedbystaffid` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltnonserializeditemhierarchy_id_unq` (`id`),
  KEY `nonserializeditemhierarchy_mvno_id_fk` (`mvno_id`),
  CONSTRAINT `nonserializeditemhierarchy_mvno_id_fk` FOREIGN KEY (`mvno_id`) REFERENCES `tblmmvno` (`MVNOID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
