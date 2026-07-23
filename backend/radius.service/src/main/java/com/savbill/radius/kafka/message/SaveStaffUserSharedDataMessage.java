package com.savbill.radius.kafka.message;


import com.savbill.radius.entity.BusinessUnit;
import com.savbill.radius.entity.Role;
import com.savbill.radius.entity.ServiceArea;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private Set<Role> roles = new HashSet<>();
    private Boolean isDelete = false;
    private ServiceArea servicearea;
    private BusinessUnit businessUnit;
    private Integer mvnoId;
    private Integer branchId;
    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();
    private List<BusinessUnit> businessUnitNameList = new ArrayList<>();
    private Integer parentStaffId;
    private String email;
    private String phone;
    private Integer lcoId;
    private String countryCode;
    private Integer createdById;
    private Integer lastModifiedById;
    private String tacacsAccessLevelGroup;
    List<ServiceArea> serviceAreasList = new ArrayList<>();
    public SaveStaffUserSharedDataMessage() {}
}
