-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillintegrationsystem    Table: tblmpaymentgenrawdata
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmpaymentgenrawdata`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmpaymentgenrawdata` (
  `sr_no` bigint NOT NULL AUTO_INCREMENT,
  `paymentdate` date DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `branch_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `business_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ic_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nav_ledger_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_pushed` bit(1) DEFAULT NULL,
  `service_area_id` bigint DEFAULT NULL,
  `document_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `other_details` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `payment_mode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `payment_source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `serial_number_payment_gen_final` bigint DEFAULT NULL,
  `olt` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pop` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`sr_no`),
  UNIQUE KEY `tblmpaymentgenrawdata_sr_no` (`sr_no`),
  KEY `fk_tblmpaymentgenrawdata_serial_number_payment_gen_final_id` (`serial_number_payment_gen_final`),
  CONSTRAINT `fk_tblmpaymentgenrawdata_serial_number_payment_gen_final_id` FOREIGN KEY (`serial_number_payment_gen_final`) REFERENCES `tblmpaymentgenfinaldata` (`sr_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
