package com.savbill.taskmanagement.core.modules.Teams.domain;


import com.savbill.taskmanagement.core.data.Auditable;
import com.savbill.taskmanagement.core.data.IBaseData;
import com.savbill.taskmanagement.core.modules.Partner.domain.Partner;
import com.savbill.taskmanagement.core.modules.common.AuditableListener;
import com.savbill.taskmanagement.core.modules.staffuser.domain.StaffUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tblteams")
@EntityListeners(AuditableListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Teams extends Auditable implements IBaseData<Long> {

	@Id
	@Column(name = "team_id")
	private Long id;

	@Column(name = "team_name")
	private String name;

	@Column(name = "team_status")
	private String status;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "tblteamusermapping", joinColumns = @JoinColumn(name = "team_id"), inverseJoinColumns = @JoinColumn(name = "staffid"))
	@ToString.Exclude
	@LazyCollection(LazyCollectionOption.FALSE)
	private Set<StaffUser> staffUser = new HashSet<>();

	@Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
	private Boolean isDeleted = false;

	@ManyToOne
	@JoinColumn(name = "partnerid")
	private Partner partner;
	
	@Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;
	
	@ManyToOne
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "parentteamid")
    private Teams parentTeams;
	
	@Transient
	private String cafStatus;

	@Column(name = "lcoid")
	private Integer lcoId;

	@Column(name = "teamtype")
	private String teamType;

	public Teams() {

	}

	@JsonIgnore
	@Override
	public Long getPrimaryKey() {
		return id;
	}

	@JsonIgnore
	@Override
	public void setDeleteFlag(boolean deleteFlag) {
		this.isDeleted = deleteFlag;
	}

	@JsonIgnore
	@Override
	public boolean getDeleteFlag() {
		return isDeleted;
	}
	
	public Teams getParentTeams() {
        if (parentTeams == null) {
            return null;
        } else {
            return parentTeams;
        }
    }

	public Teams(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public String toString() {
		return "Teams [id=" + id + "]";
	}
}
