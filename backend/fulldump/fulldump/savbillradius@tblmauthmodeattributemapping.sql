-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tblmauthmodeattributemapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmauthmodeattributemapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmauthmodeattributemapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `radiusprofileid` bigint DEFAULT NULL,
  `authentication_mode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `column_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblmauthmodeattributemapping_id_pk_unq` (`id`),
  KEY `tblmauthmodeattributemapping_tblmradiusprofileid_fk` (`radiusprofileid`),
  CONSTRAINT `tblmauthmodeattributemapping_tblmradiusprofileid_fk` FOREIGN KEY (`radiusprofileid`) REFERENCES `tblmradiusprofile` (`radiusprofileid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
