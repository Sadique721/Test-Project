-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillpartnermanagement    Table: tblpricebookslabdtls
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblpricebookslabdtls`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblpricebookslabdtls` (
  `pbslabdetailid` bigint NOT NULL,
  `bookid` bigint DEFAULT NULL,
  `from_range` bigint NOT NULL,
  `to_range` bigint NOT NULL,
  `commission_amount` bigint NOT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`pbslabdetailid`),
  UNIQUE KEY `pricebookslabdtls_pbslabdetailid_unq` (`pbslabdetailid`),
  KEY `tblpricebookslabdtls_bookid_fk` (`bookid`),
  CONSTRAINT `tblpricebookslabdtls_bookid_fk` FOREIGN KEY (`bookid`) REFERENCES `tblpricebook` (`bookid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
