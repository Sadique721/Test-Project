-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmlocationmastermapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmlocationmastermapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmlocationmastermapping` (
  `locationmappingid` bigint NOT NULL AUTO_INCREMENT,
  `mac` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `identity` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `locationid` bigint NOT NULL,
  `is_used` bit(1) DEFAULT b'0',
  `location_name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`locationmappingid`),
  KEY `fk_location_mapping_location` (`locationid`),
  CONSTRAINT `fk_location_mapping_location` FOREIGN KEY (`locationid`) REFERENCES `tblmlocationmaster` (`locationid`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
