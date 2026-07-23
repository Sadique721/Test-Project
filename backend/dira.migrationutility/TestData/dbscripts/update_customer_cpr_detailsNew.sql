DELIMITER $$

USE `Savbillcpm` $$

DROP PROCEDURE IF EXISTS update_customer_cpr_details $$
CREATE DEFINER=`root`@`%` procedure savbillcpm.update_customer_cpr_details()
BEGIN
   
	DECLARE cprid INT;
	DECLARE varCustId INT;
	DECLARE varStartDate DATETIME;
	DECLARE varRenewedDate DATETIME;
	DECLARE varEndDate DATETIME;
	DECLARE varDebitDocumentId INT;
	DECLARE done INT DEFAULT FALSE;
	DECLARE msg varchar(1000);
	DECLARE varAccountNo varchar(100);
	DECLARE varUpdates varchar(100);
	DECLARE varCreatedBy INT DEFAULT 12;
	DECLARE varCreatedByName varchar(100) DEFAULT 'Mayur Patil';

	DECLARE cursor_i CURSOR FOR SELECT t.custpackageid,t.custid,t1.registered,t1.renewed,t1.expires,t1.AccountNo,t1.Updates FROM tblcustpackagerel t join tempcustdetailsoutput t1 on t1.cprid=t.custpackageid;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
	DECLARE EXIT HANDLER FOR sqlexception
    BEGIN
        SHOW ERRORS;
        ROLLBACK;   
    END; 
    CALL resetLog();
	OPEN cursor_i;
	read_loop: LOOP
		FETCH cursor_i INTO cprid,varCustId,varStartDate,varRenewedDate,varEndDate,varAccountNo,varUpdates;
		IF done THEN
			LEAVE read_loop;
		END IF;
		
		START TRANSACTION;
		
		UPDATE Savbillcpm.tblcustpackagerel set startdate=varRenewedDate,createdate=varRenewedDate,enddate=varEndDate,expirydate=varEndDate WHERE custpackageid IN (cprid);		
		UPDATE savbillradius.tblcustpackagerel set startdate=varStartDate,createdate=varStartDate,enddate=varEndDate,expirydate=varEndDate WHERE custpackageid IN (cprid);	
		
		UPDATE savbillrevenuemanagement.tbltdebitdocument set billdate=varRenewedDate,createdate=varRenewedDate,startdate=varRenewedDate,enddate=varEndDate,duedate=varEndDate,latepaymentdate=varEndDate WHERE custpackrelid IN (cprid);
		SELECT debitdocumentid into varDebitDocumentId from savbillrevenuemanagement.tbltdebitdocument where custpackrelid IN (cprid);
		UPDATE savbillrevenuemanagement.tbltcustledgerdetails set createdate=varStartDate WHERE debitdocid IN (cprid);
		
		UPDATE Savbillcpm.tblcustomers SET username = varAccountNo, password = varAccountNo,accountnumber = varAccountNo, createdate = varStartDate, firstactivationdate = varStartDate, lastmodifieddate = varStartDate, last_login_time = varStartDate WHERE custid in (varCustId);
	
		INSERT INTO Savbillcpm.tbltcustomernotes (notes, custid, created_on, created_by, created_by_name) VALUES (varUpdates, varCustId, varStartDate, varCreatedBy, varCreatedByName);
	
		SELECT concat('cprid=', cprid,',','varCustId=', varCustId,',','startdate=', varStartDate,',','enddate=', varEndDate,',','varAccountNo=', varAccountNo,',','varUpdates=', varUpdates,',','varCreatedBy=', varCreatedBy,',','varCreatedByName=', varCreatedByName)  into msg;
		CALL doLog('Customer', msg);
	
		COMMIT;
	END LOOP;
	CLOSE cursor_i;
	SELECT concat('Update Customer procedure is completed');
END$$
DELIMITER ;
