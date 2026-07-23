-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tbltcustomerlocationmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltcustomerlocationmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltcustomerlocationmapping` (
  `customerlocationid` bigint NOT NULL,
  `customerid` bigint DEFAULT NULL,
  `locationid` bigint DEFAULT NULL,
  `locationname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT b'0',
  `is_active` bit(1) DEFAULT b'1',
  `is_parent_location` bit(1) DEFAULT b'0',
  `mac` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  PRIMARY KEY (`customerlocationid`),
  UNIQUE KEY `TBLTCUSTOMERLOCATIONMAPPING_id_uni` (`customerlocationid`),
  KEY `custid_tbltcustomerlocationmapping` (`customerid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
