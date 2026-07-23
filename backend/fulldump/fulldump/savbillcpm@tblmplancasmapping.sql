-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmplancasmapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmplancasmapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmplancasmapping` (
  `plancasmappingid` bigint NOT NULL AUTO_INCREMENT,
  `planid` bigint DEFAULT NULL,
  `casid` bigint DEFAULT NULL,
  `packageid` bigint DEFAULT NULL,
  PRIMARY KEY (`plancasmappingid`),
  KEY `planid_fk` (`planid`),
  KEY `casid_fk` (`casid`),
  CONSTRAINT `casid_fk` FOREIGN KEY (`casid`) REFERENCES `tbltcasmaster` (`id`),
  CONSTRAINT `planid_fk` FOREIGN KEY (`planid`) REFERENCES `tblmpostpaidplan` (`POSTPAIDPLANID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
