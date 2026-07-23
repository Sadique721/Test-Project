-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblcustomercafassignment
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblcustomercafassignment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblcustomercafassignment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `custcaf_id` bigint DEFAULT NULL,
  `staff_id` bigint DEFAULT NULL,
  `next_staff_id` bigint DEFAULT NULL,
  `status` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `remark` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `assigned_date` date NOT NULL,
  `creddoc_id` bigint DEFAULT NULL,
  `plan_id` bigint DEFAULT NULL,
  `custpackage_id` bigint DEFAULT NULL,
  `new_discount` bigint DEFAULT NULL,
  `lead_id` bigint DEFAULT NULL,
  `next_approver_id` bigint DEFAULT NULL,
  `plangroupid` bigint DEFAULT NULL,
  `cust_terminate_id` bigint DEFAULT NULL,
  `ADDRESSID` bigint DEFAULT NULL,
  `custpackageid` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblcustomercafassignment_id_unq` (`id`),
  KEY `tblcustomercafassignment_custcaf_id_fk` (`custcaf_id`),
  KEY `tblcustomercafassignment_staff_id_fk` (`staff_id`),
  KEY `tblcustomercafassignment_next_staff_id_fk` (`next_staff_id`),
  CONSTRAINT `tblcustomercafassignment_custcaf_id_fk` FOREIGN KEY (`custcaf_id`) REFERENCES `tblcustomers` (`custid`),
  CONSTRAINT `tblcustomercafassignment_next_staff_id_fk` FOREIGN KEY (`next_staff_id`) REFERENCES `tblstaffuser` (`staffid`),
  CONSTRAINT `tblcustomercafassignment_staff_id_fk` FOREIGN KEY (`staff_id`) REFERENCES `tblstaffuser` (`staffid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
