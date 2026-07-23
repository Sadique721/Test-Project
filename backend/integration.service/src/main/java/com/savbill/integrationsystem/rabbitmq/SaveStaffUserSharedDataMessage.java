package com.savbill.integrationsystem.rabbitmq;
import com.savbill.integrationsystem.billgen.entity.ServiceArea;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SaveStaffUserSharedDataMessage {
    private Integer id;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String status;
    private String last_login_time;
    private Integer partnerid;
    private Boolean isDelete = false;
    private ServiceArea servicearea;
    private Integer mvnoId;
    private Integer branchId;
    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();
    private Integer parentStaffId;
    private String email;
    private String phone;
    private Integer lcoId;
    private String countryCode;
    private Integer createdById;
    private Integer lastModifiedById;
    // Tacacs Related Fields
    private String tacacsAccessLevelGroup;

    List<ServiceArea> serviceAreasList = new ArrayList<>();

}
