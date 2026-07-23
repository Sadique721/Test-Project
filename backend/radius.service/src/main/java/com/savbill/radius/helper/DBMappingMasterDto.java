package com.savbill.radius.helper;

import java.util.List;

import com.savbill.radius.entity.DBMappingMaster;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DBMappingMasterDto {

    	private Long id;
	private String name;
	private String status;
	private List<DBMappingDto> dbMappingDtoList;
	
	public DBMappingMasterDto(DBMappingMaster mappingMaster) {
		this.name = mappingMaster.getName();
		this.status = mappingMaster.getStatus();
	}

}
