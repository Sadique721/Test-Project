-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tbltfieldserviceparamrel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltfieldserviceparamrel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltfieldserviceparamrel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fieldid` bigint DEFAULT NULL,
  `serviceparamid` bigint DEFAULT NULL,
  `is_mandatory` tinyint DEFAULT NULL,
  `module` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_deleted` tinyint DEFAULT NULL,
  `CREATEDATE` timestamp NULL DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` bigint DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_serviceparamfieldmapping` (`serviceparamid`),
  CONSTRAINT `fk_serviceparamfieldmapping` FOREIGN KEY (`serviceparamid`) REFERENCES `tblmserviceparams` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
