-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblnotifcation_config
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblnotifcation_config`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblnotifcation_config` (
  `noti_config_id` bigint NOT NULL AUTO_INCREMENT,
  `notification_id` bigint DEFAULT NULL,
  `config_entity` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `config_attribute` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `config_atrr_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `atrr_condi` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `attr_value` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`noti_config_id`),
  UNIQUE KEY `notifcation_config_Id_unq` (`noti_config_id`),
  KEY `tblnotifcation_config_FK` (`notification_id`),
  CONSTRAINT `tblnotifcation_config_FK` FOREIGN KEY (`notification_id`) REFERENCES `tblnotifications` (`notification_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
