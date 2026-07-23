DELIMITER $$

USE `Savbillcpm`$$
DROP PROCEDURE IF EXISTS `update_customer_cpr_details`$$
CREATE DEFINER=`root`@`%` PROCEDURE `Savbillcpm`.`update_customer_cpr_details`()
BEGIN
   
	DECLARE cprid INT;
	DECLARE varStartDate DATETIME;
	DECLARE varEndDate DATETIME;
	DECLARE varDebitDocumentId INT;
	DECLARE done INT DEFAULT FALSE;
	DECLARE msg varchar(1000);

	DECLARE cursor_i CURSOR FOR SELECT t.custpackageid,t1.CreateDate,t1.EndDate FROM tblcustpackagerel t join tempcustdetailsheet t1 on t1.cprid=t.custpackageid;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
	DECLARE EXIT HANDLER FOR sqlexception
    BEGIN
        SHOW ERRORS;
        ROLLBACK;   
    END; 
    CALL resetLog();
	OPEN cursor_i;
	read_loop: LOOP
		FETCH cursor_i INTO cprid,varStartDate,varEndDate;
		IF done THEN
			LEAVE read_loop;
		END IF;
		
		START TRANSACTION;
	
		UPDATE Savbillcpm.tblcustpackagerel set startdate=varStartDate,enddate=varEndDate,expirydate=varEndDate WHERE custpackageid IN (cprid);
		UPDATE savbillradius.tblcustpackagerel set startdate=varStartDate,enddate=varEndDate,expirydate=varEndDate WHERE custpackageid IN (cprid);
		UPDATE savbillrevenuemanagement.tbltdebitdocument set billdate=varStartDate,createdate=varStartDate,startdate=varStartDate,enddate=varEndDate,duedate=varEndDate,latepaymentdate=varEndDate WHERE custpackrelid IN (cprid);
		
		SELECT debitdocumentid into varDebitDocumentId from savbillrevenuemanagement.tbltdebitdocument where custpackrelid IN (cprid);
		UPDATE savbillrevenuemanagement.tbltcustledgerdetails set createdate=varStartDate WHERE debitdocid IN (cprid);
		
		SELECT concat('cprid=', cprid,',','startdate=', varStartDate,',','enddate=', varEndDate,',','debitdocumentid=', varDebitDocumentId) into msg;
		CALL doLog(msg);
	
		COMMIT;
	END LOOP;
	CLOSE cursor_i;
	SELECT concat('Update Customer procedure is completed');
END$$

DELIMITER ;