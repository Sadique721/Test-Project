-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmdiscountpostpaidplanrel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmdiscountpostpaidplanrel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmdiscountpostpaidplanrel` (
  `DISCOUNTPLANRELID` bigint NOT NULL AUTO_INCREMENT,
  `DISCOUNTID` bigint DEFAULT NULL,
  `POSTPAIDPLANID` bigint DEFAULT NULL,
  PRIMARY KEY (`DISCOUNTPLANRELID`),
  UNIQUE KEY `mdiscountfieldmapping_Id_unq` (`DISCOUNTPLANRELID`),
  KEY `tblmdiscountpostpaidplanrel_ibfk_1` (`DISCOUNTID`),
  KEY `tblmdiscountpostpaidplanrel_ibfk_2` (`POSTPAIDPLANID`),
  CONSTRAINT `tblmdiscountpostpaidplanrel_ibfk_1` FOREIGN KEY (`DISCOUNTID`) REFERENCES `tblmdiscount` (`DISCOUNTID`),
  CONSTRAINT `tblmdiscountpostpaidplanrel_ibfk_2` FOREIGN KEY (`POSTPAIDPLANID`) REFERENCES `tblmpostpaidplan` (`POSTPAIDPLANID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
