package com.savbill.partnermanagement.rabbitmq.setting;

import com.savbill.partnermanagement.modules.StaffUser.StaffUser;
import com.savbill.partnermanagement.modules.partner.entity.Partner;
import com.savbill.partnermanagement.modules.Teams.Teams;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class SaveTeamsSharedSharedData {


    private Long id;


    private String name;


    private String status;


    private Set<StaffUser> staffUser = new HashSet<>();


    private Boolean isDeleted = false;


    private Partner partner;


    private Integer mvnoId;


    private Teams parentTeams;

    private String cafStatus;


    private Integer lcoId;
    private Integer createdById;
    private Integer lastModifiedById;

}


