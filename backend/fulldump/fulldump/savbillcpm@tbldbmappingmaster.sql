-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbldbmappingmaster
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbldbmappingmaster`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbldbmappingmaster` (
  `mappingmasterid` bigint NOT NULL AUTO_INCREMENT,
  `mappingname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodified_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`mappingmasterid`),
  UNIQUE KEY `dbmappingmaster_Id_unq` (`mappingmasterid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
