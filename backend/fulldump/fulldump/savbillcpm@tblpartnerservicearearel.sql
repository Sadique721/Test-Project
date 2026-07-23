-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblpartnerservicearearel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblpartnerservicearearel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblpartnerservicearearel` (
  `serviceareaid` bigint NOT NULL,
  `partnerid` bigint NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodified_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `CREATEDATE` timestamp NULL DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` bigint DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblpartnerservicearearel_uniqu` (`id`),
  KEY `tblpartnerservicearearel_ibfk_1` (`serviceareaid`),
  KEY `tblpartnerservicearearel_ibfk_2` (`partnerid`),
  CONSTRAINT `tblpartnerservicearearel_ibfk_1` FOREIGN KEY (`serviceareaid`) REFERENCES `tblservicearea` (`service_area_id`),
  CONSTRAINT `tblpartnerservicearearel_ibfk_2` FOREIGN KEY (`partnerid`) REFERENCES `tblpartners` (`PARTNERID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
