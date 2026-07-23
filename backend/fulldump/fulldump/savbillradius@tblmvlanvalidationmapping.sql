-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tblmvlanvalidationmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmvlanvalidationmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmvlanvalidationmapping` (
  `VALIDATIONMAPPINGID` bigint NOT NULL AUTO_INCREMENT,
  `VLANID` bigint DEFAULT NULL,
  `RADIUS_ATTRIBUTE` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `REGEX` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `REGEXVALUE` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`VALIDATIONMAPPINGID`),
  UNIQUE KEY `tblmvlanvalidationmapping_validationmapping_pk` (`VALIDATIONMAPPINGID`),
  KEY `tblmvlanvalidationmapping_vlanid_fk` (`VLANID`),
  CONSTRAINT `tblmvlanvalidationmapping_vlanid_fk` FOREIGN KEY (`VLANID`) REFERENCES `tblmvlanmanagement` (`VLANID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
