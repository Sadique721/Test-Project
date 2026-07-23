-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tblstaffrolerel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblstaffrolerel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblstaffrolerel` (
  `staffrolerelid` bigint NOT NULL AUTO_INCREMENT,
  `staffid` int NOT NULL,
  `roleid` int NOT NULL,
  PRIMARY KEY (`staffrolerelid`),
  UNIQUE KEY `staffrolerel_staffrolerelid_unq` (`staffrolerelid`)
) ENGINE=InnoDB AUTO_INCREMENT=1084 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
