-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tblcustdocdetails
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblcustdocdetails`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblcustdocdetails` (
  `docId` bigint NOT NULL AUTO_INCREMENT,
  `lead_master_id` bigint DEFAULT NULL,
  `docType` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `docSubType` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mode` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `remark` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `docStatus` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `filename` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `uniquename` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `isDelete` tinyint(1) DEFAULT NULL,
  `startDate` timestamp NULL DEFAULT NULL,
  `endDate` timestamp NULL DEFAULT NULL,
  `documentNumber` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mvnoId` bigint DEFAULT NULL,
  PRIMARY KEY (`docId`),
  UNIQUE KEY `cust_doc_dtls_id_unq` (`docId`),
  KEY `tblcustdocdetails_lead_master_id_fk` (`lead_master_id`),
  CONSTRAINT `tblcustdocdetails_lead_master_id_fk` FOREIGN KEY (`lead_master_id`) REFERENCES `tblmleadmaster` (`lead_master_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
