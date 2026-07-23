-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tbltqotationdetails
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltqotationdetails`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltqotationdetails` (
  `quotation_detail_id` bigint NOT NULL AUTO_INCREMENT,
  `lead_id` bigint DEFAULT NULL,
  `version_id` bigint DEFAULT NULL,
  `template_id` bigint DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `modified_at` timestamp NULL DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT b'0',
  `validity_unit` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `validity` bigint DEFAULT NULL,
  `installation_unit` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `installation_validity` bigint DEFAULT NULL,
  `BUID` bigint DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  `next_approve_staff_id` bigint DEFAULT NULL,
  `next_team_mapping_id` bigint DEFAULT NULL,
  `final_approved` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`quotation_detail_id`),
  UNIQUE KEY `tbltqotationdetails_quotation_detail_id_unq` (`quotation_detail_id`),
  KEY `qotationdetails_fk1` (`lead_id`),
  CONSTRAINT `qotationdetails_fk1` FOREIGN KEY (`lead_id`) REFERENCES `tblmleadmaster` (`lead_master_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
