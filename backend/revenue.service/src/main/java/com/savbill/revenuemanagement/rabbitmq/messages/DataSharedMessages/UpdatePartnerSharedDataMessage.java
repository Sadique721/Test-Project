package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages;


import com.savbill.revenuemanagement.mastermanagement.ServiceArea.domain.ServiceArea;
import lombok.Data;

import java.util.List;

@Data
public class UpdatePartnerSharedDataMessage {

    private Integer id;
    private String name;
    private String prcode;
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
    private Double credit;
    private Integer city;
    private Integer state;
    private Integer country;
    private String pincode;
    private String mobile;
    private String countryCode;
    private String email;
    private String partnerType;
    private String cpName;
    private String cname;
    private String panName;
    private Boolean isDelete;
    private Integer mvnoId;
    private String commissionShareType;
    private Long buId;
    private Long newCustomerCount = 0L;
    private Long renewCustomerCount = 0L;
    private Long totalCustomerCount = 0L;
    private String calendarType;
    private String resetDate;
    private Double creditConsume = 0d;
    private Long region ;
    private Long branch ;
    private Long bussinessvertical ;
    private String dunningActivateFor;
    private String lastDunningDate;
    private Boolean isDunningEnable;
    private String dunningAction;
    private Integer parentPartnerId;
    private Integer createdById;
    private Integer lastModifiedById;
    private List<ServiceArea> serviceAreaList;

    private Long priceBookId;

    private List<Long> serviceAreaIds;

    private String commissionInterval;

    public UpdatePartnerSharedDataMessage(){}
}
