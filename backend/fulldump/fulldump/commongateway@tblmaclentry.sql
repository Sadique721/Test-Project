-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: commongateway    Table: tblmaclentry
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmaclentry`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmaclentry` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `roleid` bigint DEFAULT NULL,
  `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `menuid` bigint DEFAULT NULL,
  `product` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'BSS',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tblmaclentry_id_unq` (`id`),
  KEY `aclentry_role_code_index` (`roleid`,`code`)
) ENGINE=InnoDB AUTO_INCREMENT=38856 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
