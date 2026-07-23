package com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage;

import com.savbill.ticketmanagement.core.modules.BusinessUnit.domain.BusinessUnit;
import com.savbill.ticketmanagement.core.modules.ServiceArea.domain.ServiceArea;
import com.savbill.ticketmanagement.core.modules.Teams.domain.Teams;
import com.savbill.ticketmanagement.core.modules.role.domain.Role;
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


    private Set<Teams> team = new HashSet<>();

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






