package com.savbill.radius.entity;


import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.radius.helper.DBMappingDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "TBLTDBMAPPING")
@NoArgsConstructor
public class DBMapping {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mappingid")
    private Long mappingId;
	
	@Column(name = "mappingmasterid")
	private Long mappingMasterId;
	 
    @Column(name = "radiusname")
    private String radiusName;
    
    @Column(name = "dbcolumnname")
    private String dbColumnName;
    
    @ApiModelProperty(hidden = true)
   	@Column (name="createdate")
   	private Timestamp createdOn;

   	@ApiModelProperty(hidden = true)
   	@Column (name="lastmodificationdate")
   	private Timestamp lastModifiedOn;

   	@ApiModelProperty(hidden = true)
	@Column (name="mvnoid", nullable = false)
	private Integer mvnoId;

	public Integer getMvnoId() {
		return mvnoId;
	}

	public void setMvnoId(Integer mvnoId) {
		this.mvnoId = mvnoId;
	}

	public DBMapping( DBMappingDto mappingDto) {

		this.mappingMasterId = mappingDto.getMappingMasterId();
		this.radiusName = mappingDto.getRadiusName();
		this.dbColumnName = mappingDto.getDbColumnName();
	}
   	
   	
}
