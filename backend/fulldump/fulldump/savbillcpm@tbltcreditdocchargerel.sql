-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltcreditdocchargerel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltcreditdocchargerel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltcreditdocchargerel` (
  `creditdocchargeid` bigint NOT NULL AUTO_INCREMENT,
  `CREDITDOCID` bigint DEFAULT NULL,
  `CHARGEID` bigint DEFAULT NULL,
  `debit_doc_id` bigint DEFAULT NULL,
  `charge_amount` double DEFAULT NULL,
  `discount` double DEFAULT NULL,
  `tax_amount` double DEFAULT NULL,
  `total_amount` double DEFAULT NULL,
  PRIMARY KEY (`creditdocchargeid`),
  UNIQUE KEY `tbltcreditdocchargerel_id_unq` (`creditdocchargeid`),
  KEY `fk_chargeid_tbltcreditdocchargerel` (`CHARGEID`),
  KEY `fk_debit_doc_id_tbltcreditdocchargerel` (`debit_doc_id`),
  KEY `fk_CREDITDOCID_tbltcreditdocchargerel` (`CREDITDOCID`),
  CONSTRAINT `fk_chargeid_tbltcreditdocchargerel` FOREIGN KEY (`CHARGEID`) REFERENCES `tblcharges` (`CHARGEID`),
  CONSTRAINT `fk_debit_doc_id_tbltcreditdocchargerel` FOREIGN KEY (`debit_doc_id`) REFERENCES `tbltdebitdocument` (`debitdocumentid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
