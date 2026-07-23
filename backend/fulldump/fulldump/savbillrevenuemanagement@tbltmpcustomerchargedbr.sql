-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillrevenuemanagement    Table: tbltmpcustomerchargedbr
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltmpcustomerchargedbr`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltmpcustomerchargedbr` (
  `dbr_id` bigint NOT NULL AUTO_INCREMENT,
  `charge_id` bigint DEFAULT NULL,
  `custid` bigint DEFAULT NULL,
  `custname` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `planname` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `planid` bigint DEFAULT NULL,
  `validity_days` int DEFAULT NULL,
  `offer_price` decimal(16,2) DEFAULT NULL,
  `status` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `custtype` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `pending_amt` decimal(16,6) DEFAULT NULL,
  `dbr` decimal(16,6) DEFAULT NULL,
  `cumm_revenue` decimal(10,6) DEFAULT NULL,
  `is_direct_charge` bit(1) DEFAULT b'0',
  `cprid` bigint DEFAULT NULL,
  `invoiceid` bigint DEFAULT NULL,
  `service_id` bigint DEFAULT NULL,
  `remark` varchar(350) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
  `service_area` bigint DEFAULT NULL,
  `mvnoid` bigint DEFAULT NULL,
  `buid` bigint DEFAULT NULL,
  PRIMARY KEY (`dbr_id`),
  UNIQUE KEY `tbltmpcustomerchargedbr_id_unq` (`dbr_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
