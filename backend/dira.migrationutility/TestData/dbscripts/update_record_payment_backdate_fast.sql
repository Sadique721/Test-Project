


CREATE INDEX idx_creditdoc_referenceno
ON savbillcpm.tbltcreditdoc(referenceno);

CREATE INDEX idx_creditdoc_rm_referenceno
ON savbillrevenuemanagement.tbltcreditdoc(referenceno);

CREATE INDEX idx_temp_referencenumber
ON temppaymentdetailsheet(referencenumber);

CREATE INDEX idx_customers_username
ON savbillcpm.tblcustomers(username);

CREATE INDEX idx_temp_customerusername
ON temppaymentdetailsheet(customerusername);








DELIMITER $$

DROP PROCEDURE IF EXISTS savbillcpm.update_record_payment_backdate_fast $$
CREATE PROCEDURE savbillcpm.update_record_payment_backdate_fast()
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SHOW ERRORS;
    END;

    START TRANSACTION;

    -- CPM Update
    UPDATE savbillcpm.tbltcreditdoc cd
    JOIN temppaymentdetailsheet tpd
        ON cd.referenceno = tpd.referencenumber
    JOIN savbillcpm.tblcustomers c
        ON c.username = tpd.customerusername
    SET
        cd.PAYMENTDATE        = tpd.paymentdate,
        cd.CREATEDATE         = tpd.paymentdate,
        cd.LASTMODIFIEDDATE   = tpd.paymentdate,
        cd.PAYMENTREFERENCENO = tpd.referencenumber;

    -- Revenue Management Update
    UPDATE savbillrevenuemanagement.tbltcreditdoc cd
    JOIN temppaymentdetailsheet tpd
        ON cd.referenceno = tpd.referencenumber
    JOIN savbillcpm.tblcustomers c
        ON c.username = tpd.customerusername
    SET
        cd.PAYMENTDATE        = tpd.paymentdate,
        cd.CREATEDATE         = tpd.paymentdate,
        cd.LASTMODIFIEDDATE   = tpd.paymentdate,
        cd.PAYMENTREFERENCENO = tpd.referencenumber;

    COMMIT;

    -- Logging (once)
    CALL doLog('Payment', 'Payment backdate update completed successfully');

END$$
DELIMITER ;




CALL savbillcpm.update_record_payment_backdate_fast();