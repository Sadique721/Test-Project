-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillnotification    Table: tblmstaff
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmstaff`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmstaff` (
  `staffid` bigint NOT NULL AUTO_INCREMENT,
  `mvnoid` bigint DEFAULT NULL,
  `username` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `roleid` bigint DEFAULT NULL,
  PRIMARY KEY (`staffid`),
  UNIQUE KEY `users_username_unq` (`username`),
  KEY `staff_role_id_fk` (`roleid`),
  CONSTRAINT `staff_role_id_fk` FOREIGN KEY (`roleid`) REFERENCES `tblmrole` (`roleid`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
