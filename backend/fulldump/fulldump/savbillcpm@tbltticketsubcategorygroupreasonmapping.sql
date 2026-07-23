-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltticketsubcategorygroupreasonmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltticketsubcategorygroupreasonmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltticketsubcategorygroupreasonmapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `reason` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ticket_reason_sub_category_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tbltticketsubcategorygroupreasonmapping_fk` (`ticket_reason_sub_category_id`),
  CONSTRAINT `tbltticketsubcategorygroupreasonmapping_fk` FOREIGN KEY (`ticket_reason_sub_category_id`) REFERENCES `tblmticketreasonsubcategory` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
