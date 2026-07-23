package com.savbill.salescrmsbss.rabbitMq.message;

import com.savbill.salescrmsbss.entity.Partner;

import lombok.Data;

@Data
public class PartnerMessage {

	private Integer id;

    private String name;

    private String status;

    private String commtype;

    private Double commrelvalue;

    private Double balance;

    private Integer commdueday;

    private String nextbilldate;

    private String lastbilldate;

    private Integer taxid;

    private String addresstype;

    private String address1;

    private String address2;

    private Integer city;

    private Integer state;

    private Integer country;

    private String pincode;

    private String mobile;

    private String countryCode;

    private String email;

    private Partner parentPartner;

    private Long priceBookId;

    private Boolean isDelete;
    
    private Integer mvnoId;
    
    private String commissionShareType;

    private Long buId;

    private Long newCustomerCount;

    private Long renewCustomerCount;

    private Long totalCustomerCount;
}
