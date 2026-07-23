package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbltleadmastersequence")
public class LeadMasterSequence {

	@Id
    @Column(name = "lead_master_sequence_id")
	private String id;
}
