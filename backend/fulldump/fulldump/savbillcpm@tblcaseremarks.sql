-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblcaseremarks
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblcaseremarks`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblcaseremarks` (
  `remark_id` bigint NOT NULL AUTO_INCREMENT,
  `case_id` bigint DEFAULT NULL,
  `remarks_logged_by_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `remark_date_time` datetime NOT NULL,
  `attachment` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_delete` tinyint(1) NOT NULL,
  `CREATEDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `LASTMODIFIEDDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CREATEDBYSTAFFID` bigint NOT NULL,
  `LASTMODIFIEDBYSTAFFID` bigint NOT NULL,
  PRIMARY KEY (`remark_id`),
  UNIQUE KEY `caseremarks_remark_id_unq` (`remark_id`),
  KEY `tblcaseremarks_case_id_fk` (`case_id`),
  CONSTRAINT `tblcaseremarks_case_id_fk` FOREIGN KEY (`case_id`) REFERENCES `tblcases` (`case_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
