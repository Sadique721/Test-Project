-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbilltaskmanagement    Table: tblstaffservicearearel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblstaffservicearearel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblstaffservicearearel` (
  `serviceareaid` bigint NOT NULL,
  `staffid` bigint NOT NULL,
  `created_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodified_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `CREATEDATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `CREATEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblstaffservicearearel_uni` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000124 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
