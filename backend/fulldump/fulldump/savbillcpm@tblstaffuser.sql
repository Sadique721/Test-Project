-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblstaffuser
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblstaffuser`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblstaffuser` (
  `staffid` bigint NOT NULL,
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `firstname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `lastname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `failcount` int DEFAULT NULL,
  `sstatus` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_login_time` timestamp NULL DEFAULT NULL,
  `partnerid` bigint NOT NULL,
  `oldpassword1` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `oldpassword2` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `oldpassword3` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_delete` tinyint(1) NOT NULL DEFAULT '0',
  `otp` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `otpvalidate` timestamp NULL DEFAULT NULL,
  `sysstaff` tinyint(1) NOT NULL DEFAULT '0',
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` decimal(20,0) NOT NULL DEFAULT '1',
  `LASTMODIFIEDBYSTAFFID` decimal(20,0) NOT NULL DEFAULT '1',
  `createdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `service_area_id` bigint DEFAULT NULL,
  `parent_staff_id` bigint DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  `country_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `businessunitid` bigint DEFAULT NULL,
  `branchid` bigint DEFAULT NULL,
  `total_collected` decimal(20,4) DEFAULT NULL,
  `total_transferred` decimal(20,4) DEFAULT NULL,
  `available_amount` decimal(20,4) DEFAULT NULL,
  `lcoid` bigint DEFAULT NULL,
  `hrms_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `profile_image` longblob,
  `department` int DEFAULT NULL,
  `access_level_group_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mvno_deactivation_flag` bit(1) DEFAULT NULL,
  PRIMARY KEY (`staffid`),
  UNIQUE KEY `staffuser_staffid_unq` (`staffid`),
  KEY `businessunitid_fk` (`businessunitid`),
  KEY `tblstaffuser_ibfk_1` (`MVNOID`),
  KEY `parent_staff_id_fk` (`parent_staff_id`),
  KEY `service_area_id_fk` (`service_area_id`),
  KEY `tblstaffuser_branchidfk_1` (`branchid`),
  KEY `idx_staffuser_id_username` (`staffid`,`username`),
  CONSTRAINT `parent_staff_id_fk` FOREIGN KEY (`parent_staff_id`) REFERENCES `tblstaffuser` (`staffid`),
  CONSTRAINT `service_area_id_fk` FOREIGN KEY (`service_area_id`) REFERENCES `tblservicearea` (`service_area_id`),
  CONSTRAINT `tblstaffuser_branchidfk_1` FOREIGN KEY (`branchid`) REFERENCES `tblmbranch` (`branchid`),
  CONSTRAINT `tblstaffuser_ibfk_1` FOREIGN KEY (`MVNOID`) REFERENCES `tblmmvno` (`MVNOID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
