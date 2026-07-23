package com.savbill.revenuemanagement.autoassign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirtelAppToCRMDTO {
    private String customerMsisdn;
    private String merchantMsisdn;
    private String username;
    private String password;
    private String accountNo;
    private String customerName;
    private String customerReference;
    private String walletBalance;
    private String status;
    private Integer custId;
    private Integer mvnoId;
    private Integer buId;
    private String firstName;
    private String lastName;
    private String currencyCode;
    private String dueDate;
}
