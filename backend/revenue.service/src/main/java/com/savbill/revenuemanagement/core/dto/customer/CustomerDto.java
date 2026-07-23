package com.savbill.revenuemanagement.core.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    private Integer id;
    private String username;
    private String firstname;
    private String lastname;
    private String custname;
    private String aadhar;
    private String email;
    private String mobile;
    private String phone;
    private String pan;
    private String acctno;
    private Double walletbalance;
    private List<CustPlanMapppingDto> planMappingList = new ArrayList<>();
    private LocalDateTime expiryDate;
    public CustomerDto(String acctno, Double walletbalance, String email, Integer id) {
        this.acctno = acctno;
        this.walletbalance = walletbalance;
        this.email = email;
        this.id = id;
    }

    public CustomerDto(String acctno, Double walletbalance, String email, Integer id, String firstname, String lastname) {
        this.acctno = acctno;
        this.walletbalance = walletbalance;
        this.email = email;
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
    }
    public CustomerDto(String acctno, Double walletbalance,  List<CustPlanMapppingDto> planMappingList,LocalDateTime expiryDate ) {
        this.acctno = acctno;
        this.walletbalance = walletbalance;
        this.planMappingList = planMappingList;
        this.expiryDate=expiryDate;
    }
}
