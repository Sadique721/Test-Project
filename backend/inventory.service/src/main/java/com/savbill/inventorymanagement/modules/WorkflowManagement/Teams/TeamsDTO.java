package com.savbill.inventorymanagement.modules.WorkflowManagement.Teams;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@ToString
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@AllArgsConstructor
@NoArgsConstructor
public class TeamsDTO extends Auditable implements IBaseDto {
    private Long id;
    @NotNull(message = "Please Enter Team Name")
    private String name;
    @NotNull(message = "Please Enter Status")
    private String status;
    private Boolean isDeleted = false;
    private Long partnerid;
    private String partnername;
    private Integer lcoId;

    private Set<Long> staffUserIds = new HashSet<>();
    private List<String> staffNameList = new ArrayList<>();
    
    private Long parentteamid;
    
    private String parentTeamName;

    private Integer mvnoId;
//
//    private Integer lcoId;
    private Long displayId;
    private String displayName;

    public TeamsDTO(long teamId, String teamName) {
        this.id = teamId;
        this.name = teamName;
    }
    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }


	@Override
	public Integer getMvnoId() {
		// TODO Auto-generated method stub
		return mvnoId;
	}

//    @Override
//    public Long getBuId() {
//        return null;
//    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                '}';
    }

}
