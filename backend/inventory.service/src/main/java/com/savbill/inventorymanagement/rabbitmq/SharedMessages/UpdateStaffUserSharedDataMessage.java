package com.savbill.inventorymanagement.rabbitmq.SharedMessages;

import com.savbill.inventorymanagement.modules.Role.Role;
import com.savbill.inventorymanagement.modules.MasterManagement.BusinessUnit.BusinessUnit;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Teams.Teams;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class UpdateStaffUserSharedDataMessage {
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
    private List<Teams> teamsList = new ArrayList<>();
    private Integer mvnoId;
    private Long branchId;
    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();
    private List<BusinessUnit> businessUnitNameList = new ArrayList<>();
    private Integer createdById;
    private Integer lastModifiedById;
    private String email;
    private String phone;
}
