-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tbltcustledgerdtls
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltcustledgerdtls`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltcustledgerdtls` (
  `CUSTLEDGERDTLSID` bigint NOT NULL AUTO_INCREMENT,
  `transtype` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `transcategory` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `amount` decimal(20,4) DEFAULT NULL,
  `lead_master_id` bigint DEFAULT NULL,
  `creditdocid` bigint DEFAULT NULL,
  `debitdocid` bigint DEFAULT NULL,
  `description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`CUSTLEDGERDTLSID`),
  UNIQUE KEY `cust_ledger_dtls_id_unq` (`CUSTLEDGERDTLSID`),
  KEY `cust_ledger_dtls_lead_master_id_fk` (`lead_master_id`),
  CONSTRAINT `cust_ledger_dtls_lead_master_id_fk` FOREIGN KEY (`lead_master_id`) REFERENCES `tblmleadmaster` (`lead_master_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
