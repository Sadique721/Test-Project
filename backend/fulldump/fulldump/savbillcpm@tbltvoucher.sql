-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltvoucher
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltvoucher`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltvoucher` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `BATCH_NAME` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CODE` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `VOUCHER_CONFIG_ID` bigint DEFAULT NULL,
  `STATUS` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdby` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `lastmodifiedby` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT NULL,
  `lastmodificationdate` timestamp NULL DEFAULT NULL,
  `mvnoid` bigint DEFAULT NULL,
  `voucher_batch_id` bigint DEFAULT NULL,
  `buid` bigint DEFAULT NULL,
  `voucher_used_date` datetime DEFAULT NULL,
  `createdbystaffid` bigint DEFAULT NULL,
  `serial_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `voucher_batch_id_fk` (`voucher_batch_id`),
  CONSTRAINT `voucher_batch_id_fk` FOREIGN KEY (`voucher_batch_id`) REFERENCES `tblmvoucherbatch` (`voucherbatchid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
