-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tbltcustquotadtls
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltcustquotadtls`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltcustquotadtls` (
  `cust_quota_dtls_id` bigint NOT NULL AUTO_INCREMENT,
  `planId` bigint DEFAULT NULL,
  `quotaType` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `totalQuota` decimal(20,4) DEFAULT NULL,
  `usedQuota` decimal(20,4) DEFAULT NULL,
  `quotaUnit` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `timeTotalQuota` decimal(20,4) DEFAULT NULL,
  `timeQuotaUsed` decimal(20,4) DEFAULT NULL,
  `timeQuotaUnit` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `isDelete` tinyint(1) DEFAULT '0',
  `totalQuotaKB` decimal(20,4) DEFAULT NULL,
  `usedQuotaKB` decimal(20,4) DEFAULT NULL,
  `timeUsedQuotaSec` decimal(20,4) DEFAULT NULL,
  `timeTotalQuotaSec` decimal(20,4) DEFAULT NULL,
  `didtotalquota` decimal(20,4) DEFAULT NULL,
  `didusedquota` decimal(20,4) DEFAULT NULL,
  `intercomtotalquota` decimal(20,4) DEFAULT NULL,
  `intercomusedquota` decimal(20,4) DEFAULT NULL,
  `didQuotaUnit` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `intercomQuotaUnit` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `planName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `lead_master_id` bigint DEFAULT NULL,
  `cust_plan_mapping_id` bigint DEFAULT NULL,
  PRIMARY KEY (`cust_quota_dtls_id`),
  UNIQUE KEY `cust_quota_dtls_id_id_unq` (`cust_quota_dtls_id`),
  KEY `cust_quota_dtls_lead_master_id_fk` (`lead_master_id`),
  KEY `cust_quota_dtls_cust_plan_mapping_id_fk` (`cust_plan_mapping_id`),
  CONSTRAINT `cust_quota_dtls_cust_plan_mapping_id_fk` FOREIGN KEY (`cust_plan_mapping_id`) REFERENCES `tblcustpackagerel` (`cust_plan_mappping_id`),
  CONSTRAINT `cust_quota_dtls_lead_master_id_fk` FOREIGN KEY (`lead_master_id`) REFERENCES `tblmleadmaster` (`lead_master_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
