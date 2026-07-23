package com.savbill.salescrmsbss.entity;

import javax.persistence.*;

import com.savbill.salescrmsbss.audit.Auditable;
import com.savbill.salescrmsbss.rabbitMq.message.RoleMessage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tblroles")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Role extends Auditable {

	@Id
	@Column(name = "roleid", nullable = false, length = 40)
	private Long id;

	@Column(nullable = false, length = 40)
	private String rolename;

	@Column(name = "rstatus", nullable = false, length = 100)
	private String status;

	@Column(name = "sysrole", columnDefinition = "Boolean default false")
	private Boolean sysRole;

	@Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
	private Boolean isDelete;

	@Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
	private Integer mvnoId;

	@CreationTimestamp
	@Column(name = "created_on", nullable = false, updatable = false)
	private LocalDateTime createdate;

	@UpdateTimestamp
	@Column(name = "lastmodified_on")
	private LocalDateTime updatedate;

	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@OrderBy("id asc")
	@JsonManagedReference
	private List<RoleACLEntry> roleAclEntry = new ArrayList<>();

	
	public Role(RoleMessage message) {
		this.id  = message.getId();
		this.rolename = message.getRolename();
		this.status = message.getStatus();
		this.sysRole = message.getSysRole();
		this.isDelete = message.getIsDelete();
		this.mvnoId = message.getMvnoId();
	}

	public Role(Long id) {
		this.id = id;
	}
}
