-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tbltdictionaryattribute
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltdictionaryattribute`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltdictionaryattribute` (
  `dictionaryattributeid` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `category` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `attributeid` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dictionaryid` bigint DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT NULL,
  `lastmodificationdate` timestamp NULL DEFAULT NULL,
  `hastag` bit(1) DEFAULT b'0',
  PRIMARY KEY (`dictionaryattributeid`),
  UNIQUE KEY `dictionaryAttribute_name_mvno_dictionaryid_unq` (`name`,`dictionaryid`),
  KEY `dictionary_id_fk` (`dictionaryid`),
  CONSTRAINT `dictionary_id_fk` FOREIGN KEY (`dictionaryid`) REFERENCES `tblmdictionary` (`dictionaryid`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
