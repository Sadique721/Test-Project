-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmbuildingmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmbuildingmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmbuildingmapping` (
  `id` bigint NOT NULL,
  `building_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `building_mgmt_id` bigint NOT NULL,
  `is_deleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_buildingmapping_mgmtid_deleted` (`building_mgmt_id`,`is_deleted`),
  KEY `idx_t9_covering` (`building_mgmt_id`,`is_deleted`,`building_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
