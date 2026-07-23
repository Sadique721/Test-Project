package com.savbill.salescrmsbss.rabbitMq.message;

import java.util.ArrayList;
import java.util.List;

import com.savbill.salescrmsbss.entity.CustomACLEntry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleMessage {

	private Long id;

	private String rolename;
    
    private String status;

	private Boolean sysRole = false;
	
	private List<CustomACLEntry> aclEntryList = new ArrayList<>();
	
    private Integer mvnoId;
    
    private Boolean isDelete = false;
}
