-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tblmpostpaidplanchargerel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmpostpaidplanchargerel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmpostpaidplanchargerel` (
  `POSTPAIDPLANCHARGERELID` bigint NOT NULL AUTO_INCREMENT,
  `CHARGEID` bigint NOT NULL,
  `BILLINGCYCLE` decimal(2,0) DEFAULT NULL,
  `CREATEDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `POSTPAIDPLANID` bigint DEFAULT NULL,
  `chargeprice` decimal(20,4) DEFAULT NULL,
  `apig_plancharge_id` bigint DEFAULT NULL,
  PRIMARY KEY (`POSTPAIDPLANCHARGERELID`),
  UNIQUE KEY `mpostpaidplanchargerel_id_unq` (`POSTPAIDPLANCHARGERELID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
