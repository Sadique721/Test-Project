package com.savbill.salescrmsbss.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
//@Data
@Entity
@Table(name="tblmfields")
public class Fields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "fieldname", nullable = false)
    private String fieldname;

    @Column(name = "name",length = 40)
    private String name;
    @Column(name = "data_type")
    private String dataType;

    @Transient
    private String defaultValue;

    @Transient
    private Boolean mandatoryFlag;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFieldname() {
		return fieldname;
	}

	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Boolean getMandatoryFlag() {
		return mandatoryFlag;
	}

	public void setMandatoryFlag(Boolean mandatoryFlag) {
		this.mandatoryFlag = mandatoryFlag;
	}
    
}
