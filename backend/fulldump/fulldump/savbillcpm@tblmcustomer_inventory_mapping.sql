-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm    Table: tblmcustomer_inventory_mapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmcustomer_inventory_mapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmcustomer_inventory_mapping` (
  `mapping_id` bigint NOT NULL,
  `quantity` bigint DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `staff_id` bigint DEFAULT NULL,
  `assigned_date_time` timestamp NULL DEFAULT NULL,
  `mvno_id` bigint DEFAULT NULL,
  `CREATEDATE` timestamp NULL DEFAULT NULL,
  `LASTMODIFIEDDATE` timestamp NULL DEFAULT NULL,
  `createbyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updatebyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `CREATEDBYSTAFFID` bigint DEFAULT NULL,
  `LASTMODIFIEDBYSTAFFID` bigint DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `expiry_date_time` timestamp NULL DEFAULT NULL,
  `next_approver` bigint DEFAULT NULL,
  `team_hierarchy_mapping_id` bigint DEFAULT NULL,
  `previous_approve_id` bigint DEFAULT NULL,
  `inward_id` bigint DEFAULT NULL,
  `external_item_id` bigint DEFAULT NULL,
  `service_id` bigint DEFAULT NULL,
  `custpack_id` bigint DEFAULT NULL,
  `item_id` bigint DEFAULT NULL,
  `itemassemblyid` bigint DEFAULT NULL,
  `connection_no` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_invoice_created` tinyint(1) DEFAULT '0',
  `replacement_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `plan_id` bigint DEFAULT NULL,
  `mapping_ref_id` bigint DEFAULT NULL,
  `remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `plangroup_id` bigint DEFAULT NULL,
  `offer_price` double DEFAULT NULL,
  `charge_id` bigint DEFAULT NULL,
  `bill_to` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'CUSTOMER',
  `is_invoice_to_org` tinyint(1) DEFAULT '0',
  `new_amount` decimal(20,4) DEFAULT NULL,
  `discount` double DEFAULT NULL,
  `is_required_approval` tinyint(1) DEFAULT '0',
  `is_free` tinyint(1) DEFAULT '0',
  `payment_owner_id` bigint DEFAULT NULL,
  `ezybill_stock_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `billable_cust_id` bigint DEFAULT NULL,
  `pairstatus` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `non_seri_remark` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `vendorId` bigint DEFAULT NULL,
  PRIMARY KEY (`mapping_id`),
  UNIQUE KEY `mapping_id` (`mapping_id`),
  KEY `tblmcustomer_inventory_mapping_product_fk` (`product_id`),
  KEY `tblmcustomer_inventory_mapping_customer_id_fk` (`customer_id`),
  KEY `tblmcustomer_inventory_mapping_inward_inward_fk` (`inward_id`),
  KEY `tblmcustomer_inventory_mapping_external_item_id_fk` (`external_item_id`),
  KEY `tblmcustomer_inventory_mapping_staff_id_fk` (`staff_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
