-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tblcharges
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblcharges`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblcharges` (
  `CHARGEID` bigint NOT NULL AUTO_INCREMENT,
  `CHARGENAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CHARGETYPE` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `PRICE` decimal(16,4) DEFAULT NULL,
  `TAXID` bigint DEFAULT NULL,
  `actual_price` decimal(20,4) DEFAULT NULL,
  `is_delete` tinyint(1) NOT NULL DEFAULT '0',
  `MVNOID` bigint DEFAULT NULL,
  `BUID` bigint DEFAULT NULL,
  `chargecategory` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `apig_charge_id` bigint DEFAULT NULL,
  `saccode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `LEDGER_ID` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `service_id` bigint DEFAULT NULL,
  PRIMARY KEY (`CHARGEID`),
  UNIQUE KEY `charges_chargeId_unq` (`CHARGEID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
