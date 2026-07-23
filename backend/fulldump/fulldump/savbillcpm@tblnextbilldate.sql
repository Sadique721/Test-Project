-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblnextbilldate
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblnextbilldate`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblnextbilldate` (
  `SUBSCRIBERID` bigint NOT NULL AUTO_INCREMENT,
  `NEXTBILLDATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `LASTBILLDATE` timestamp NULL DEFAULT NULL,
  `BILLDAY` decimal(20,0) DEFAULT NULL,
  UNIQUE KEY `nextbilldate_subscriberid_unq` (`SUBSCRIBERID`),
  CONSTRAINT `tblnextbilldate_ibfk_1` FOREIGN KEY (`SUBSCRIBERID`) REFERENCES `tblcustomers` (`custid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
