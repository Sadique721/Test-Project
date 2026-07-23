package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.savbill.salescrmsbss.rabbitMq.message.TeamsMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tblteams")
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Teams {

	@Id
	@Column(name = "team_id")
	private Long id;

	@Column(name = "team_name")
	private String name;

	@Column(name = "team_status")
	private String status;

	@Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
	private Boolean isDeleted;

	@Column(name = "partnerid")
	private Long partnerId;
	
	@Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;
	
	@ManyToOne
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "parentteamid")
    private Teams parentTeams;

	@Column(name = "lcoid")
	private Integer lcoId;
	
	public Teams(TeamsMessage teamsMessage) {
		this.id = teamsMessage.getId();
		this.name = teamsMessage.getName();
		this.status = teamsMessage.getStatus();
		this.isDeleted = teamsMessage.getIsDeleted();
		this.partnerId = teamsMessage.getPartnerId();
		this.mvnoId = teamsMessage.getMvnoId();
		this.lcoId = teamsMessage.getLcoId();
		if(teamsMessage.getParentTeamsId() != null)
			this.parentTeams = new Teams(teamsMessage.getParentTeamsId());
	}

	public Teams(Long id) {
		this.id = id;
	}

}
