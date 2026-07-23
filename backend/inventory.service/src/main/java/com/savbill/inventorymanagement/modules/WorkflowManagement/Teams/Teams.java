package com.savbill.inventorymanagement.modules.WorkflowManagement.Teams;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUser;
import com.savbill.inventorymanagement.modules.PartnerManagement.Partner;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tblmteams")
@EntityListeners(AuditableListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class Teams extends Auditable implements IBaseData<Long> {

	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "team_id")
	private Long id;

	@Column(name = "team_name")
	private String name;

	@Column(name = "team_status")
	private String status;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "tbltteamusermapping", joinColumns = @JoinColumn(name = "team_id"), inverseJoinColumns = @JoinColumn(name = "staffid"))
	@ToString.Exclude
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonManagedReference
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

	@Override
	public String toString() {
		return "Teams [id=" + id + "]";
	}

    public Teams(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
