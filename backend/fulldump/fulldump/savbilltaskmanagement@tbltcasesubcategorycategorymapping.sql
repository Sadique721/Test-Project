-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbilltaskmanagement    Table: tbltcasesubcategorycategorymapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltcasesubcategorycategorymapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltcasesubcategorycategorymapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `case_category_id` bigint DEFAULT NULL,
  `case_sub_category_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `casesubcategorycategorymapping_casecategoryid_fk` (`case_category_id`),
  KEY `casesubcategorycategorymapping_casesubcategoryid_fk` (`case_sub_category_id`),
  CONSTRAINT `casesubcategorycategorymapping_casecategoryid_fk` FOREIGN KEY (`case_category_id`) REFERENCES `tblmcasecategory` (`category_id`),
  CONSTRAINT `casesubcategorycategorymapping_casesubcategoryid_fk` FOREIGN KEY (`case_sub_category_id`) REFERENCES `tblmcasesubcategory` (`sub_category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
