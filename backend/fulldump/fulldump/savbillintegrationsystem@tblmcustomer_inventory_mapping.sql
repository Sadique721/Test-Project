-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillintegrationsystem    Table: tblmcustomer_inventory_mapping
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblmcustomer_inventory_mapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblmcustomer_inventory_mapping` (
  `mapping_id` bigint NOT NULL AUTO_INCREMENT,
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
  `is_invoice_created` bigint DEFAULT NULL,
  `connection_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `replacement_reason` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `plan_id` bigint DEFAULT NULL,
  `mapping_ref_id` bigint DEFAULT NULL,
  `remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `plangroup_id` bigint DEFAULT NULL,
  `offer_price` bigint DEFAULT NULL,
  `charge_id` bigint DEFAULT NULL,
  `bill_to` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_invoice_to_org` bigint DEFAULT NULL,
  `new_amount` bigint DEFAULT NULL,
  `discount` bigint DEFAULT NULL,
  `is_required_approval` bigint DEFAULT NULL,
  `is_free` bigint DEFAULT NULL,
  `payment_owner_id` bigint DEFAULT NULL,
  `billable_cust_id` bigint DEFAULT NULL,
  `ezybill_stock_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pairstatus` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`mapping_id`),
  UNIQUE KEY `mapping_id` (`mapping_id`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
