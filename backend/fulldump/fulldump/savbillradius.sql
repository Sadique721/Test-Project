-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Current Database: savbillradius
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `savbillradius` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `savbillradius`;

--
-- Dumping events for database 'savbillradius'
--
/*!50106 SET @save_time_zone= @@TIME_ZONE */ ;

-- begin event `savbillradius`.`event_check_new_faulty_macs`
/*!50106 DROP EVENT IF EXISTS `event_check_new_faulty_macs` */;
DELIMITER ;;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;;
/*!50003 SET character_set_client  = utf8mb4 */ ;;
/*!50003 SET character_set_results = utf8mb4 */ ;;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;;
/*!50003 SET @saved_time_zone      = @@time_zone */ ;;
/*!50003 SET time_zone             = 'SYSTEM' */ ;;
/*!50106 CREATE DEFINER=`Savbillradius`@`%` EVENT IF NOT EXISTS `event_check_new_faulty_macs` ON SCHEDULE EVERY 15 MINUTE STARTS '2025-07-16 19:32:48' ON COMPLETION NOT PRESERVE ENABLE DO INSERT INTO tbltfaultymac (
                mack_id,
                is_active,
                is_deleted,
                mvnoid
            )
            SELECT
                CallingStationId,
                1 AS is_active,
                0 AS is_deleted,
                MIN(mvnoid)
            FROM
                tbltliveuser t
            WHERE
                CallingStationId IN (
                    SELECT
                        CallingStationId
                    FROM
                        tbltliveuser
                    WHERE
                        UserName != CallingStationId
                    GROUP BY
                        CallingStationId
                    HAVING
                        COUNT(DISTINCT UserName) > 1
                ) and
                CallingStationId not IN(
                    SELECT
                        mack_id
                    FROM
                        tbltfaultymac
                    WHERE
                        is_deleted = 0
                )
                GROUP BY
                    CallingStationId
                HAVING
                    COUNT(t.CallingStationId) > 1 */ ;;
/*!50003 SET time_zone             = @saved_time_zone */ ;;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;;
/*!50003 SET character_set_client  = @saved_cs_client */ ;;
/*!50003 SET character_set_results = @saved_cs_results */ ;;
/*!50003 SET collation_connection  = @saved_col_connection */ ;;
-- end event `savbillradius`.`event_check_new_faulty_macs`

DELIMITER ;
/*!50106 SET TIME_ZONE= @save_time_zone */ ;

--
-- Dumping routines for database 'savbillradius'
--

-- begin procedure `savbillradius`.`updates_mvnoid`
/*!50003 DROP PROCEDURE IF EXISTS `updates_mvnoid` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`savbillradius`@`%` PROCEDURE `updates_mvnoid`(IN old_mvnoid INT, IN new_mvnoid INT)
BEGIN
    DECLARE tableName VARCHAR(100);
    DECLARE done BOOLEAN DEFAULT FALSE;

    DECLARE cur CURSOR FOR
            SELECT TABLE_NAME
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = 'savbillradius'
              AND COLUMN_NAME LIKE '%mvnoid%'
              AND TABLE_NAME NOT IN (
                                     'tblmmvno', 'tblstaffuser',
                                     'tblmrole', 'tbltstaffbusinessunitrel',
                                     'tblstaffservicearearel','tblmclientservice'
                );

            DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
   	SET FOREIGN_KEY_CHECKS = 0;

            OPEN cur;
            read_loop: LOOP
        FETCH cur INTO tableName;
        IF done THEN
            LEAVE read_loop;
            END IF;
        SET @sql = CONCAT('UPDATE ', tableName, ' SET mvnoId =',new_mvnoid,' WHERE mvnoId = ', old_mvnoid ,';');
            select @sql;
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
            END LOOP;
            CLOSE cur;
            SET FOREIGN_KEY_CHECKS = 1;

            END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
-- end procedure `savbillradius`.`updates_mvnoid`

-- begin procedure `savbillradius`.`insert_vlan_mappings_from_management`
/*!50003 DROP PROCEDURE IF EXISTS `insert_vlan_mappings_from_management` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`savbillradius`@`%` PROCEDURE `insert_vlan_mappings_from_management`()
BEGIN
            DECLARE done INT DEFAULT FALSE;
            DECLARE next_validationmappingid INT;
            DECLARE v_VLANID INT;
            DECLARE v_CIRCUIT_TYPE VARCHAR(255);
            DECLARE v_NAS_PORT_ID_2 VARCHAR(255);
            DECLARE v_NAS_PORT_ID_3 VARCHAR(255);
            DECLARE v_NAS_PORT_ID_4 VARCHAR(255);
            DECLARE v_NAS_PORT_ID_5 VARCHAR(255);
            DECLARE v_exists_1 INT;
            DECLARE v_exists_2 INT;
            DECLARE v_final_regex_value VARCHAR(500);

            DECLARE cur CURSOR FOR
            SELECT VLANID, CIRCUIT_TYPE, NAS_PORT_ID_2, NAS_PORT_ID_3, NAS_PORT_ID_4, NAS_PORT_ID_5
            FROM savbillradius.tblmvlanmanagement;

            DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

            OPEN cur;
            read_loop: LOOP
            FETCH cur INTO v_VLANID, v_CIRCUIT_TYPE, v_NAS_PORT_ID_2, v_NAS_PORT_ID_3, v_NAS_PORT_ID_4, v_NAS_PORT_ID_5;
            IF done THEN
            LEAVE read_loop;
            END IF;

            SELECT COUNT(*) INTO v_exists_1
            FROM savbillradius.tblmvlanvalidationmapping
            WHERE VLANID = v_VLANID AND REGEX = 'REGEX{\\b(pppoe|clips)\\b,{PROFILE{vlan.CIRCUIT_TYPE}}}';

            IF v_exists_1 > 0 THEN
            UPDATE savbillradius.tblmvlanvalidationmapping
            SET REGEXVALUE = v_CIRCUIT_TYPE
            WHERE VLANID = v_VLANID
            AND REGEX = 'REGEX{\\b(pppoe|clips)\\b,{PROFILE{vlan.CIRCUIT_TYPE}}}';
            ELSE
            SELECT COALESCE(MAX(VALIDATIONMAPPINGID), 0) + 1 INTO next_validationmappingid
            FROM savbillradius.tblmvlanvalidationmapping;

            INSERT INTO savbillradius.tblmvlanvalidationmapping
            (VALIDATIONMAPPINGID, VLANID, RADIUS_ATTRIBUTE, REGEX, REGEXVALUE)
            VALUES
            (next_validationmappingid, v_VLANID, 'NAS-Port-Id',
            'REGEX{\\b(pppoe|clips)\\b,{PROFILE{vlan.CIRCUIT_TYPE}}}', v_CIRCUIT_TYPE);
            END IF;

            
            SELECT COUNT(*) INTO v_exists_2
            FROM savbillradius.tblmvlanvalidationmapping
            WHERE VLANID = v_VLANID
            AND REGEX LIKE '{EXP{MERGE%}';




            IF v_NAS_PORT_ID_5 IS NOT NULL THEN
            SET v_final_regex_value = CONCAT('CONTAINS{REQ{NAS-Port-Id},', v_NAS_PORT_ID_2, ' ',v_NAS_PORT_ID_3,' ', v_NAS_PORT_ID_4, ':', v_NAS_PORT_ID_5, '}');
            ELSE
            SET v_final_regex_value = CONCAT('CONTAINS{REQ{NAS-Port-Id},', v_NAS_PORT_ID_2,' ', v_NAS_PORT_ID_3,' ', v_NAS_PORT_ID_4, ':}');
            END IF;

            
            IF v_exists_2 > 0 THEN
            UPDATE savbillradius.tblmvlanvalidationmapping
            SET REGEXVALUE = v_final_regex_value
            WHERE VLANID = v_VLANID
            AND REGEX LIKE '{EXP{MERGE%}';
            ELSE
            SET next_validationmappingid = next_validationmappingid + 1;

            INSERT INTO savbillradius.tblmvlanvalidationmapping
            (VALIDATIONMAPPINGID, VLANID, RADIUS_ATTRIBUTE, REGEX, REGEXVALUE)
            VALUES
            (next_validationmappingid, v_VLANID, 'NAS-Port-Id',
            '{EXP{MERGE{PROFILE{vlan.NAS_PORT_ID_2}},MERGE{ },MERGE{PROFILE{vlan.NAS_PORT_ID_3}},MERGE{ },MERGE{PROFILE{vlan.NAS_PORT_ID_4}},MERGE{:},MERGE{PROFILE{vlan.NAS_PORT_ID_5}}}}',
            v_final_regex_value);
            END IF;
            END LOOP;

            CLOSE cur;
            END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
-- end procedure `savbillradius`.`insert_vlan_mappings_from_management`

