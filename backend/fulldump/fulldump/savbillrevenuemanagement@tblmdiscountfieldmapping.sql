-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillrevenuemanagement    Table: tblmdiscountfieldmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmdiscountfieldmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmdiscountfieldmapping` (
  `DISCOUNTFIELDMAPPINGID` bigint NOT NULL AUTO_INCREMENT,
  `VALIDFROM` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `VALIDUPTO` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `DISCOUNTTYPE` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DISCOUNT` decimal(16,4) DEFAULT NULL,
  `DISCOUNTID` bigint DEFAULT NULL,
  PRIMARY KEY (`DISCOUNTFIELDMAPPINGID`),
  UNIQUE KEY `mdiscountfieldmapping_Id_unq` (`DISCOUNTFIELDMAPPINGID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
