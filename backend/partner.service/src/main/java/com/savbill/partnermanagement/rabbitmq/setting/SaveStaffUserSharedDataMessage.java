package com.savbill.partnermanagement.rabbitmq.setting;

import com.savbill.partnermanagement.modules.MasterManagement.BusinessUnit.BusinessUnit;
import com.savbill.partnermanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.partnermanagement.modules.Role.Role;
import com.savbill.partnermanagement.modules.Teams.Teams;
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
    private Integer partnerid;
    private Set<Role> roles = new HashSet<>();
    private List<Teams> teamsList = new ArrayList<>();
    private Boolean isDelete = false;
    private String last_login_time;
    private Integer mvnoId;
    private Long branchId;
    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();
    private List<BusinessUnit> businessUnitNameList = new ArrayList<>();
    private Integer createdById;
    private Integer lastModifiedById;
    private String email;
    private String phone;
}
