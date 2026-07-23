-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblpartners
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblpartners`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblpartners` (
  `PARTNERID` bigint NOT NULL,
  `PARTNERNAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CREATEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `COMM_TYPE` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `COMM_REL_VALUE` decimal(20,4) DEFAULT NULL,
  `COMM_DUE_DAY` decimal(2,0) DEFAULT NULL,
  `NEXTBILLDATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `LASTBILLDATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `taxid` bigint DEFAULT NULL,
  `addresstype` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `city` decimal(10,0) DEFAULT NULL,
  `state` decimal(10,0) DEFAULT NULL,
  `country` bigint DEFAULT NULL,
  `pincode` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mobile` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_delete` tinyint(1) NOT NULL DEFAULT '0',
  `parentpartnerid` int DEFAULT NULL,
  `pricebookid` bigint DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  `commission_share_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `balance` decimal(10,2) DEFAULT NULL,
  `country_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `BUID` bigint DEFAULT NULL,
  `new_customer_count` bigint DEFAULT NULL,
  `renew_customer_count` bigint DEFAULT NULL,
  `total_customer_count` bigint DEFAULT NULL,
  `credit` double(20,6) DEFAULT NULL,
  `calendartype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'English',
  `reset_date` timestamp NULL DEFAULT NULL,
  `partner_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `partner_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contact_person_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `company_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pan_details` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `credit_consume` double(16,6) DEFAULT NULL,
  `region` bigint DEFAULT NULL,
  `branch` bigint DEFAULT NULL,
  `bussiness_vertical` bigint DEFAULT NULL,
  `is_dunning_enable` tinyint(1) DEFAULT '1',
  `dunning_action` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dunning_activate_for` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_dunning_date` datetime DEFAULT NULL,
  `is_visible_to_isp` bit(1) DEFAULT b'0',
  PRIMARY KEY (`PARTNERID`),
  UNIQUE KEY `partners_partnersId_unq` (`PARTNERID`),
  KEY `tblpartners_ibfk_1` (`pricebookid`),
  KEY `businessunit_partner_fk` (`BUID`),
  KEY `idx_partners_type_visible` (`partner_type`,`is_visible_to_isp`,`PARTNERID`),
  CONSTRAINT `businessunit_partner_fk` FOREIGN KEY (`BUID`) REFERENCES `tblmbusinessunit` (`businessunitid`),
  CONSTRAINT `tblpartners_ibfk_1` FOREIGN KEY (`pricebookid`) REFERENCES `tblpricebook` (`bookid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
