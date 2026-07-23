-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbldunruleaction
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbldunruleaction`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbldunruleaction` (
  `actionid` bigint NOT NULL AUTO_INCREMENT,
  `druleid` bigint NOT NULL,
  `days` decimal(4,0) DEFAULT NULL,
  `daction` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`actionid`),
  UNIQUE KEY `dunruleaction_actionid_unq` (`actionid`),
  KEY `tbldunruleaction_ibfk_1` (`druleid`),
  CONSTRAINT `tbldunruleaction_ibfk_1` FOREIGN KEY (`druleid`) REFERENCES `tbldunningrules` (`druleid`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
