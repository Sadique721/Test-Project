-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tbltplangroupmappingchargerel
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tbltplangroupmappingchargerel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tbltplangroupmappingchargerel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `plan_group_mappingid` bigint DEFAULT NULL,
  `chargeid` bigint DEFAULT NULL,
  `price` double DEFAULT NULL,
  `chargeName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `isdelete` tinyint(1) DEFAULT '0',
  `planid` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tbltplangroupmappingchargerel_field_unq` (`id`),
  KEY `fk_plan_group_mappingid_tbltplangroupmappingchargerel` (`plan_group_mappingid`),
  KEY `fk_chargeid_tbltplangroupmappingchargerel` (`chargeid`),
  KEY `fk_planid_tbltplangroupmappingchargerel` (`planid`),
  CONSTRAINT `fk_chargeid_tbltplangroupmappingchargerel` FOREIGN KEY (`chargeid`) REFERENCES `tblcharges` (`CHARGEID`),
  CONSTRAINT `fk_plan_group_mappingid_tbltplangroupmappingchargerel` FOREIGN KEY (`plan_group_mappingid`) REFERENCES `tblmplangroupmapping` (`plangroupmappingid`),
  CONSTRAINT `fk_planid_tbltplangroupmappingchargerel` FOREIGN KEY (`planid`) REFERENCES `tblmpostpaidplan` (`POSTPAIDPLANID`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
