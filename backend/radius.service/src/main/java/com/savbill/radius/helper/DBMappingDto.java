package com.savbill.radius.helper;

import com.savbill.radius.entity.DBMapping;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DBMappingDto {
	
	private Long mappingMasterId;
	private String radiusName;
	private String dbColumnName;
	private Integer mvnoId;
	
	public DBMappingDto(DBMapping mapping) {
		this.mappingMasterId = mapping.getMappingMasterId();
		this.radiusName = mapping.getRadiusName();
		this.dbColumnName = mapping.getDbColumnName();
	}
	
	

}
