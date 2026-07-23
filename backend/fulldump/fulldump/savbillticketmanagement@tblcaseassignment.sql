-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillticketmanagement    Table: tblcaseassignment
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblcaseassignment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblcaseassignment` (
  `assignment_id` bigint NOT NULL AUTO_INCREMENT,
  `case_id` bigint DEFAULT NULL,
  `assignee_id` bigint DEFAULT NULL,
  `assigned_date` date NOT NULL,
  PRIMARY KEY (`assignment_id`),
  UNIQUE KEY `caseassignment_assignment_id_unq` (`assignment_id`),
  KEY `tblcaseassignment_case_id_fk` (`case_id`),
  KEY `tblcaseassignment_assignee_id_fk` (`assignee_id`),
  CONSTRAINT `tblcaseassignment_assignee_id_fk` FOREIGN KEY (`assignee_id`) REFERENCES `tblstaffuser` (`staffid`),
  CONSTRAINT `tblcaseassignment_case_id_fk` FOREIGN KEY (`case_id`) REFERENCES `tblcases` (`case_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
