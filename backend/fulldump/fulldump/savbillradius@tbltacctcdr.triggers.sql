-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: tbltacctcdr
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Dumping triggers for table 'savbillradius'.'tbltacctcdr'
--

-- begin trigger `savbillradius`.`trg_after_insert_tbltacctcdr`
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
/*!50032 DROP TRIGGER IF EXISTS `trg_after_insert_tbltacctcdr` */;
DELIMITER ;;
/*!50003 CREATE DEFINER=`Savbillradius`@`%` TRIGGER `trg_after_insert_tbltacctcdr` AFTER INSERT ON `tbltacctcdr` FOR EACH ROW BEGIN
            DECLARE plan_name VARCHAR(500) DEFAULT '';
            DECLARE total_upload BIGINT DEFAULT 0;
            DECLARE total_download BIGINT DEFAULT 0;
            DECLARE total_sum BIGINT DEFAULT 0;
            DECLARE total_session_time BIGINT DEFAULT 0;
            IF NEW.custid != 0 THEN
            SELECT pp.NAME
            INTO plan_name
            FROM tblmpostpaidplan pp
            WHERE pp.POSTPAIDPLANID =
                  (SELECT planid
                   FROM tblcustquotadtls tcq
                   WHERE custid = (
                       SELECT tc.custid
                       FROM tblcustomers tc
                       WHERE tc.username = NEW.userName
                   )
                   ORDER BY tcq.quotadtlsid DESC
                LIMIT 1);

            SELECT
                IFNULL(SUM(UPLOAD), 0),
                IFNULL(SUM(DOWNLOAD), 0),
                IFNULL(SUM(TOTAL), 0),
                IFNULL(SUM(CDRTIME), 0)
            INTO
                total_upload,
                total_download,
                total_sum,
                total_session_time
            FROM
                tblmprocesscdr
            WHERE
                SESSIONID = NEW.AcctSessionId;


            INSERT INTO tblmprocesscdr (
                USERNAME,
                SESSIONID,
                FRAMEDIPADDRESS,
                NASIPADDRESS,
                MACADDRESS,
                NASPORTID,
                FRAMED_IPV6_ADDRESS,
                AGGREGATEKEY,
                FRAMED_INTERFACE_ID,
                DELEGATED_IPV6_PREFIX,
                UPLOAD,
                DOWNLOAD,
                TOTAL,
                CDRTIME,
                STARTTIME,
                ENDTIME,
                REQUESTTYPE,
                SESSIONAUTHRULE
            )
            VALUES (
                       NEW.userName,
                       NEW.AcctSessionId,
                       NEW.FramedIPAddress,
                       NEW.NASIPAddress,
                       REPLACE(NEW.CallingStationId, '-', ':'),
                       NEW.NASPort,
                       NEW.framedipv6address,
                       "Default Service",
                       (
                           SELECT GROUP_CONCAT(SUBSTRING(REPLACE(NEW.FramedInterfaceId, ':', ''), (n * 2) + 1, 2) SEPARATOR ':')
                           FROM (
                                    SELECT @rownum := @rownum + 1 AS n
                                    FROM (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t1,
                                        (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t2,
                                        (SELECT @rownum := -1) r
                                ) numbers
                           WHERE (n * 2) < LENGTH(REPLACE(NEW.FramedInterfaceId, ':', ''))
                                                 ),
                               NEW.DelegatedIPv6Prefix,

                               NEW.AcctInputOctets - total_upload,
                               NEW.AcctOutputOctets - total_download ,
                               (NEW.AcctInputOctets + NEW.AcctOutputOctets) - total_sum,
                               NEW.AcctSessionTime - total_session_time,
                               NEW.createdate,
               DATE_SUB(NOW(), INTERVAL (NEW.AcctSessionTime) SECOND),
                               IFNULL(NEW.AcctStatusType, 'Stop'),
							   CONCAT(plan_name, "##", "TOTAL_QoS_Profile", "#",0)
                           );
        END IF;
        END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
-- end trigger `savbillradius`.`trg_after_insert_tbltacctcdr`

