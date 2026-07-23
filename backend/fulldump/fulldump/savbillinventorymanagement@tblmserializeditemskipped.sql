-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillinventorymanagement    Table: tblmserializeditemskipped
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmserializeditemskipped`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmserializeditemskipped` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `inward_id` bigint DEFAULT NULL,
  `mvno_id` bigint DEFAULT NULL,
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
  `reason` text COLLATE utf8mb4_unicode_ci,
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `outward_id` bigint DEFAULT NULL,
  `type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `msisdn` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `trackable` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `port` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `serial` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mac` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tblmserializeditemskipped_mvno_id` (`mvno_id`),
  KEY `serializeditemskipped_inward_id_fk` (`inward_id`),
  KEY `fk_serializeditemskipped_outward_id` (`outward_id`),
  CONSTRAINT `fk_serializeditemskipped_outward_id` FOREIGN KEY (`outward_id`) REFERENCES `tblmoutward` (`outward_id`),
  CONSTRAINT `serializeditemskipped_inward_id_fk` FOREIGN KEY (`inward_id`) REFERENCES `tblminward` (`inward_id`),
  CONSTRAINT `serializeditemskipped_mvno_id_fk` FOREIGN KEY (`mvno_id`) REFERENCES `tblmmvno` (`MVNOID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
