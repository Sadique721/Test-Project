package com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage;



import com.savbill.taskmanagement.core.modules.Partner.domain.Partner;
import com.savbill.taskmanagement.core.modules.Teams.domain.Teams;
import com.savbill.taskmanagement.core.modules.staffuser.domain.StaffUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateTeamsSharedData {


    private Long id;


    private String name;


    private String status;


    private Set<StaffUser> staffUser = new HashSet<>();


    private Boolean isDeleted = false;

    @JsonSerialize(using = PartnerSerializer.class)
    @JsonDeserialize(using = PartnerDeserializer.class)
    private Partner partner;


    private Integer mvnoId;


    private Teams parentTeams;

    private String cafStatus;


    private Integer lcoId;

    private String teamType;
}


