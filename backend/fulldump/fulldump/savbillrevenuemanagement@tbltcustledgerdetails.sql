-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillrevenuemanagement    Table: tbltcustledgerdetails
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltcustledgerdetails`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltcustledgerdetails` (
  `CUSTLEDGERDTLSID` bigint NOT NULL AUTO_INCREMENT,
  `CREATEDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `TRANSTYPE` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TRANSCATEGORY` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `AMOUNT` decimal(20,4) DEFAULT NULL,
  `CUSTID` bigint DEFAULT NULL,
  `CREDITDOCID` bigint DEFAULT NULL,
  `DEBITDOCID` bigint DEFAULT NULL,
  `DESCRIPTION` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_delete` tinyint(1) DEFAULT '0',
  `PAYMENTMODE` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `BANK` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `BRANCH` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `PAYMENTREFNO` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_void` tinyint(1) DEFAULT '0',
  `from_id` bigint DEFAULT NULL,
  `to_id` bigint DEFAULT NULL,
  PRIMARY KEY (`CUSTLEDGERDTLSID`),
  UNIQUE KEY `tcustledgerdetails_id_unq` (`CUSTLEDGERDTLSID`),
  KEY `index_custLedgerDertail_custid` (`CUSTID`)
) ENGINE=InnoDB AUTO_INCREMENT=56501 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
