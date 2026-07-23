-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillinventorymanagement    Table: tblminventoryspecification
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblminventoryspecification`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblminventoryspecification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `param_id` bigint NOT NULL,
  `param_value` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `inward_id` bigint NOT NULL,
  `inven_spec_id` bigint NOT NULL,
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createdbystaffid` bigint DEFAULT NULL,
  `lastmodifiedbystaffid` bigint DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblminventoryspecification_id_unq` (`id`),
  KEY `tblminventoryspecification_param_id_pk` (`param_id`),
  KEY `tblminventoryspecification_InwardId` (`inward_id`),
  CONSTRAINT `tblminventoryspecification_inward_id_pk` FOREIGN KEY (`inward_id`) REFERENCES `tblminward` (`inward_id`),
  CONSTRAINT `tblminventoryspecification_param_id_pk` FOREIGN KEY (`param_id`) REFERENCES `tblmspecificationparameter` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
