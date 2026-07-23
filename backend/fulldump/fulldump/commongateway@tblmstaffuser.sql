-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: commongateway    Table: tblmstaffuser
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmstaffuser`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmstaffuser` (
  `staffid` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `firstname` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `lastname` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sstatus` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_login_time` timestamp NULL DEFAULT NULL,
  `partnerid` bigint DEFAULT NULL,
  `is_delete` tinyint(1) DEFAULT '0',
  `MVNOID` bigint DEFAULT NULL,
  `branchid` bigint DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdbystaffid` bigint DEFAULT NULL,
  `lastmodifiedbystaffid` bigint DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `service_area_id` bigint DEFAULT NULL,
  `parent_staff_id` bigint DEFAULT NULL,
  `country_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `total_collected` decimal(20,4) DEFAULT NULL,
  `total_transferred` decimal(20,4) DEFAULT NULL,
  `available_amount` decimal(20,4) DEFAULT NULL,
  `lcoid` bigint DEFAULT NULL,
  `hrms_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `profile_image` longblob,
  `department` int DEFAULT NULL,
  `oldpassword1` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `oldpassword2` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `oldpassword3` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `otp` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `otpvalidate` timestamp NULL DEFAULT NULL,
  `sysstaff` tinyint(1) NOT NULL DEFAULT '0',
  `businessunitid` bigint DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `failcount` bigint DEFAULT NULL,
  `access_level_group_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mvno_deactivation_flag` bit(1) DEFAULT NULL,
  `uuid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_password_expired` tinyint(1) DEFAULT '0',
  `password_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`staffid`),
  UNIQUE KEY `tblmstaffuser_staffid_unq` (`staffid`),
  KEY `service_area_id_fk` (`service_area_id`),
  KEY `parent_staff_id_fk` (`parent_staff_id`),
  KEY `tblstaffuser_ibfk_1` (`MVNOID`),
  KEY `tblstaffuser_branchidfk_1` (`branchid`),
  KEY `businessunitid_fk` (`businessunitid`),
  KEY `idx_staffuser_status` (`is_delete`,`sstatus`),
  CONSTRAINT `businessunitid_fk` FOREIGN KEY (`businessunitid`) REFERENCES `tblmbusinessunit` (`businessunitid`),
  CONSTRAINT `parent_staff_id_fk` FOREIGN KEY (`parent_staff_id`) REFERENCES `tblmstaffuser` (`staffid`),
  CONSTRAINT `service_area_id_fk` FOREIGN KEY (`service_area_id`) REFERENCES `tblmservicearea` (`service_area_id`),
  CONSTRAINT `tblstaffuser_branchidfk_1` FOREIGN KEY (`branchid`) REFERENCES `tblmbranch` (`branchid`),
  CONSTRAINT `tblstaffuser_ibfk_1` FOREIGN KEY (`MVNOID`) REFERENCES `tblmmvno` (`MVNOID`)
) ENGINE=InnoDB AUTO_INCREMENT=1098 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
