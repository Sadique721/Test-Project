-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbldunninghistory
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbldunninghistory`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbldunninghistory` (
  `dunninghistoryid` bigint NOT NULL AUTO_INCREMENT,
  `event_name` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `action` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `staffid` bigint DEFAULT NULL,
  `custid` bigint DEFAULT NULL,
  `partnerid` bigint DEFAULT NULL,
  `dunning_message_date` datetime DEFAULT NULL,
  `dunning_message` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mvnoid` bigint DEFAULT NULL,
  PRIMARY KEY (`dunninghistoryid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
