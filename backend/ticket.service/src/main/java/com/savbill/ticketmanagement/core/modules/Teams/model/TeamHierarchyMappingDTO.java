package com.savbill.ticketmanagement.core.modules.Teams.model;



import com.savbill.ticketmanagement.core.dto.IBaseDto;
import lombok.Data;

import java.util.List;

@Data
public class TeamHierarchyMappingDTO implements IBaseDto {


    private Integer id;


    private Integer teamId;


    private Integer hierarchyId;


    private Boolean isDeleted = false;


    private Integer nextTeamId;


    private Integer orderNumber;

    private String teamAction;

    private String teamCondition;

    private  Integer tat_id;

    private List<QueryFieldDTO> queryFieldMappingList;


    @Override
    public Long getIdentityKey() {
        return null;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {

    }

    @Override
    public Long getBuId() {
        return null;
    }
}
