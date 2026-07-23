-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmleadsubscriberaddressrel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmleadsubscriberaddressrel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmleadsubscriberaddressrel` (
  `ADDRESSID` bigint NOT NULL,
  `address_type` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address1` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address2` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `landmark` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `area_id` bigint DEFAULT NULL,
  `pincode_id` bigint DEFAULT NULL,
  `city_id` bigint DEFAULT NULL,
  `state_id` bigint DEFAULT NULL,
  `country_id` bigint DEFAULT NULL,
  `full_address` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  `lead_master_id` bigint DEFAULT NULL,
  `is_delete` tinyint(1) DEFAULT '0',
  `street_name` varchar(528) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `house_no` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ADDRESSID`),
  UNIQUE KEY `subscriber_address_id_unq` (`ADDRESSID`),
  KEY `TBLMLEADSUBSCRIBERADDRESSREL_lead_master_id_fk` (`lead_master_id`),
  CONSTRAINT `TBLMLEADSUBSCRIBERADDRESSREL_lead_master_id_fk` FOREIGN KEY (`lead_master_id`) REFERENCES `tblmleadmaster` (`lead_master_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
