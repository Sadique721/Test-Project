-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltpartnerinvoicemapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltpartnerinvoicemapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltpartnerinvoicemapping` (
  `mapping_id` bigint NOT NULL AUTO_INCREMENT,
  `partner_ledger_id` bigint DEFAULT NULL,
  `debitdoc_id` bigint DEFAULT NULL,
  `cpr_id` bigint DEFAULT NULL,
  `start_date` timestamp NULL DEFAULT NULL,
  `end_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`mapping_id`),
  UNIQUE KEY `tbltpartnerinvoicemapping_mapping_id_unq` (`mapping_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
