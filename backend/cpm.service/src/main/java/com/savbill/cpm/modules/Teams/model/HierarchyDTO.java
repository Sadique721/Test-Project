package com.savbill.cpm.modules.Teams.model;

import com.savbill.cpm.core.dto.IBaseDto2;
import com.savbill.cpm.modules.Teams.domain.TeamHierarchyMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HierarchyDTO  implements IBaseDto2 {


    private Long id;

    //private String  flowName;

    //private String status;

    private String hierarchyName;

    private String eventName;

    private List<TeamHierarchyMapping> teamHierarchyMappingList ;

    private Integer mvnoId;

    private Boolean isDeleted = false;

    private Long buId;

    private Integer lcoId;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }
}
