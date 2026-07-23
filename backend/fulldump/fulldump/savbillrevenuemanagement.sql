-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillrevenuemanagement
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Current Database: savbillrevenuemanagement
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `savbillrevenuemanagement` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `savbillrevenuemanagement`;

--
-- Dumping events for database 'savbillrevenuemanagement'
--

--
-- Dumping routines for database 'savbillrevenuemanagement'
--

-- begin function `savbillrevenuemanagement`.`nextvaltrial`
/*!50003 DROP FUNCTION IF EXISTS `nextvaltrial` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`savbillrevenuemanagement`@`%` FUNCTION `nextvaltrial`(seq_name varchar(100)) RETURNS bigint
BEGIN
            DECLARE cur_val bigint;
            SELECT
                cur_value INTO cur_val
            FROM
                sequence
            WHERE
                name = seq_name;
            IF cur_val IS NOT NULL THEN
            UPDATE
                sequence
            SET
                cur_value = IF (
                                (cur_value + increment) > max_value OR (cur_value + increment) < min_value,
                                IF (
                                            cycle = TRUE,
                                            IF (
                                                        (cur_value + increment) > max_value,
                                                        min_value,
                                                        max_value
                                                ),
                                            NULL
                                    ),
                                cur_value + increment
                    )
            WHERE
                name = seq_name;
            END IF;
            RETURN cur_val;
            END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
-- end function `savbillrevenuemanagement`.`nextvaltrial`

-- begin function `savbillrevenuemanagement`.`nextvalpayment`
/*!50003 DROP FUNCTION IF EXISTS `nextvalpayment` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`savbillrevenuemanagement`@`%` FUNCTION `nextvalpayment`(seq_name varchar(100)) RETURNS bigint
BEGIN
            DECLARE cur_val bigint;
            SELECT
                cur_value INTO cur_val
            FROM
                sequence
            WHERE
                name = seq_name;
            IF cur_val IS NOT NULL THEN
            UPDATE
                sequence
            SET
                cur_value = IF (
                                (cur_value + increment) > max_value OR (cur_value + increment) < min_value,
                                IF (
                                            cycle = TRUE,
                                            IF (
                                                        (cur_value + increment) > max_value,
                                                        min_value,
                                                        max_value
                                                ),
                                            NULL
                                    ),
                                cur_value + increment
                    )
            WHERE
                name = seq_name;
            END IF;
            RETURN cur_val;
            END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
-- end function `savbillrevenuemanagement`.`nextvalpayment`

-- begin function `savbillrevenuemanagement`.`nextval`
/*!50003 DROP FUNCTION IF EXISTS `nextval` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`savbillrevenuemanagement`@`%` FUNCTION `nextval`(seq_name varchar(100)) RETURNS varchar(2000) CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci
BEGIN
            DECLARE cur_val bigint;
           	DECLARE cur_valuestr varchar(2000);
            DECLARE append varchar(360);
            DECLARE cur_year INT;
  			SET cur_year = (SELECT YEAR(CURRENT_DATE()));
            SELECT
                cur_value INTO cur_val
            FROM
                sequence
            WHERE
                name = seq_name;
            IF cur_val IS NOT NULL THEN
            UPDATE
                sequence
            SET
                cur_value = IF (
                                (cur_value + increment) > max_value OR (cur_value + increment) < min_value,
                                IF (
                                            cycle = TRUE,
                                            IF (
                                                        (cur_value + increment) > max_value,
                                                        min_value,
                                                        max_value
                                                ),
                                            NULL
                                    ),
                                cur_value + increment
                    )
            WHERE
                name = seq_name;
            SET append = (SELECT YEAR(CURRENT_DATE()));
            SET cur_valuestr = (SELECT LPAD(cur_val, 7, "0"));
        END IF;
        RETURN CONCAT(cur_year,'-',cur_valuestr);
        END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
-- end function `savbillrevenuemanagement`.`nextval`

-- begin function `savbillrevenuemanagement`.`nextvalcreditnote`
/*!50003 DROP FUNCTION IF EXISTS `nextvalcreditnote` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`savbillrevenuemanagement`@`%` FUNCTION `nextvalcreditnote`(seq_name varchar(100)) RETURNS bigint
BEGIN
            DECLARE cur_val bigint;
            SELECT
                cur_value INTO cur_val
            FROM
                sequence
            WHERE
                name = seq_name;
            IF cur_val IS NOT NULL THEN
            UPDATE
                sequence
            SET
                cur_value = IF (
                                (cur_value + increment) > max_value OR (cur_value + increment) < min_value,
                                IF (
                                            cycle = TRUE,
                                            IF (
                                                        (cur_value + increment) > max_value,
                                                        min_value,
                                                        max_value
                                                ),
                                            NULL
                                    ),
                                cur_value + increment
                    )
            WHERE
                name = seq_name;
            END IF;
            RETURN cur_val;
            END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
-- end function `savbillrevenuemanagement`.`nextvalcreditnote`

-- begin function `savbillrevenuemanagement`.`nextvalconnection`
/*!50003 DROP FUNCTION IF EXISTS `nextvalconnection` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`savbillrevenuemanagement`@`%` FUNCTION `nextvalconnection`(seq_name varchar(100)) RETURNS bigint
BEGIN
            DECLARE cur_val bigint;
            SELECT
                cur_value INTO cur_val
            FROM
                sequence
            WHERE
                name = seq_name;
            IF cur_val IS NOT NULL THEN
            UPDATE
                sequence
            SET
                cur_value = IF (
                                (cur_value + increment) > max_value OR (cur_value + increment) < min_value,
                                IF (
                                            cycle = TRUE,
                                            IF (
                                                        (cur_value + increment) > max_value,
                                                        min_value,
                                                        max_value
                                                ),
                                            NULL
                                    ),
                                cur_value + increment
                    )
            WHERE
                name = seq_name;
            END IF;
            RETURN cur_val;
            END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
-- end function `savbillrevenuemanagement`.`nextvalconnection`

-- begin procedure `savbillrevenuemanagement`.`updates_mvnoid`
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
CREATE DEFINER=`savbillrevenuemanagement`@`%` PROCEDURE `updates_mvnoid`(IN old_mvnoid INT, IN new_mvnoid INT)
BEGIN
            DECLARE tableName VARCHAR(100);
            DECLARE done BOOLEAN DEFAULT FALSE;
            DECLARE cur CURSOR FOR
            SELECT TABLE_NAME
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = 'savbillrevenuemanagement'
            AND COLUMN_NAME LIKE '%mvnoid%'
            AND TABLE_NAME NOT IN (
            'tblmmvno', 'tblstaffuser','tblroles', 'tblstaffrolerel',
            'tbltstaffuserreceiptmapping','tblclientservice',
            'tblteams','tbltemplatemanagement'
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
-- end procedure `savbillrevenuemanagement`.`updates_mvnoid`

