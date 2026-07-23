-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblchildcustomer
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblchildcustomer`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblchildcustomer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_modify_by_staff_id` bigint DEFAULT NULL,
  `created_by_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_by_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_date_time` timestamp NULL DEFAULT NULL,
  `modify_date_time` timestamp NULL DEFAULT NULL,
  `create_by_staff_id` bigint DEFAULT NULL,
  `mvno_id` bigint NOT NULL,
  `parent_cust_id` bigint DEFAULT NULL,
  `wallet` double DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bu_Id` int DEFAULT NULL,
  `isdeleted` bit(1) DEFAULT NULL,
  `isparent` bit(1) DEFAULT NULL,
  `mobilenumber` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_accountnumber` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `link1` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `link2` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `link3` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `link4` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `link5` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `link6` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `link7` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `country_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active_service` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_mvno_id` (`mvno_id`),
  KEY `fk_childcustomer_parent` (`parent_cust_id`),
  KEY `fk_create_by_staff_id` (`create_by_staff_id`),
  KEY `idx_username_mvnoid` (`user_name`,`mvno_id`),
  CONSTRAINT `fk_childcustomer_parent` FOREIGN KEY (`parent_cust_id`) REFERENCES `tblcustomers` (`custid`)
) ENGINE=InnoDB AUTO_INCREMENT=54778 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
