package com.savbill.inventorymanagement.rabbitmq.SharedMessages;

import com.savbill.inventorymanagement.modules.CustomerServiceMapping.CustomerServiceMapping;
import com.savbill.inventorymanagement.modules.CustPlanMapping.CustPlanMapppingPojo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SaveCustomerDataShareMessage {
    private Integer id;
    private String title;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String custname;
    private Long serviceAreaId;
    private Integer networkdevicesId;
    private String status;
    private String custtype;
    private Integer mvnoId;
    private Long buId;
    private Boolean isDeleted;
    private Long oltslotid;
    private Long oltportid;
    private String fullName;
    private Integer parnterId;
    private String serviceAreaName;
    private String partnerName;
    private String parentCustUsername;
    private Integer parentCustId;
    private Long popId;
    private Long oltId;
    private Long masterdbid;
    private Long splitterid;
    private String framedIp;
    private String ipPoolNameBind;
    private String nasPort;
    private List<CustPlanMapppingPojo> custPlanMapppingList = new ArrayList<>();
    CustPlanMapppingPojo custPlanMappping = new CustPlanMapppingPojo();
    private List<CustomerServiceMapping> customerServiceMappingList = new ArrayList<>();
    private Integer createdById;
    private Integer lastModifiedById;
    private String framedIpBind;
    private String blockNo;
}
