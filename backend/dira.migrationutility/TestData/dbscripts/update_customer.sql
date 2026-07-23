DELIMITER $$

USE `Savbillcpm`$$
DROP PROCEDURE IF EXISTS `update_customer`$$
CREATE DEFINER=`root`@`%` PROCEDURE `Savbillcpm`.`update_customer`()
BEGIN
   
	DECLARE cprid INT;
	DECLARE varStartDate DATETIME;
	DECLARE done INT DEFAULT FALSE;
	DECLARE msg varchar(1000);

	DECLARE cursor_i CURSOR FOR SELECT t.custid,t1.CreateDate FROM tblcustomers t join tempcustdetailsheet t1 on t1.username=t.username;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
	DECLARE EXIT HANDLER FOR sqlexception
    BEGIN
        SHOW ERRORS;
        ROLLBACK;   
    END; 
    CALL resetLog();
	OPEN cursor_i;
	read_loop: LOOP
		FETCH cursor_i INTO cprid,varStartDate;
		IF done THEN
			LEAVE read_loop;
		END IF;
		
		START TRANSACTION;
	
		UPDATE Savbillcpm.tblcustpackagerel SET createdate=varCreateDate WHERE custid  IN (id);
		UPDATE Savbillcpm.tblcustomers SET createdate=varCreateDate WHERE custid  IN (id);
		
		SELECT concat('cprid=', cprid,',','startdate=', varStartDate) into msg;
		CALL doLog(msg);
	
		COMMIT;
	END LOOP;
	CLOSE cursor_i;
	SELECT concat('Update Customer procedure is completed');
END$$

DELIMITER ;