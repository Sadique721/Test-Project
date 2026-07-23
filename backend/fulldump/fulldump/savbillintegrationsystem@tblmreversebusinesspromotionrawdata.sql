-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillintegrationsystem    Table: tblmreversebusinesspromotionrawdata
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmreversebusinesspromotionrawdata`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmreversebusinesspromotionrawdata` (
  `sr_no` bigint NOT NULL AUTO_INCREMENT,
  `added_date` date DEFAULT NULL,
  `billing_start_date` datetime DEFAULT NULL,
  `billing_end_date` datetime DEFAULT NULL,
  `transaction_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `doc_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `customer_name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `customer_user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bill_gen_id` bigint DEFAULT NULL,
  `customer_account_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `customer_account_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `transaction_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `branch_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `business_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ic_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nav_ledger_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `debit_doc_id` bigint DEFAULT NULL,
  `service_area_id` bigint DEFAULT NULL,
  `is_pushed` bit(1) DEFAULT NULL,
  `serial_number_reverse_business_promotion_final` bigint DEFAULT NULL,
  PRIMARY KEY (`sr_no`),
  UNIQUE KEY `tblmreversebusinesspromotionrawdata_sr_no` (`sr_no`),
  KEY `fk_tblmreversebusinesspromotionrawdata_serial_number` (`serial_number_reverse_business_promotion_final`),
  CONSTRAINT `fk_tblmreversebusinesspromotionrawdata_serial_number` FOREIGN KEY (`serial_number_reverse_business_promotion_final`) REFERENCES `tblmreversebusinesspromotionfinaldata` (`sr_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
