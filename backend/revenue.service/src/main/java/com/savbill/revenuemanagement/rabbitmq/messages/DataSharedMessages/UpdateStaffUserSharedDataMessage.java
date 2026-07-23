package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages;

import com.savbill.revenuemanagement.core.entity.role.domain.Role;
import com.savbill.revenuemanagement.core.entity.staff.Teams;
import com.savbill.revenuemanagement.mastermanagement.BusinessUnit.domain.BusinessUnit;
import com.savbill.revenuemanagement.mastermanagement.ServiceArea.domain.ServiceArea;

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
    private Integer partnerid;


    private Set<Role> roles = new HashSet<>();


    //private Set<Teams> team = new HashSet<>();
    
    private Boolean isDelete = false;
    private String last_login_time;
    private Integer parentStaffId;
    private String email;
    private String phone;
    private Integer lcoId;


    private ServiceArea servicearea;

   
    private BusinessUnit businessUnit;
    private Integer mvnoId;
    private Integer branchId;
    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();
    private List<BusinessUnit> businessUnitNameList = new ArrayList<>();
    private List<Teams> teamsList = new ArrayList<>();
}
