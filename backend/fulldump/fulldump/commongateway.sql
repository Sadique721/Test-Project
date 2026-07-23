-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: commongateway
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Current Database: commongateway
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `commongateway` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `commongateway`;

--
-- Dumping events for database 'commongateway'
--

--
-- Dumping routines for database 'commongateway'
--

-- begin procedure `commongateway`.`updates_mvnoid`
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
CREATE DEFINER=`commongateway`@`%` PROCEDURE `updates_mvnoid`(IN old_mvnoid INT, IN new_mvnoid INT)
BEGIN
    DECLARE
            tableName VARCHAR(100);
    DECLARE
            done BOOLEAN DEFAULT FALSE;

    DECLARE
            cur CURSOR FOR
            SELECT TABLE_NAME
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = 'savbillcommonapigateway'
              AND COLUMN_NAME LIKE '%mvnoid%'
              AND TABLE_NAME NOT IN (
                                     'tblmmvno', 'tblmstaffuser',
                                     'tblmroles', 'tbltstaffrolerel', 'tbltstaffbusinessunitrel',
                                     'tbltstaffservicearearel', 'tblmclientservice','tblmpaymentconfig',
                                     'tblmpaymentconfigmapping','tblmteams','tblmpaymentconfig',
                                     'tblmpaymentconfigmapping'
                );
            DECLARE
            CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
   	SET
            FOREIGN_KEY_CHECKS = 0;

            OPEN cur;
            read_loop
            : LOOP
        FETCH cur INTO tableName;
        IF
            done THEN
            LEAVE read_loop;
            END IF;
        SET
            @sql = CONCAT('UPDATE ', tableName, ' SET mvnoId =',new_mvnoid,' WHERE mvnoId = ', old_mvnoid ,';');
            select @sql;
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
            END LOOP;
            CLOSE cur;
            SET
            FOREIGN_KEY_CHECKS = 1;
            END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
-- end procedure `commongateway`.`updates_mvnoid`

