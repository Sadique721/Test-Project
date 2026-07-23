package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tblaclentry")
public class CustomACLEntry {

	@Id
	@Column(name = "aclid", nullable = false, length = 40)
	private Integer id;

	@Column(nullable = false)
	private int classid;

	@ManyToOne
	@JoinColumn(name = "roleid")
	@ToString.Exclude
	private Role role;

	@Column(nullable = false)
	private int permit;
	

}
