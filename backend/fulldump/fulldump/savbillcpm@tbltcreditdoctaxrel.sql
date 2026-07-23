-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltcreditdoctaxrel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltcreditdoctaxrel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltcreditdoctaxrel` (
  `creditdoctaxid` bigint NOT NULL AUTO_INCREMENT,
  `CREDITDOCID` bigint DEFAULT NULL,
  `CHARGEID` bigint DEFAULT NULL,
  `creditdocchargeid` bigint DEFAULT NULL,
  `tax_amount` double DEFAULT NULL,
  PRIMARY KEY (`creditdoctaxid`),
  UNIQUE KEY `tbltcreditdoctaxrel_id_unq` (`creditdoctaxid`),
  KEY `fk_chargeid_tbltcreditdoctaxrel` (`CHARGEID`),
  KEY `fk_CREDITDOCID_tbltcreditdoctaxrel` (`CREDITDOCID`),
  KEY `fk_creditdocchargeid_tbltcreditdoctaxrel` (`creditdocchargeid`),
  CONSTRAINT `fk_chargeid_tbltcreditdoctaxrel` FOREIGN KEY (`CHARGEID`) REFERENCES `tblcharges` (`CHARGEID`),
  CONSTRAINT `fk_creditdocchargeid_tbltcreditdoctaxrel` FOREIGN KEY (`creditdocchargeid`) REFERENCES `tbltcreditdocchargerel` (`creditdocchargeid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
