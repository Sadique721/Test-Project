-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillticketmanagement    Table: tbltticketsubcategoryreasoncategorymapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltticketsubcategoryreasoncategorymapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltticketsubcategoryreasoncategorymapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ticket_reason_category_id` bigint DEFAULT NULL,
  `ticket_reason_sub_category_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `subcategoryreasoncategorymapping_ticketreasoncategoryid_fk` (`ticket_reason_category_id`),
  KEY `subcategoryreasoncategorymapping_ticketreasonsubcategoryid_fk` (`ticket_reason_sub_category_id`),
  CONSTRAINT `subcategoryreasoncategorymapping_ticketreasoncategoryid_fk` FOREIGN KEY (`ticket_reason_category_id`) REFERENCES `tblmticketreasoncategory` (`id`),
  CONSTRAINT `subcategoryreasoncategorymapping_ticketreasonsubcategoryid_fk` FOREIGN KEY (`ticket_reason_sub_category_id`) REFERENCES `tblmticketreasonsubcategory` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
