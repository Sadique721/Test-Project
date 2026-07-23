-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbl_partner_customer_rel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbl_partner_customer_rel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbl_partner_customer_rel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `custid` bigint NOT NULL,
  `partnerid` bigint NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodified_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `partner_customer_rel_id_unq` (`id`),
  KEY `tbl_partner_customer_rel_custid_fk` (`custid`),
  KEY `tbl_partner_customer_rel_partnerid_fk` (`partnerid`),
  CONSTRAINT `tbl_partner_customer_rel_custid_fk` FOREIGN KEY (`custid`) REFERENCES `tblcustomers` (`custid`),
  CONSTRAINT `tbl_partner_customer_rel_partnerid_fk` FOREIGN KEY (`partnerid`) REFERENCES `tblpartners` (`PARTNERID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
