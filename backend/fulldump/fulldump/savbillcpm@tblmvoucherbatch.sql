-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmvoucherbatch
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmvoucherbatch`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmvoucherbatch` (
  `voucherbatchid` bigint NOT NULL AUTO_INCREMENT,
  `batchname` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `planid` bigint DEFAULT NULL,
  `resellerid` bigint DEFAULT NULL,
  `createdate` timestamp NULL DEFAULT NULL,
  `voucherquantity` bigint DEFAULT NULL,
  `price` decimal(20,8) DEFAULT NULL,
  `mvnoid` bigint DEFAULT NULL,
  `voucher_profile_id` bigint DEFAULT NULL,
  `voucher_batch_id` bigint DEFAULT NULL,
  `expirydate` datetime DEFAULT NULL,
  `buid` bigint DEFAULT NULL,
  `createdbystaffid` bigint DEFAULT NULL,
  PRIMARY KEY (`voucherbatchid`),
  KEY `voucher_profile_id_fk` (`voucher_profile_id`),
  KEY `voucher_plan_id_fk` (`planid`),
  CONSTRAINT `voucher_plan_id_fk` FOREIGN KEY (`planid`) REFERENCES `tblmpostpaidplan` (`POSTPAIDPLANID`),
  CONSTRAINT `voucher_profile_id_fk` FOREIGN KEY (`voucher_profile_id`) REFERENCES `tblmvoucherprofiles` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
