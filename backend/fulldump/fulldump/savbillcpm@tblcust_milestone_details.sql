-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblcust_milestone_details
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblcust_milestone_details`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblcust_milestone_details` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `milestone_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `due_date` date DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  `lead_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tblcust_milestone_details_customer_id_fk` (`customer_id`),
  KEY `tblcust_milestone_details_lead_id_fk` (`lead_id`),
  CONSTRAINT `tblcust_milestone_details_customer_id_fk` FOREIGN KEY (`customer_id`) REFERENCES `tblcustomers` (`custid`),
  CONSTRAINT `tblcust_milestone_details_lead_id_fk` FOREIGN KEY (`lead_id`) REFERENCES `tblmleadmaster` (`lead_master_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
