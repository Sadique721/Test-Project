-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillcpm
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Current Database: savbillcpm
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `savbillcpm` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `savbillcpm`;

--
-- Dumping events for database 'savbillcpm'
--

--
-- Dumping routines for database 'savbillcpm'
--

-- begin function `savbillcpm`.`nextvaltrial`
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
CREATE DEFINER=`Savbillcpm`@`%` FUNCTION `nextvaltrial`(seq_name varchar(100)) RETURNS bigint
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
-- end function `savbillcpm`.`nextvaltrial`

-- begin function `savbillcpm`.`nextvalpayment`
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
CREATE DEFINER=`Savbillcpm`@`%` FUNCTION `nextvalpayment`(seq_name varchar(100)) RETURNS varchar(2000) CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci
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
            RETURN CONCAT('PY',cur_year,'-',cur_valuestr);
            END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
-- end function `savbillcpm`.`nextvalpayment`

-- begin function `savbillcpm`.`nextval`
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
CREATE DEFINER=`Savbillcpm`@`%` FUNCTION `nextval`(seq_name varchar(100)) RETURNS varchar(2000) CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci
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
-- end function `savbillcpm`.`nextval`

-- begin function `savbillcpm`.`nextvalcreditnote`
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
CREATE DEFINER=`Savbillcpm`@`%` FUNCTION `nextvalcreditnote`(seq_name varchar(100)) RETURNS varchar(2000) CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci
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
            RETURN CONCAT('CN',cur_year,'-',cur_valuestr);
            END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
-- end function `savbillcpm`.`nextvalcreditnote`

-- begin function `savbillcpm`.`nextvalconnection`
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
CREATE DEFINER=`Savbillcpm`@`%` FUNCTION `nextvalconnection`(seq_name varchar(100)) RETURNS varchar(2000) CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci
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
        RETURN CONCAT('SERV',cur_year,'-',cur_valuestr);
        END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
-- end function `savbillcpm`.`nextvalconnection`

-- begin procedure `savbillcpm`.`ProcessInvoiceUpdation`
/*!50003 DROP PROCEDURE IF EXISTS `ProcessInvoiceUpdation` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `ProcessInvoiceUpdation`(IN dynamic_limit INT, IN batch_size INT)
BEGIN
    DECLARE offset INT DEFAULT 0;
    
    WHILE offset < dynamic_limit DO
        
        CREATE TEMPORARY TABLE tmp_update AS
        SELECT t2.custpackrelid, t2.createdate, t2.startdate, t2.billdate, t2.enddate,t2.duedate,t2.latepaymentdate
        FROM savbillrevenuemanagement.tbltdebitdocument AS t2
        ORDER BY t2.custpackrelid DESC
        LIMIT batch_size OFFSET offset;

        
        UPDATE Savbillcpm.tbltdebitdocument AS t1
        JOIN tmp_update AS tmp
        ON t1.custpackrelid = tmp.custpackrelid
        SET t1.billdate = tmp.billdate,
            t1.createdate = tmp.createdate,
            t1.startdate = tmp.startdate,
            t1.enddate = tmp.enddate,
            t1.duedate = tmp.duedate,
            t1.latepaymentdate = tmp.latepaymentdate;

        
        DROP TEMPORARY TABLE IF EXISTS tmp_update;

        
        SET offset = offset + batch_size;
    END WHILE;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
-- end procedure `savbillcpm`.`ProcessInvoiceUpdation`

-- begin procedure `savbillcpm`.`Customer`
/*!50003 DROP PROCEDURE IF EXISTS `Customer` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `Customer`(IN dynamic_limit INT, IN batch_size INT)
BEGIN     DECLARE offset INT DEFAULT 0;     DECLARE done INT DEFAULT FALSE;     DECLARE v_cui VARCHAR(200);     DECLARE v_createdate DATETIME;     DECLARE v_lastlogin DATETIME;     DECLARE v_firstactivationdate DATETIME;     DECLARE v_custid INT;     DECLARE cur CURSOR FOR         SELECT accountnumber, custid, firstactivationdate, createdate, last_login_time         FROM Savbillcpm.tblcustomers         ORDER BY custid DESC         LIMIT dynamic_limit OFFSET offset;     DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;     OPEN cur;     read_loop: LOOP         FETCH cur INTO v_cui, v_custid, v_firstactivationdate, v_createdate, v_lastlogin;         IF done THEN             LEAVE read_loop;         END IF;         UPDATE savbillradius.tblcustomers         SET accountnumber = v_cui,             firstactivationdate = v_firstactivationdate,             last_login_time = v_lastlogin,             createdate = v_createdate         WHERE custid = v_custid;         SET offset = offset + batch_size;     END LOOP;     CLOSE cur; END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
-- end procedure `savbillcpm`.`Customer`

-- begin procedure `savbillcpm`.`ProcessCustPackage`
/*!50003 DROP PROCEDURE IF EXISTS `ProcessCustPackage` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `ProcessCustPackage`(IN dynamic_limit INT, IN batch_size INT)
BEGIN
    DECLARE offset INT DEFAULT 0;
    
    WHILE offset < dynamic_limit DO
        
        CREATE TEMPORARY TABLE tmp_update AS
        SELECT t2.custpackageid, t2.createdate, t2.startdate, t2.enddate, t2.expirydate
        FROM Savbillcpm.tblcustpackagerel AS t2
        ORDER BY t2.custpackageid DESC
        LIMIT batch_size OFFSET offset;

        
        UPDATE savbillradius.tblcustpackagerel AS t1
        JOIN tmp_update AS tmp
        ON t1.custpackageid = tmp.custpackageid
        SET t1.createdate = tmp.createdate,
            t1.startdate = tmp.startdate,
            t1.enddate = tmp.enddate,
            t1.expirydate = tmp.expirydate;

        
        DROP TEMPORARY TABLE IF EXISTS tmp_update;

        
        SET offset = offset + batch_size;
    END WHILE;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
-- end procedure `savbillcpm`.`ProcessCustPackage`

-- begin procedure `savbillcpm`.`get_workflow_in_process_data`
/*!50003 DROP PROCEDURE IF EXISTS `get_workflow_in_process_data` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`Savbillcpm`@`%` PROCEDURE `get_workflow_in_process_data`(IN mvnoid INT)
BEGIN
    
    CREATE TEMPORARY TABLE IF NOT EXISTS temp_entity_assignments (
        entity_id INT,
        entityname VARCHAR(255),
        tablename VARCHAR(255),
        assign_to_team_or_staff VARCHAR(255),
        staff_name varchar(255),
        workflow varchar(255)
    );

    
            INSERT INTO temp_entity_assignments
            SELECT lead_master_id AS entity_id, savbillsalesscrms.tblmleadmaster.username AS entityname, 'tblmleadmaster' AS tablename, 'Assign to Staff' AS assign_to_team_or_staff, tblstaffuser.username  as staff_name, "Lead WorkFlow" as workflow
            FROM savbillsalesscrms.tblmleadmaster
                     inner join tblstaffuser on savbillsalesscrms.tblmleadmaster.next_approve_staff_id = tblstaffuser.staffid
            WHERE ((lead_status != 'Converted') and (next_approve_staff_id IS NOT NULL OR next_team_mapping_id IS NOT NULL)) AND savbillsalesscrms.tblmleadmaster.mvnoId = mvnoid
                LIMIT 1;

            INSERT INTO temp_entity_assignments
            SELECT custid AS entity_id, custname AS entityname, 'tblcustomers' AS tablename, 'Assign to Staff' AS assign_to_team_or_staff,tblstaffuser.username  as staff_name, "CAF WorkFlow" as workflow
            FROM tblcustomers
                     inner join tblstaffuser on tblcustomers.current_assignee_id = tblstaffuser.staffid
            WHERE (current_assignee_id IS NOT NULL OR next_team_hir_mapping IS NOT NULL) AND tblcustomers.MVNOID = mvnoid
                LIMIT 1;

            INSERT INTO temp_entity_assignments
            SELECT tblcustapprove.custid AS entity_id, customer_name AS entityname, 'tblcustapprove' AS tablename, 'Assign to Staff' AS assign_to_team_or_staff, parent_staff as staff_name, "Customer Termination WorkFlow" as workflow
            FROM tblcustapprove
                     INNER JOIN tblcustomers ON tblcustapprove.custid = tblcustomers.custid
            WHERE tblcustomers.MVNOID = mvnoid AND status = 'pending'
                LIMIT 1;

            INSERT INTO temp_entity_assignments
            SELECT case_id AS entity_id, case_number AS entityname, 'tblcases' AS tablename, 'Assign to Staff' AS assign_to_team_or_staff, tblstaffuser.username as staff_name, "Ticket WorkFlow" as workflow
            FROM savbillticketmanagement.tblcases
                     inner join tblstaffuser on savbillticketmanagement.tblcases.current_assignee_id = tblstaffuser.staffid
            WHERE (current_assignee_id IS NOT NULL OR team_hir_mapping_id IS NOT NULL) AND savbillticketmanagement.tblcases.MVNOID = mvnoid
                LIMIT 1;

            INSERT INTO temp_entity_assignments
            SELECT POSTPAIDPLANID AS entity_id, name AS entityname, 'tblmpostpaidplan' AS tablename, 'Assign to Staff' AS assign_to_team_or_staff,tblstaffuser.username as staff_name, "Plan WorkFlow" as workflow
            FROM tblmpostpaidplan
                     inner join tblstaffuser on  tblmpostpaidplan.next_staff  = tblstaffuser.staffid
            WHERE (next_staff IS NOT NULL OR next_team_hir_mapping IS NOT NULL) AND tblmpostpaidplan.MVNOID = mvnoid
                LIMIT 1;

            INSERT INTO temp_entity_assignments
            SELECT custpack_id AS entityid, connection_no AS entityname, 'tbltcustomerinventorymapping' AS tablename, 'Assign to Staff' AS assign_to_team_or_staff, tblstaffuser.username as staff_name, "Inventory WorkFlow" as workflow
            FROM savbillinventorymanagement.tbltcustomerinventorymapping
                     inner join tblstaffuser on savbillinventorymanagement.tbltcustomerinventorymapping.next_approver = tblstaffuser.staffid
            WHERE next_approver IS NOT NULL AND mvno_id = mvnoid
                LIMIT 1;

            INSERT INTO temp_entity_assignments
            SELECT CREDITDOCID AS entityid, referenceno AS entityname, 'tbltcreditdoc' AS tablename, 'Assign to Staff' AS assign_to_team_or_staff, tblstaffuser.username as staff_name, "CreditNote WorkFlow" as workflow
            FROM tbltcreditdoc
                     inner join tblstaffuser on tbltcreditdoc.APPROVEDBYSTAFFID = tblstaffuser.staffid
            WHERE (APPROVEDBYSTAFFID IS NOT NULL OR next_team_hir_mapping IS NOT NULL) AND tbltcreditdoc.MVNOID = mvnoid and paytype ="creditnote"
                LIMIT 1;

            INSERT INTO temp_entity_assignments
            SELECT CREDITDOCID AS entityid, referenceno AS entityname, 'tbltcreditdoc' AS tablename, 'Assign to Staff' AS assign_to_team_or_staff, tblstaffuser.username as staff_name, "Payment Workflow" as workflow
            FROM tbltcreditdoc
                     inner join tblstaffuser on tbltcreditdoc.APPROVEDBYSTAFFID = tblstaffuser.staffid
            WHERE (APPROVEDBYSTAFFID IS NOT NULL OR next_team_hir_mapping IS NOT NULL) AND tbltcreditdoc.MVNOID = mvnoid and paytype <> "creditnote"
                LIMIT 1;

            INSERT INTO temp_entity_assignments
            SELECT id AS entityid, connection_no AS entityname, 'tbltcustomerservicemapping' AS tablename, 'Assign to Staff' AS assign_to_team_or_staff, tblstaffuser.username as staff_name, "Service workflow" as workflow
            FROM tbltcustomerservicemapping
                     INNER JOIN tblcustomers ON tbltcustomerservicemapping.custid = tblcustomers.custid
                     inner join tblstaffuser on tbltcustomerservicemapping.next_staff  = tblstaffuser.staffid
            WHERE (next_staff IS NOT NULL OR tbltcustomerservicemapping.next_team_hir_mapping IS NOT NULL) AND tblcustomers.MVNOID = 3 and discount_flow_in_process ="no" or discount_flow_in_process = null
                LIMIT 1;

            INSERT INTO temp_entity_assignments
            SELECT plangroupid AS entityid, plangroupname AS entityname, 'tblmplangroup' AS tablename, 'Assign to Staff' AS assign_to_team_or_staff, tblstaffuser.username as staff_name, "PlanGroup WorkFlow" as workflow
            FROM tblmplangroup
                     inner join tblstaffuser on tblmplangroup.next_staff  = tblstaffuser.staffid
            WHERE (next_staff IS NOT NULL OR next_team_hir_mapping IS NOT NULL) AND tblmplangroup.MVNOID = mvnoid
                LIMIT 1;

            INSERT INTO temp_entity_assignments
            SELECT debitdocumentid AS entityid, debitdocumentnumber AS entityname, 'tbltdebitdocument' AS tablename, 'Assign to Staff' AS assign_to_team_or_staff, tblstaffuser.username as staff_name, "Invoice WorkFlow" as workflow
            FROM tbltdebitdocument
                     inner join tblstaffuser on tbltdebitdocument.next_staff = tblstaffuser.staffid
                     INNER JOIN tblcustomers ON tbltdebitdocument.subscriberid = tblcustomers.custid
            WHERE (next_staff IS NOT NULL OR next_team_hir_mapping_id IS NOT NULL) AND tblcustomers.MVNOID = mvnoid
                LIMIT 1;

            







            INSERT INTO temp_entity_assignments
            SELECT ADDRESSID AS entityid, landmark AS entityname, 'tblmsubscriberaddressrel' AS tablename, 'Assign to Staff' AS assign_to_team_or_staff, tblstaffuser.username as staff_name, "ShiftLocation WorkFlow" as workflow
            FROM tblmsubscriberaddressrel
                     inner join tblstaffuser on tblmsubscriberaddressrel.next_staff = tblstaffuser.staffid
                     INNER JOIN tblcustomers ON tblmsubscriberaddressrel.SUBSCRIBERID = tblcustomers.custid
            WHERE (next_staff IS NOT NULL or  tblmsubscriberaddressrel.next_team_hir_mapping IS NOT NULL) AND tblcustomers.MVNOID = mvnoid
                LIMIT 1;

            INSERT INTO temp_entity_assignments
            SELECT doc_id AS entityid, doc_type AS entityname, 'tblcustdocdetails' AS tablename, 'Assign to Staff' AS assign_to_team_or_staff, tblstaffuser.username as staff_name,"Document Verification WorkFlow" as workflow
            FROM tblcustdocdetails
                     INNER JOIN tblcustomers ON tblcustdocdetails.cust_id = tblcustomers.custid
                     inner join tblstaffuser on tblcustdocdetails.next_staff = tblstaffuser.staffid
            WHERE (next_staff IS NOT NULL OR tblcustdocdetails.next_team_hir_mapping IS NOT NULL) AND tblcustomers.MVNOID = mvnoid
                LIMIT 1;

            INSERT INTO temp_entity_assignments
            SELECT CUSTSPPLANID AS entityid, mapping_name AS entityname, 'tblmcustspecialplanrelmapping' AS tablename, 'Assign to Staff' AS assign_to_team_or_staff,tblstaffuser.username as staff_name, "Special Plan Mapping WorkFlow" as workflow
            FROM tblmcustspecialplanrelmapping
                     inner join tblstaffuser on tblmcustspecialplanrelmapping.next_staff = tblstaffuser.staffid
            WHERE (next_staff IS NOT NULL OR next_team_hir_mapping IS NOT NULL) AND tblmcustspecialplanrelmapping.MVNOID = 3
                LIMIT 1;

            INSERT INTO temp_entity_assignments
            SELECT entity_id AS entityid, event_name AS entityname, 'tblworkflowassignstaffmapping' AS tablename, 'Assign to Team' AS assign_to_team_or_staff, tblstaffuser.username as staff_name, concat(event_name,' WorkFlow') as workflow
            FROM tblworkflowassignstaffmapping
                     INNER JOIN tblstaffuser ON tblworkflowassignstaffmapping.staff_id = tblstaffuser.staffid
            WHERE id IS NOT NULL AND tblstaffuser.MVNOID = mvnoid
                LIMIT 1;

            INSERT INTO temp_entity_assignments
            SELECT ticket_id AS entityid, 'Ticket' AS entityname, 'tblmticketassignstaffmapping' AS tablename, 'Assign to Team' AS assign_to_team_or_staff, tblstaffuser.username as staff_name , "Ticket WorkFlow " as workflow
            FROM savbillticketmanagement.tblmticketassignstaffmapping
                     INNER JOIN savbillticketmanagement.tblcases ON savbillticketmanagement.tblmticketassignstaffmapping.ticket_id = savbillticketmanagement.tblcases.case_id
                     inner join tblstaffuser on savbillticketmanagement.tblmticketassignstaffmapping.staff_id = tblstaffuser.staffid
            WHERE id IS NOT NULL AND savbillticketmanagement.tblcases.MVNOID = mvnoid
                LIMIT 1;

            
            SELECT * FROM temp_entity_assignments;
            END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
-- end procedure `savbillcpm`.`get_workflow_in_process_data`

