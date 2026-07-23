-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltticketfollowupaudit
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltticketfollowupaudit`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltticketfollowupaudit` (
  `ticket_follow_up_audit_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(528) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `audit_name` varchar(528) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `staff_name` varchar(528) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `ticket_id` bigint NOT NULL,
  `created_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ticket_follow_up_audit_id`),
  UNIQUE KEY `tickwt_follow_up_audit_id_uni` (`ticket_follow_up_audit_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
