-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tblvlan_profile_mapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblvlan_profile_mapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblvlan_profile_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `clientgroupid` bigint DEFAULT NULL,
  `attribute` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `coloumn` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblmunknown_profile_mapping_id_pk` (`id`),
  KEY `tblvlan_profile_mapping_clientgroupid_fk` (`clientgroupid`),
  CONSTRAINT `tblvlan_profile_mapping_clientgroupid_fk` FOREIGN KEY (`clientgroupid`) REFERENCES `tblmclientgroup` (`clientgroupid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
