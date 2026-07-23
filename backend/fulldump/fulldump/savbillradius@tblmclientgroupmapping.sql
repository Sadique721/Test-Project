-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tblmclientgroupmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmclientgroupmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmclientgroupmapping` (
  `clientgroupentryid` bigint NOT NULL AUTO_INCREMENT,
  `clientid` bigint DEFAULT NULL,
  `checkitem` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `clientgroupid` bigint DEFAULT NULL,
  `priority` bigint DEFAULT NULL,
  PRIMARY KEY (`clientgroupentryid`),
  UNIQUE KEY `tblmclientgroupmapping_clientgroupentryid_pk` (`clientgroupentryid`),
  KEY `tblmclientgroupmapping_clientid_fk` (`clientid`),
  CONSTRAINT `tblmclientgroupmapping_clientid_fk` FOREIGN KEY (`clientid`) REFERENCES `tbltclients` (`clientid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
