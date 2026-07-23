-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillrevenuemanagement    Table: tbltbatchpaymentdetails
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltbatchpaymentdetails`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltbatchpaymentdetails` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `batch_id` bigint DEFAULT NULL,
  `staff_id` bigint DEFAULT NULL,
  `next_staff_id` bigint DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `assigneddate` timestamp NULL DEFAULT NULL,
  `assignedstatus` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_delete` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltbatchpaymentdetails_id_unq` (`id`),
  KEY `tbltbatchpaymentdetails_batch_id_fk` (`batch_id`),
  KEY `tbltbatchpaymentdetails_staff_id_fk` (`staff_id`),
  KEY `tbltbatchpaymentdetails_nextstff_id_fk` (`next_staff_id`),
  CONSTRAINT `tbltbatchpaymentdetails_batch_id_fk` FOREIGN KEY (`batch_id`) REFERENCES `tblmbatchpayment` (`id`),
  CONSTRAINT `tbltbatchpaymentdetails_nextstff_id_fk` FOREIGN KEY (`next_staff_id`) REFERENCES `tblstaffuser` (`staffid`),
  CONSTRAINT `tbltbatchpaymentdetails_staff_id_fk` FOREIGN KEY (`staff_id`) REFERENCES `tblstaffuser` (`staffid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
