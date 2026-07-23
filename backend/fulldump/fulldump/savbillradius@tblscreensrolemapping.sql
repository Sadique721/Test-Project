-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tblscreensrolemapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblscreensrolemapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblscreensrolemapping` (
  `rolescreenid` bigint NOT NULL AUTO_INCREMENT,
  `screenid` bigint DEFAULT NULL,
  `roleid` bigint DEFAULT NULL,
  `readOnly` bit(1) DEFAULT NULL,
  `createUpdateOnly` bit(1) DEFAULT NULL,
  `deleteOnly` bit(1) DEFAULT NULL,
  `mvnoid` bigint DEFAULT NULL,
  PRIMARY KEY (`rolescreenid`),
  KEY `rolescreen_screen_id_fk` (`screenid`),
  KEY `rolescreen_roleid_fk` (`roleid`),
  CONSTRAINT `rolescreen_screen_id_fk` FOREIGN KEY (`screenid`) REFERENCES `tblscreens` (`screenid`)
) ENGINE=InnoDB AUTO_INCREMENT=139 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
