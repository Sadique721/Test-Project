-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillpartnermanagement    Table: tblpricebookplandtls
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblpricebookplandtls`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblpricebookplandtls` (
  `pbdetailid` bigint NOT NULL,
  `planid` bigint DEFAULT NULL,
  `bookid` bigint DEFAULT NULL,
  `offerprice` decimal(20,4) DEFAULT '0.0000',
  `partnerofficeprice` decimal(20,4) DEFAULT '0.0000',
  `revsharen` char(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Yes',
  `registration` char(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Yes',
  `renewal` char(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Yes',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `revenue_share_percentage` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_tax_included` tinyint(1) DEFAULT '0',
  `planbundleid` bigint DEFAULT NULL,
  PRIMARY KEY (`pbdetailid`),
  UNIQUE KEY `pricebookplandtls_pbdetailid_unq` (`pbdetailid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
