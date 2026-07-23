-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillintegrationsystem    Table: tblmapiauditdetails
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmapiauditdetails`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmapiauditdetails` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `time_stamp` timestamp NOT NULL,
  `api_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `header_details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `http_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `request_payload` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `response_payload` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `http_status_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `response_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `auth_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `ip_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `error_message` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `rate_limit_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `environment_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dependencies` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `custom_meta_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `mvno_id` bigint NOT NULL,
  `CREATEDATE` timestamp NULL DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT NULL,
  `createbyname` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` int DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` int DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `username_for_audit` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reference_number` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
