-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillrevenuemanagement    Table: tblmbatchpayment
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmbatchpayment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmbatchpayment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `batchname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdby` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `BUID` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblmbatchpayment_batchid_unq` (`id`),
  KEY `businessunit_batchpayment_fk` (`BUID`),
  CONSTRAINT `businessunit_batchpayment_fk` FOREIGN KEY (`BUID`) REFERENCES `tblmbusinessunit` (`businessunitid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
