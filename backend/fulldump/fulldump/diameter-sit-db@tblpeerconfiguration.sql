-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: diameter-sit-db    Table: tblpeerconfiguration
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Table structure for table `tblpeerconfiguration`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tblpeerconfiguration` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `node_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `realm` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `fqdn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `sctp_listen_port` int NOT NULL DEFAULT '3868',
  `tcp_listen_port` int NOT NULL DEFAULT '3868',
  `dtls_sctp_listen_port` int NOT NULL DEFAULT '5658',
  `tls_tcp_listen_port` int NOT NULL DEFAULT '5658',
  `radius_udp_server_ports` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `enable_radius_udp_client_ports` tinyint(1) NOT NULL DEFAULT '0',
  `radius_client_udp_port_range_start` int NOT NULL DEFAULT '2000',
  `radius_client_udp_port_range_end` int NOT NULL DEFAULT '2499',
  `verification_mode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Verify None',
  `certificate_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `certificate_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ip_addresses` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `watchdog_interval` int NOT NULL DEFAULT '1000',
  `remote_port` int NOT NULL DEFAULT '3868',
  `remote_ip_address` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Active',
  PRIMARY KEY (`id`),
  UNIQUE KEY `node_name` (`node_name`),
  CONSTRAINT `chk_dtls_sctp_listen_port_range` CHECK ((`dtls_sctp_listen_port` between 1024 and 49151)),
  CONSTRAINT `chk_fqdn_format` CHECK ((regexp_like(`fqdn`,_utf8mb4'^[a-zA-Z_][a-zA-Z0-9-]*[a-zA-Z0-9](.[a-zA-Z][a-zA-Z0-9-]*[a-zA-Z0-9])*$') and (length(`fqdn`) <= 255))),
  CONSTRAINT `chk_radius_client_ports_when_enabled` CHECK ((((`enable_radius_udp_client_ports` = false) and (`radius_client_udp_port_range_start` is null) and (`radius_client_udp_port_range_end` is null)) or ((`enable_radius_udp_client_ports` = true) and (`radius_client_udp_port_range_start` is not null) and (`radius_client_udp_port_range_end` is not null)))),
  CONSTRAINT `chk_radius_port_range` CHECK (((`radius_client_udp_port_range_start` between 1024 and 49151) and (`radius_client_udp_port_range_end` between 1024 and 49151) and (`radius_client_udp_port_range_start` <= `radius_client_udp_port_range_end`))),
  CONSTRAINT `chk_realm_format` CHECK ((regexp_like(`realm`,_utf8mb4'^[a-zA-Z_][a-zA-Z0-9-]*[a-zA-Z0-9](.[a-zA-Z][a-zA-Z0-9-]*[a-zA-Z0-9])*$') and (length(`realm`) <= 255))),
  CONSTRAINT `chk_remote_port_range` CHECK ((`remote_port` between 1024 and 49151)),
  CONSTRAINT `chk_sctp_listen_port_range` CHECK ((`sctp_listen_port` between 1024 and 49151)),
  CONSTRAINT `chk_status` CHECK ((`status` in (_utf8mb4'Active',_utf8mb4'Inactive',_utf8mb4'Deleted'))),
  CONSTRAINT `chk_tcp_listen_port_range` CHECK ((`tcp_listen_port` between 1024 and 49151)),
  CONSTRAINT `chk_tls_tcp_listen_port_range` CHECK ((`tls_tcp_listen_port` between 1024 and 49151)),
  CONSTRAINT `chk_verification_mode` CHECK ((`verification_mode` in (_utf8mb4'Verify None',_utf8mb4'Verify Peer',_utf8mb4'Fail if No Peer Certificate',_utf8mb4'Verify Client Once'))),
  CONSTRAINT `chk_watchdog_interval` CHECK ((`watchdog_interval` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
