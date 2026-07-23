-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tblmdynamicattributemapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmdynamicattributemapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmdynamicattributemapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `clientgroupid` bigint DEFAULT NULL,
  `customer_attribute` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `radius_attribute` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_absence_accepted` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblmdynamicattributemapping_id_pk_unq` (`id`),
  KEY `tblmdynamicattributemapping_clientgroupid_fk` (`clientgroupid`),
  CONSTRAINT `tblmdynamicattributemapping_clientgroupid_fk` FOREIGN KEY (`clientgroupid`) REFERENCES `tblmclientgroup` (`clientgroupid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
