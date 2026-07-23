-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillintegrationsystem    Table: tbltcreditdebitmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltcreditdebitmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltcreditdebitmapping` (
  `creddebtmappingid` bigint NOT NULL,
  `CREDITDOCID` double DEFAULT NULL,
  `debitdocumentid` double DEFAULT NULL,
  `adjustedamount` double DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `withdrawal_id` bigint DEFAULT NULL,
  PRIMARY KEY (`creddebtmappingid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
