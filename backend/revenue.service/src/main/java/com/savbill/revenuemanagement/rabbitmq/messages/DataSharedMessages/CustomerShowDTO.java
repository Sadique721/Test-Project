package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages;

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

    private String mobile;

    private String name;

    private String email;

    private Boolean connectivity;

    private Double outstanding;

    private String custtype;

    private String calendarType;

    private String ConnectionMode;

//    private CustNetworkDetailsDTO networkDetails;

    private boolean isinvoicestop;







}
