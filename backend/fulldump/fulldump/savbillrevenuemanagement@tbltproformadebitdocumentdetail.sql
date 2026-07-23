-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillrevenuemanagement    Table: tbltproformadebitdocumentdetail
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltproformadebitdocumentdetail`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltproformadebitdocumentdetail` (
  `proformadebitdocaddrid` bigint NOT NULL AUTO_INCREMENT,
  `proformadebitdocumentid` bigint NOT NULL,
  `chargeid` decimal(20,0) NOT NULL,
  `chargename` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `chargetype` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `chargecycle` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `subtotal` decimal(20,4) DEFAULT '0.0000',
  `tax` decimal(20,4) DEFAULT '0.0000',
  `discount` decimal(20,4) DEFAULT '0.0000',
  `totalamount` decimal(20,4) DEFAULT '0.0000',
  `startdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `enddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `prorationtype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `noofcycle` decimal(20,4) DEFAULT '0.0000',
  `planid` bigint DEFAULT NULL,
  PRIMARY KEY (`proformadebitdocaddrid`),
  UNIQUE KEY `tproformadebitdocumentdetail_proformadebitdocaddrid_pk` (`proformadebitdocaddrid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
