-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: commongateway    Table: tblcommonlist
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblcommonlist`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblcommonlist` (
  `list_item_id` bigint NOT NULL AUTO_INCREMENT,
  `list_text` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `list_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `list_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `MVNOID` bigint DEFAULT NULL,
  `has_mandatory` bit(1) DEFAULT b'0',
  PRIMARY KEY (`list_item_id`),
  UNIQUE KEY `commonlist_list_item_id_unq` (`list_item_id`),
  KEY `commonlist_mvno_id_fk` (`MVNOID`),
  CONSTRAINT `commonlist_mvno_id_fk` FOREIGN KEY (`MVNOID`) REFERENCES `tblmmvno` (`MVNOID`),
  CONSTRAINT `tblcommonlist_ibfk_1` FOREIGN KEY (`MVNOID`) REFERENCES `tblmmvno` (`MVNOID`)
) ENGINE=InnoDB AUTO_INCREMENT=1320 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
