-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tbltquotationcircuitmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltquotationcircuitmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltquotationcircuitmapping` (
  `quotation_circuit_mapping_id` bigint NOT NULL AUTO_INCREMENT,
  `quotation_details_id` bigint DEFAULT NULL,
  `lead_service_mapping_id` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `modified_at` timestamp NULL DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT b'0',
  `offer_price` decimal(20,4) DEFAULT NULL,
  `tax_amount` decimal(20,4) DEFAULT NULL,
  PRIMARY KEY (`quotation_circuit_mapping_id`),
  UNIQUE KEY `tbltquotationcircuitmapping_quotation_circuit_mapping_id_unq` (`quotation_circuit_mapping_id`),
  KEY `quotationcircuitmapping_fk1` (`quotation_details_id`),
  KEY `quotationcircuitmapping_fk2` (`lead_service_mapping_id`),
  CONSTRAINT `quotationcircuitmapping_fk1` FOREIGN KEY (`quotation_details_id`) REFERENCES `tbltqotationdetails` (`quotation_detail_id`),
  CONSTRAINT `quotationcircuitmapping_fk2` FOREIGN KEY (`lead_service_mapping_id`) REFERENCES `tbltleadservicemapping` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
