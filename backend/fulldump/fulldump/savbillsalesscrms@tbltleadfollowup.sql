-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tbltleadfollowup
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltleadfollowup`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltleadfollowup` (
  `lead_follow_up_id` bigint NOT NULL AUTO_INCREMENT,
  `follow_up_name` varchar(528) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `follow_up_datetime` timestamp NOT NULL,
  `remarks` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_missed` tinyint(1) DEFAULT '0',
  `lead_master_id` bigint DEFAULT NULL,
  `assignee_id` bigint DEFAULT NULL,
  `created_by` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_send` tinyint(1) DEFAULT '0',
  `send_reminder_notification` tinyint(1) DEFAULT '0',
  `is_no_followup_action` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`lead_follow_up_id`),
  UNIQUE KEY `tblt_lead_follow_up_id_uni` (`lead_follow_up_id`),
  KEY `tblt_lead_follow_up_lead_master_id_fk` (`lead_master_id`),
  KEY `tblt_lead_follow_up_assignee_id_fk` (`assignee_id`),
  CONSTRAINT `tblt_lead_follow_up_assignee_id_fk` FOREIGN KEY (`assignee_id`) REFERENCES `tblstaffuser` (`staffid`),
  CONSTRAINT `tblt_lead_follow_up_lead_master_id_fk` FOREIGN KEY (`lead_master_id`) REFERENCES `tblmleadmaster` (`lead_master_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
