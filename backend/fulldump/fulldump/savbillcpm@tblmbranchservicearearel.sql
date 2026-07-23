-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmbranchservicearearel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmbranchservicearearel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmbranchservicearearel` (
  `branchid` bigint NOT NULL,
  `servicearea_id` bigint NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodified_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `CREATEDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CREATEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblmbranchservicearearel_uni` (`id`),
  KEY `tblmbranchservicearearel_change` (`servicearea_id`),
  KEY `tblmbranchservicearearel_unique` (`branchid`),
  CONSTRAINT `tblmbranchservicearearel_change` FOREIGN KEY (`servicearea_id`) REFERENCES `tblservicearea` (`service_area_id`),
  CONSTRAINT `tblmbranchservicearearel_unique` FOREIGN KEY (`branchid`) REFERENCES `tblmbranch` (`branchid`)
) ENGINE=InnoDB AUTO_INCREMENT=822702 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
