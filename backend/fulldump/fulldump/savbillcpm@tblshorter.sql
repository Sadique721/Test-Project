-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblshorter
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblshorter`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblshorter` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `hash` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `original_url` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `custid` bigint DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `customer_username` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `planname` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mvnoid` int DEFAULT NULL,
  `plan_duedate` date DEFAULT NULL,
  `invoiceid` int DEFAULT NULL,
  `token` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `planid` int DEFAULT NULL,
  `ishashused` tinyint(1) DEFAULT '0',
  `account_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `linktype` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tblshorter_fk_01` (`custid`),
  CONSTRAINT `tblshorter_fk_01` FOREIGN KEY (`custid`) REFERENCES `tblcustomers` (`custid`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
