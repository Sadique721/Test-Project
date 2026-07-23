-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillpartnermanagement    Table: tblmpartnerledgerdetails
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmpartnerledgerdetails`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmpartnerledgerdetails` (
  `partnerledgerdtls_id` bigint NOT NULL,
  `CREATEDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `transtype` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `transcategory` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `commission` decimal(20,4) DEFAULT '0.0000',
  `partner_id` bigint DEFAULT NULL,
  `description` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `custid` bigint DEFAULT NULL,
  `royalty_base_price` decimal(20,6) DEFAULT NULL,
  `agr_amount` decimal(10,2) DEFAULT NULL,
  `tds_amount` decimal(10,2) DEFAULT NULL,
  `tax` decimal(10,2) DEFAULT NULL,
  `amount` decimal(10,2) DEFAULT NULL,
  `debit_doc_id` bigint DEFAULT NULL,
  `royalty` double(10,6) DEFAULT '0.000000',
  `partner_tax` double(10,6) DEFAULT '0.000000',
  `gross_offer_price` double(16,6) DEFAULT '0.000000',
  `offerprice` double(10,2) DEFAULT '0.00',
  `planid` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`partnerledgerdtls_id`),
  UNIQUE KEY `mpartnerledgerdetails_id_unq` (`partnerledgerdtls_id`),
  KEY `tblmpartnerledgerdetails_ibfk_1` (`partner_id`),
  CONSTRAINT `tblmpartnerledgerdetails_ibfk_1` FOREIGN KEY (`partner_id`) REFERENCES `tblpartners` (`PARTNERID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
