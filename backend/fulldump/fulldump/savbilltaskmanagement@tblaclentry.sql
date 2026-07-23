-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbilltaskmanagement    Table: tblaclentry
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblaclentry`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblaclentry` (
  `aclid` bigint NOT NULL,
  `classid` bigint DEFAULT NULL,
  `roleid` bigint DEFAULT NULL,
  `permit` int DEFAULT NULL,
  PRIMARY KEY (`aclid`),
  UNIQUE KEY `aclentry_aclid_unq` (`aclid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
