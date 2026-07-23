package com.savbill.radius.entity;

import java.sql.Timestamp;

import javax.persistence.*;

import com.savbill.radius.helper.DBMappingMasterDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "TBLMDBMAPPINGMASTER")
@NoArgsConstructor
public class DBMappingMaster {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mappingmasterid")
    private Long mappingMasterId;
	
    @Column(name = "mappingname")
    private String name;

    @Column(name = "status")
    private String status;
    
    @ApiModelProperty(hidden = true)
	@Column (name="createdate")
	private Timestamp createdOn;

	@ApiModelProperty(hidden = true)
	@Column (name="lastmodificationdate")
	private Timestamp lastModifiedOn;

	@ApiModelProperty(hidden = true)
	@Column (name="mvnoid", nullable = false, updatable = false)
	private Integer mvnoId;

	public Integer getMvnoId() {
		return mvnoId;
	}

	public void setMvnoId(Integer mvnoId) {
		this.mvnoId = mvnoId;
	}

	public DBMappingMaster(DBMappingMasterDto mappingMasterDto) {
		this.name = mappingMasterDto.getName();
		this.status = mappingMasterDto.getStatus();
	}

}
