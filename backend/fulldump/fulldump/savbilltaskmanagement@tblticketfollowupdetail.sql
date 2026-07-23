-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbilltaskmanagement    Table: tblticketfollowupdetail
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblticketfollowupdetail`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblticketfollowupdetail` (
  `ticketfollowid` bigint NOT NULL AUTO_INCREMENT,
  `remark` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `is_delete` tinyint(1) DEFAULT '0',
  `remark_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `case_id` bigint DEFAULT NULL,
  `staffid` bigint DEFAULT NULL,
  `custid` bigint DEFAULT NULL,
  `remark_type` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_from_customer` bit(1) DEFAULT NULL,
  PRIMARY KEY (`ticketfollowid`),
  UNIQUE KEY `tblticketfollowupdetail_ticketfollowid_unq` (`ticketfollowid`),
  KEY `tblticketfollowupdetail_ibfk_1` (`case_id`),
  KEY `tblticketfollowupdetail_ibfk_2` (`staffid`),
  KEY `tblticketfollowupdetail_ibfk_3` (`custid`),
  CONSTRAINT `tblticketfollowupdetail_ibfk_1` FOREIGN KEY (`case_id`) REFERENCES `tblcases` (`case_id`),
  CONSTRAINT `tblticketfollowupdetail_ibfk_2` FOREIGN KEY (`staffid`) REFERENCES `tblstaffuser` (`staffid`),
  CONSTRAINT `tblticketfollowupdetail_ibfk_3` FOREIGN KEY (`custid`) REFERENCES `tblcustomers` (`custid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
