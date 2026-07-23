package com.savbill.revenuemanagement.core.dto.invoice;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OnlinePaymentDTO {

    Double amount;
    String bank;
    Integer customerid;
    String paymode;
    String referenceno;
    String remark;
    String reciptNo;
    String type;
    String paytype;
    Integer tdsAmount;
    Integer abbsAmount;
    Integer invoiceId;
    String onlinesource;


}
