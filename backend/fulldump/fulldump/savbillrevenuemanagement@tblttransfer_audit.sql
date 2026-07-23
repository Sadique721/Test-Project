-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillrevenuemanagement    Table: tblttransfer_audit
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblttransfer_audit`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblttransfer_audit` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(20,4) NOT NULL,
  `from_parent_cust_id` bigint DEFAULT NULL,
  `from_child_cust_id` bigint DEFAULT NULL,
  `to_child_cust_id` bigint DEFAULT NULL,
  `to_parent_cust_id` bigint DEFAULT NULL,
  `main_cust_id` bigint DEFAULT NULL,
  `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
