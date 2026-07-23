-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblplanqosdtls
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblplanqosdtls`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblplanqosdtls` (
  `planqosid` bigint NOT NULL AUTO_INCREMENT,
  `planid` bigint NOT NULL,
  `uploadqos` decimal(20,0) DEFAULT NULL,
  `downloadqos` decimal(20,0) DEFAULT NULL,
  `CREATEDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CREATEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` decimal(20,0) DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`planqosid`),
  UNIQUE KEY `planqosdtls_planqosid_unq` (`planqosid`),
  KEY `tblplanqosdtls_ibfk_1` (`planid`),
  CONSTRAINT `tblplanqosdtls_ibfk_1` FOREIGN KEY (`planid`) REFERENCES `tblmpostpaidplan` (`POSTPAIDPLANID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
