-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tbltemail
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltemail`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltemail` (
  `email_id` bigint NOT NULL AUTO_INCREMENT,
  `sourcename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `emailAddress` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `date` date DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `emailConfigId` bigint DEFAULT NULL,
  `createdon` date DEFAULT NULL,
  `lastmodifiedon` date DEFAULT NULL,
  `event_id` bigint DEFAULT NULL,
  `mvno_id` bigint DEFAULT NULL,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `quotation_id` bigint DEFAULT NULL,
  `lead_id` bigint DEFAULT NULL,
  `email_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`email_id`),
  UNIQUE KEY `TBLTEMAIL_emailid_unq` (`email_id`),
  KEY `TBLTEMAIL_quotation_id_fk2` (`quotation_id`),
  CONSTRAINT `TBLTEMAIL_quotation_id_fk2` FOREIGN KEY (`quotation_id`) REFERENCES `tbltqotationdetails` (`quotation_detail_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
