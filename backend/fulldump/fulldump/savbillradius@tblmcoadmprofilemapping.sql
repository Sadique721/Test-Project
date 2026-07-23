-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tblmcoadmprofilemapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmcoadmprofilemapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmcoadmprofilemapping` (
  `coadm_clientgroup_mapping_id` bigint NOT NULL AUTO_INCREMENT,
  `check_item` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `clientgroupid` bigint DEFAULT NULL,
  `coa_profile_id` bigint DEFAULT NULL,
  `dm_profile_id` bigint DEFAULT NULL,
  `priority` bigint DEFAULT '999',
  `coadmselection` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`coadm_clientgroup_mapping_id`),
  UNIQUE KEY `tblmcoadmprofilemapping_coadm_clientgroup_mapping_id_unq` (`coadm_clientgroup_mapping_id`),
  KEY `tblmcoadmprofilemapping_clientgroupid_fk` (`clientgroupid`),
  CONSTRAINT `tblmcoadmprofilemapping_clientgroupid_fk` FOREIGN KEY (`clientgroupid`) REFERENCES `tblmclientgroup` (`clientgroupid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
