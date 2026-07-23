-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblvoucherbatch
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblvoucherbatch`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblvoucherbatch` (
  `vbid` bigint NOT NULL AUTO_INCREMENT,
  `vouchercode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `vcid` int NOT NULL,
  `planid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `validity` timestamp NULL DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  PRIMARY KEY (`vbid`),
  UNIQUE KEY `voucherbatch_vbid_unq` (`vbid`),
  KEY `tblvoucherbatch_ibfk_1` (`MVNOID`),
  CONSTRAINT `tblvoucherbatch_ibfk_1` FOREIGN KEY (`MVNOID`) REFERENCES `tblmmvno` (`MVNOID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
