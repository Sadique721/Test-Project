-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmpostpaidplanchargerel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmpostpaidplanchargerel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmpostpaidplanchargerel` (
  `POSTPAIDPLANCHARGERELID` bigint NOT NULL AUTO_INCREMENT,
  `CHARGEID` bigint NOT NULL,
  `BILLINGCYCLE` decimal(2,0) DEFAULT NULL,
  `CREATEDATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `POSTPAIDPLANID` bigint DEFAULT NULL,
  `chargeprice` decimal(20,8) DEFAULT NULL,
  `chargeName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`POSTPAIDPLANCHARGERELID`),
  UNIQUE KEY `mpostpaidplanchargerel_id_unq` (`POSTPAIDPLANCHARGERELID`),
  KEY `tblmpostpaidplanchargerel_ibfk_2` (`CHARGEID`),
  KEY `index_TBLMPOSTPAIDPLANCHARGEREL_planId_chargeId` (`POSTPAIDPLANID`,`CHARGEID`),
  CONSTRAINT `tblmpostpaidplanchargerel_ibfk_1` FOREIGN KEY (`POSTPAIDPLANID`) REFERENCES `tblmpostpaidplan` (`POSTPAIDPLANID`),
  CONSTRAINT `tblmpostpaidplanchargerel_ibfk_2` FOREIGN KEY (`CHARGEID`) REFERENCES `tblcharges` (`CHARGEID`)
) ENGINE=InnoDB AUTO_INCREMENT=276 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
