-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillsalesscrms    Table: tbltrejectsubreason
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltrejectsubreason`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltrejectsubreason` (
  `reject_sub_reason_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_delete` tinyint(1) NOT NULL DEFAULT '0',
  `reject_reason_id` bigint DEFAULT NULL,
  PRIMARY KEY (`reject_sub_reason_id`),
  KEY `reject_sub_reason_id_fk` (`reject_reason_id`),
  CONSTRAINT `reject_sub_reason_id_fk` FOREIGN KEY (`reject_reason_id`) REFERENCES `tblmrejectreason` (`reject_reason_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
