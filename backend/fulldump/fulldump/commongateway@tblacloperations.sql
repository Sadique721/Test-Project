-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: commongateway    Table: tblacloperations
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblacloperations`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblacloperations` (
  `opid` bigint NOT NULL AUTO_INCREMENT,
  `opname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `classid` bigint NOT NULL,
  `parentoperationid` bigint DEFAULT '0',
  `is_visible` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`opid`),
  UNIQUE KEY `acloperations_opid_unq` (`opid`),
  KEY `tblacloperations_classid_fk` (`classid`),
  CONSTRAINT `tblacloperations_classid_fk` FOREIGN KEY (`classid`) REFERENCES `tblaclclass` (`classid`)
) ENGINE=InnoDB AUTO_INCREMENT=1405 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
