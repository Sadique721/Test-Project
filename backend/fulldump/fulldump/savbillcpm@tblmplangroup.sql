-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmplangroup
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmplangroup`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmplangroup` (
  `plangroupid` bigint NOT NULL AUTO_INCREMENT,
  `plangroupname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `servicearea_id` bigint DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `CREATEDATE` timestamp NULL DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` bigint DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` bigint DEFAULT NULL,
  `plantype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `planmode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dbr` double DEFAULT NULL,
  `BUID` bigint DEFAULT NULL,
  `plangrouptype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `PLANCATEGORY` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `next_team_hir_mapping` bigint DEFAULT NULL,
  `next_staff` bigint DEFAULT NULL,
  `accessibility` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `allowdiscount` bit(1) DEFAULT b'1',
  `invoicetoorg` tinyint(1) DEFAULT '1',
  `requiredapproval` tinyint(1) DEFAULT '1',
  `offerprice` decimal(20,8) DEFAULT NULL,
  `template_id` bigint DEFAULT NULL,
  `mvno_name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`plangroupid`),
  UNIQUE KEY `tblmplangroup_plangroupid_unq` (`plangroupid`),
  KEY `businessunit_plangroup_fk` (`BUID`),
  KEY `tblmplangroup_servicearea_id_fk` (`servicearea_id`),
  CONSTRAINT `tblmplangroup_servicearea_id_fk` FOREIGN KEY (`servicearea_id`) REFERENCES `tblservicearea` (`service_area_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
