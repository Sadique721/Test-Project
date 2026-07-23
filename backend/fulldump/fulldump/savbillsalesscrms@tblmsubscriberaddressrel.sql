-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tblmsubscriberaddressrel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmsubscriberaddressrel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmsubscriberaddressrel` (
  `ADDRESSID` bigint NOT NULL AUTO_INCREMENT,
  `addressType` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address1` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address2` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `landmark` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `areaId` bigint DEFAULT NULL,
  `pincodeId` bigint DEFAULT NULL,
  `cityId` bigint DEFAULT NULL,
  `stateId` bigint DEFAULT NULL,
  `countryId` bigint DEFAULT NULL,
  `fullAddress` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `customerId` bigint DEFAULT NULL,
  `lead_master_id` bigint DEFAULT NULL,
  `isDelete` tinyint(1) DEFAULT '0',
  `street_name` varchar(528) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `house_no` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ADDRESSID`),
  UNIQUE KEY `subscriber_address_id_unq` (`ADDRESSID`),
  KEY `TBLMSUBSCRIBERADDRESSREL_lead_master_id_fk` (`lead_master_id`),
  CONSTRAINT `TBLMSUBSCRIBERADDRESSREL_lead_master_id_fk` FOREIGN KEY (`lead_master_id`) REFERENCES `tblmleadmaster` (`lead_master_id`)
) ENGINE=InnoDB AUTO_INCREMENT=235 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
