-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tblmsuspendedprofilemapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmsuspendedprofilemapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmsuspendedprofilemapping` (
  `attributeid` bigint NOT NULL AUTO_INCREMENT,
  `clientgroupid` bigint DEFAULT NULL,
  `attribute` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `attributevalue` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `checkitem` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`attributeid`),
  UNIQUE KEY `tblmsuspendedprofilemapping_attributeid_pk` (`attributeid`),
  KEY `tblmsuspendedprofilemapping_clientgroupid_fk` (`clientgroupid`),
  CONSTRAINT `tblmsuspendedprofilemapping_clientgroupid_fk` FOREIGN KEY (`clientgroupid`) REFERENCES `tblmclientgroup` (`clientgroupid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
