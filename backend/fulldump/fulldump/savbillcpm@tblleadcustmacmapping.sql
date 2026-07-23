-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblleadcustmacmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblleadcustmacmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblleadcustmacmapping` (
  `custmacmapid` bigint NOT NULL,
  `mac_address` varchar(542) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT NULL,
  `lead_master_id` bigint DEFAULT NULL,
  PRIMARY KEY (`custmacmapid`),
  UNIQUE KEY `cust_mac_id_unq` (`custmacmapid`),
  KEY `tblleadcustmacmapping_lead_master_id_fk` (`lead_master_id`),
  CONSTRAINT `tblleadcustmacmapping_lead_master_id_fk` FOREIGN KEY (`lead_master_id`) REFERENCES `tblmleadmaster` (`lead_master_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
