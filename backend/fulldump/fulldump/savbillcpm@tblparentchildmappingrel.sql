-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblparentchildmappingrel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblparentchildmappingrel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblparentchildmappingrel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `child_username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_by_staff_id` bigint DEFAULT NULL,
  `mvno_id` bigint NOT NULL,
  `parent_cust_id` bigint DEFAULT NULL,
  `child_cust_id` bigint DEFAULT NULL,
  `isparent` bit(1) DEFAULT NULL,
  `parent_firstname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_lastname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `partner_id` bigint DEFAULT NULL,
  `parent_accountnumber` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `child_firstname` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `child_lastname` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `child_email` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `child_mobile` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_delete` bit(1) DEFAULT b'0',
  `child_password` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_parent_wallet_usable` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `fk_childcustomer` (`child_cust_id`),
  KEY `fk_staff_id` (`create_by_staff_id`),
  KEY `fk_child_parent` (`parent_cust_id`),
  KEY `fk_mvno_ids` (`mvno_id`),
  KEY `idx_childusername_mvno_flags` (`child_username`,`mvno_id`,`isparent`,`is_delete`),
  CONSTRAINT `fk_child_parent` FOREIGN KEY (`parent_cust_id`) REFERENCES `tblcustomers` (`custid`),
  CONSTRAINT `fk_childcustomer` FOREIGN KEY (`child_cust_id`) REFERENCES `tblchildcustomer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=56623 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
