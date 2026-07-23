-- Drop previous table if exists
DROP TABLE IF EXISTS temppaymentdetailsheet;

select * from savbillcpm.tbltcreditdoc t ;

-- Create the table
CREATE TABLE temppaymentdetailsheet (
    RowIndex INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    SubscriberType VARCHAR(50),
    CustomerUsername VARCHAR(100),
    DocumentNumber VARCHAR(100),
    PaymentMode VARCHAR(50),
    Source VARCHAR(100),
    Amount DECIMAL(18,2),
    TransactionDate DATETIME,
    PaymentDate DATETIME,
    FileNameToAttach VARCHAR(255),
    ChequeNumber VARCHAR(100),
    ChequeTransactionDate DATETIME,
    SourceBank VARCHAR(100),
    DestinationBank VARCHAR(100),
    Branch VARCHAR(100),
    ReferenceNumber VARCHAR(100),
    ReceiptNumber VARCHAR(100),
    TDS DECIMAL(18,2),
    ABBS VARCHAR(50),
    Remark VARCHAR(500),
    MigrationStatus VARCHAR(50),
    MigrationDetail VARCHAR(500),
    cprid INT
);


CREATE DEFINER=`root`@`%` PROCEDURE `savbillcpm`.`update_record_payment_backdate_fast`()
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
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

    -- Logging
    CALL doLog('Payment', 'Payment backdate update completed successfully');

END




CALL savbillcpm.update_record_payment_backdate_fast();