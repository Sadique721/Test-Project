-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltticketresolutionmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltticketresolutionmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltticketresolutionmapping` (
  `mapping_id` bigint NOT NULL AUTO_INCREMENT,
  `resolution_reason_id` bigint DEFAULT NULL,
  `resolution_desc` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `case_id` bigint DEFAULT NULL,
  PRIMARY KEY (`mapping_id`),
  KEY `tbltticketresolutionmapping_res_id_fk` (`resolution_reason_id`),
  KEY `tbltticketresolutionmapping_case_id_fk` (`case_id`),
  CONSTRAINT `tbltticketresolutionmapping_case_id_fk` FOREIGN KEY (`case_id`) REFERENCES `tblcases` (`case_id`),
  CONSTRAINT `tbltticketresolutionmapping_res_id_fk` FOREIGN KEY (`resolution_reason_id`) REFERENCES `tblcaseresolutions` (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
