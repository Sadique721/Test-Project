package com.savbill.revenuemanagement.PaymentTransfer;

import lombok.Data;

@Data
public class PaymentTransferDTO {

    public Integer fromParentCustomerId;

    public Integer fromChildCustomerId;

    public Integer toChildCustomerId;

    public  Integer toParentCustomerId;

    public Double amount;

    public Integer mainCustomerId;
}
