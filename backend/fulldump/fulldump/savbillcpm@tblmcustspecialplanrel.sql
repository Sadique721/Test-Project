-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmcustspecialplanrel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmcustspecialplanrel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmcustspecialplanrel` (
  `custspecialplanid` bigint NOT NULL AUTO_INCREMENT,
  `custid` bigint DEFAULT NULL,
  `specialplanid` bigint DEFAULT NULL,
  `normalplanid` bigint DEFAULT NULL,
  `service` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdbystaffid` decimal(20,0) DEFAULT NULL,
  `createdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastmodifiedbystaffid` decimal(20,0) DEFAULT NULL,
  `lastmodifieddate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CUSTSPPLANID` bigint DEFAULT NULL,
  `specialplangroupid` bigint DEFAULT NULL,
  `normalplangroupid` bigint DEFAULT NULL,
  `MVNOID` bigint DEFAULT NULL,
  `BUID` bigint DEFAULT NULL,
  `leadcustid` bigint DEFAULT NULL,
  `mvno_name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`custspecialplanid`),
  UNIQUE KEY `TBLMCUSTSPECIALPLANREL_custspecialplanid_unq` (`custspecialplanid`),
  KEY `TBLMCUSTSPECIALPLANREL_ibfk_1` (`custid`),
  KEY `TBLMCUSTSPECIALPLANREL_ibfk_2` (`specialplanid`),
  KEY `TBLMCUSTSPECIALPLANREL_ibfk_3` (`normalplanid`),
  KEY `TBLMCUSTSPECIALPLANREL_ibfk_4` (`CUSTSPPLANID`),
  KEY `TBLMCUSTSPECIALPLANREL_ibfk_5` (`specialplangroupid`),
  KEY `TBLMCUSTSPECIALPLANREL_ibfk_6` (`normalplangroupid`),
  CONSTRAINT `TBLMCUSTSPECIALPLANREL_ibfk_1` FOREIGN KEY (`custid`) REFERENCES `tblcustomers` (`custid`),
  CONSTRAINT `TBLMCUSTSPECIALPLANREL_ibfk_2` FOREIGN KEY (`specialplanid`) REFERENCES `tblmpostpaidplan` (`POSTPAIDPLANID`),
  CONSTRAINT `TBLMCUSTSPECIALPLANREL_ibfk_3` FOREIGN KEY (`normalplanid`) REFERENCES `tblmpostpaidplan` (`POSTPAIDPLANID`),
  CONSTRAINT `TBLMCUSTSPECIALPLANREL_ibfk_4` FOREIGN KEY (`CUSTSPPLANID`) REFERENCES `tblmcustspecialplanrelmapping` (`CUSTSPPLANID`) ON DELETE CASCADE,
  CONSTRAINT `TBLMCUSTSPECIALPLANREL_ibfk_5` FOREIGN KEY (`specialplangroupid`) REFERENCES `tblmplangroup` (`plangroupid`),
  CONSTRAINT `TBLMCUSTSPECIALPLANREL_ibfk_6` FOREIGN KEY (`normalplangroupid`) REFERENCES `tblmplangroup` (`plangroupid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
