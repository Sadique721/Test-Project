-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltbatchpaymentassignment
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltbatchpaymentassignment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltbatchpaymentassignment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `batch_id` bigint DEFAULT NULL,
  `staff_id` bigint DEFAULT NULL,
  `next_staff_id` bigint DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `assigneddate` timestamp NULL DEFAULT NULL,
  `assignedstatus` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltbatchpaymentassignment_id_unq` (`id`),
  KEY `tbltbatchpaymentassignment_batch_id_fk` (`batch_id`),
  KEY `tbltbatchpaymentassignment_staff_id_fk` (`staff_id`),
  KEY `tbltbatchpaymentassignment_nextstff_id_fk` (`next_staff_id`),
  CONSTRAINT `tbltbatchpaymentassignment_batch_id_fk` FOREIGN KEY (`batch_id`) REFERENCES `tblmbatchpayment` (`id`),
  CONSTRAINT `tbltbatchpaymentassignment_nextstff_id_fk` FOREIGN KEY (`next_staff_id`) REFERENCES `tblstaffuser` (`staffid`),
  CONSTRAINT `tbltbatchpaymentassignment_staff_id_fk` FOREIGN KEY (`staff_id`) REFERENCES `tblstaffuser` (`staffid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
