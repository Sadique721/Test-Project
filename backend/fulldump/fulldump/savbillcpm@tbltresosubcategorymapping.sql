-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltresosubcategorymapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltresosubcategorymapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltresosubcategorymapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `res_id` bigint DEFAULT NULL,
  `subcate_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltresosubcategorymapping_uniq` (`id`),
  KEY `tbltresosubcategorymapping_fk2` (`res_id`),
  KEY `tbltresosubcategorymapping_fk1` (`subcate_id`),
  CONSTRAINT `tbltresosubcategorymapping_fk1` FOREIGN KEY (`subcate_id`) REFERENCES `tblmticketreasonsubcategory` (`id`),
  CONSTRAINT `tbltresosubcategorymapping_fk2` FOREIGN KEY (`res_id`) REFERENCES `tblcaseresolutions` (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
