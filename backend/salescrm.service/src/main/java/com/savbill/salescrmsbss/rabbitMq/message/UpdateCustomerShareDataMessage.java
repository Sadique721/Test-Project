package com.savbill.salescrmsbss.rabbitMq.message;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateCustomerShareDataMessage {
    private Integer id;
    private String title;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String custname;
    private String email;
    private String mobile;
    private String countryCode;
    private Integer serviceAreaId;
    private Integer networkdevicesId;
    private String status;
    private String custtype;
    private String phone;
    private Integer mvnoId;
    private Long buId;
    private Integer lcoId;
    private Boolean is_from_pwc;
    private Boolean isDeleted;
    private Long oltslotid;
    private Long oltportid;
    private String fullName;
    private Integer parnterId;
    private String planPurchaseType;
    private String serviceAreaName;
    private String partnerName;
    private String calendarType;
    private String dunningCategory;
    private String parentCustUsername;
    private String feasibilityRequired;
    private String valleyType;
    private String customerArea;
    private String custcategory;
    private Integer parentCustId;
}
