-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillrevenuemanagement    Table: tbltplanqosmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltplanqosmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltplanqosmapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `planid` bigint DEFAULT NULL,
  `qosid` bigint DEFAULT NULL,
  `from_percentage` double DEFAULT NULL,
  `to_percentage` double DEFAULT NULL,
  `isdelete` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltplanqosmapping_field_unq` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
