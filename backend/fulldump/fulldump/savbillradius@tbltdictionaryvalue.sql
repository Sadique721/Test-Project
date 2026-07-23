-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tbltdictionaryvalue
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltdictionaryvalue`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltdictionaryvalue` (
  `dictionaryvalueid` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `value` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dictionaryattributeid` bigint DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT NULL,
  `lastmodificationdate` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`dictionaryvalueid`),
  UNIQUE KEY `dictionaryValue_name_mvno_dictionaryattributeid_unq` (`name`,`dictionaryattributeid`),
  KEY `dictionaryattribute_id_fk` (`dictionaryattributeid`),
  CONSTRAINT `dictionaryattribute_id_fk` FOREIGN KEY (`dictionaryattributeid`) REFERENCES `tbltdictionaryattribute` (`dictionaryattributeid`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
