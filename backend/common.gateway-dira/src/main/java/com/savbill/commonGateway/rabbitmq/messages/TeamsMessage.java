package com.savbill.commonGateway.rabbitmq.messages;


import com.savbill.commonGateway.moules.TeamsManagement.Teams.Teams;
import com.savbill.commonGateway.moules.TeamsManagement.Teams.TeamsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamsMessage {

	private Long id;

	private String name;

	private String status;

	private Boolean isDeleted;

	private Long partnerId;
	
    private Integer mvnoId;
	
    private Long parentTeamsId;

	private Integer lcoId;
	
	public TeamsMessage(TeamsDTO teamsDTO) {
		this.id = teamsDTO.getId();
		this.name = teamsDTO.getName();
		this.status = teamsDTO.getStatus();
		this.isDeleted = teamsDTO.getIsDeleted();
	    this.partnerId = teamsDTO.getPartnerid();
	    this.mvnoId = teamsDTO.getMvnoId();
	    this.parentTeamsId = teamsDTO.getParentteamid();
	    this.lcoId = teamsDTO.getLcoId();
	}
	
	public TeamsMessage(Teams teams) {
		this.id = teams.getId();
		this.name = teams.getName();
		this.status = teams.getStatus();
		this.isDeleted = teams.getIsDeleted();
//		if(teams.getPartner() != null)
//			this.partnerId = teams.getPartner().getId().longValue();
		this.mvnoId = teams.getMvnoId();
		if(teams.getParentTeams() != null)
			this.parentTeamsId = teams.getParentTeams().getId();
		this.lcoId = teams.getLcoId();
	}
}

