-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltbatchpaymentmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltbatchpaymentmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltbatchpaymentmapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `batch_id` bigint DEFAULT NULL,
  `credit_doc_id` bigint DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltbatchpaymentmapping_id_unq` (`id`),
  KEY `batchpayment_batch_id_fk` (`batch_id`),
  KEY `batchpaymentmapping_creditdoc_id_fk` (`credit_doc_id`),
  CONSTRAINT `batchpayment_batch_id_fk` FOREIGN KEY (`batch_id`) REFERENCES `tblmbatchpayment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
