DELIMITER $$

USE `Savbillcpm` $$

DROP PROCEDURE IF EXISTS update_record_payment_backdate $$
CREATE DEFINER=`root`@`%` procedure savbillcpm.update_record_payment_backdate()
BEGIN
   
	DECLARE varReferenceNumber varchar(100);
	DECLARE varUserName varchar(100);
	DECLARE varPaymentDate DATETIME;	
	DECLARE done INT DEFAULT FALSE;
	DECLARE msg varchar(1000);	

	DECLARE cursor_i CURSOR FOR SELECT t.username,t1.paymentdate, t1.referencenumber FROM tblcustomers t join temppaymentdetailsheet t1 on t1.customerusername=t.username;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
	DECLARE EXIT HANDLER FOR sqlexception
    BEGIN
        SHOW ERRORS;
        ROLLBACK;   
    END; 
    CALL resetLog();
	OPEN cursor_i;
	read_loop: LOOP
		FETCH cursor_i INTO varUserName,varPaymentDate,varReferenceNumber;
		IF done THEN
			LEAVE read_loop;
		END IF;
		
		START TRANSACTION;
					
		UPDATE savbillcpm.tbltcreditdoc SET PAYMENTDATE = varPaymentDate, CREATEDATE = varPaymentDate, LASTMODIFIEDDATE = varPaymentDate, PAYMENTREFERENCENO = varReferenceNumber WHERE referenceno IN (varReferenceNumber);		
		UPDATE savbillrevenuemanagement.tbltcreditdoc SET PAYMENTDATE = varPaymentDate, CREATEDATE = varPaymentDate, LASTMODIFIEDDATE = varPaymentDate, PAYMENTREFERENCENO = varReferenceNumber WHERE referenceno IN (varReferenceNumber);
		
		SELECT concat('varUserName=', varUserName,',','varPaymentDate=', varPaymentDate,',','varReferenceNumber=', varReferenceNumber)  into msg;			
		CALL doLog('Payment', msg);
	
		COMMIT;
	
	END LOOP;
	CLOSE cursor_i;
	SELECT concat('Update record payment procedure is completed');
END$$
DELIMITER ;
