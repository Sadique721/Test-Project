-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltleadcustquotadtls
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltleadcustquotadtls`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltleadcustquotadtls` (
  `cust_quota_dtls_id` bigint NOT NULL,
  `plan_id` bigint DEFAULT NULL,
  `quota_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `total_quota` decimal(20,4) DEFAULT NULL,
  `used_quota` decimal(20,4) DEFAULT NULL,
  `quota_unit` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `time_total_quota` decimal(20,4) DEFAULT NULL,
  `time_quota_used` decimal(20,4) DEFAULT NULL,
  `time_quota_unit` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_delete` tinyint(1) DEFAULT '0',
  `total_quota_kb` decimal(20,4) DEFAULT NULL,
  `used_quota_kb` decimal(20,4) DEFAULT NULL,
  `time_used_quota_sec` decimal(20,4) DEFAULT NULL,
  `time_total_quota_sec` decimal(20,4) DEFAULT NULL,
  `didtotalquota` decimal(20,4) DEFAULT NULL,
  `didusedquota` decimal(20,4) DEFAULT NULL,
  `intercomtotalquota` decimal(20,4) DEFAULT NULL,
  `intercomusedquota` decimal(20,4) DEFAULT NULL,
  `did_quota_unit` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `intercom_quota_unit` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `plan_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `lead_master_id` bigint DEFAULT NULL,
  `cust_plan_mapping_id` bigint DEFAULT NULL,
  PRIMARY KEY (`cust_quota_dtls_id`),
  UNIQUE KEY `cust_quota_dtls_id_id_unq` (`cust_quota_dtls_id`),
  KEY `lead_cust_quota_dtls_lead_master_id_fk` (`lead_master_id`),
  CONSTRAINT `lead_cust_quota_dtls_lead_master_id_fk` FOREIGN KEY (`lead_master_id`) REFERENCES `tblmleadmaster` (`lead_master_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
