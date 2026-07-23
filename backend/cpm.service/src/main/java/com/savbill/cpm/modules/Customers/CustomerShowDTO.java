package com.savbill.cpm.modules.Customers;

import com.savbill.cpm.pojo.api.CustNetworkDetailsDTO;
import lombok.Data;

@Data
public class CustomerShowDTO {

    private Integer id;

    private String username;

    private String firstname;

    private String lastname;

    private String status;

    private String acctno;

    private String serviceareaname;

    private String serviceArea;
    private String mobile;

    private String name;

    private String email;

    private Boolean connectivity;

    private Double outstanding;

    private String custtype;

    private String calendarType;

    private String ConnectionMode;

    private CustNetworkDetailsDTO networkDetails;

    private boolean isinvoicestop;








}
