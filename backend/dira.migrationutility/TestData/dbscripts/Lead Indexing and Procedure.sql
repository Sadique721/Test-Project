//Lead Primary key set

CREATE TABLE savbillcpm.templeaddetailsheet (
    Sno INT NOT NULL AUTO_INCREMENT,
    Title VARCHAR(250),
    Name VARCHAR(250),
    Phone VARCHAR(50),
    LeadCustomerSector VARCHAR(250),
    Servicearea VARCHAR(250),
    Branch VARCHAR(250),
    CustomerGender VARCHAR(50),
    LeadSource VARCHAR(250),
    Teams VARCHAR(250),
    AssigneeStaff VARCHAR(250),
    PrimaryIndex VARCHAR(250),
    MigrationStatus VARCHAR(250),
    MigrationDetail VARCHAR(500),
    leadMasterId INT,
    PRIMARY KEY (Sno)
);


Lead Procedure-

DELIMITER $$

CREATE DEFINER=`root`@`%` PROCEDURE `savbillcpm`.`update_lead_assignment_fast`()
BEGIN
    START TRANSACTION;

    -- 1️⃣ Update CPM Lead Master Table
    UPDATE savbillcpm.tblmleadmaster lm
    JOIN savbillcpm.templeaddetailsheet t
        ON lm.addparam1 = t.PrimaryIndex
    JOIN commongateway.tblmstaffuser su
        ON su.username = t.AssigneeStaff
    JOIN savbillcpm.tblteams tm
        ON tm.team_name = t.Teams
    SET
        lm.next_team_mapping_id = tm.team_id,
        lm.next_approve_staff_id = su.staffid;

    -- 2️⃣ Update Sales CRM Lead Master Table
    UPDATE savbillsalesscrms.tblmleadmaster lm
    JOIN savbillcpm.templeaddetailsheet t
        ON lm.addparam1 = t.PrimaryIndex
    JOIN commongateway.tblmstaffuser su
        ON su.username = t.AssigneeStaff
    JOIN savbillcpm.tblteams tm
        ON tm.team_name = t.Teams
    SET
        lm.next_team_mapping_id = tm.team_id,
        lm.next_approve_staff_id = su.staffid;

    COMMIT;

END$$

DELIMITER ;


//Call

CALL savbillcpm.update_lead_assignment_fast();

//Apply Constrain to addparam1

ALTER TABLE savbillsalesscrms.tblmleadmaster
MODIFY COLUMN addparam1 VARCHAR(255);

ALTER TABLE savbillsalesscrms.tblmleadmaster
ADD CONSTRAINT uq_addparam1 UNIQUE (addparam1);



//Remove Constrain to addparam1

ALTER TABLE savbillsalesscrms.tblmleadmaster
DROP INDEX uq_addparam1;



