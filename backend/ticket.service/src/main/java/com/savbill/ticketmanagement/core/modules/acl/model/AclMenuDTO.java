package com.savbill.ticketmanagement.core.modules.acl.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AclMenuDTO {


    private Long menuid;
    
	private String name;
	
	private String dispName;
	
	private List<AclClassDTO> submenu;
}
