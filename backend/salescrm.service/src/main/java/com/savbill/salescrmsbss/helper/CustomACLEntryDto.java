package com.savbill.salescrmsbss.helper;

import lombok.Data;

@Data
public class CustomACLEntryDto {

	private Integer id;

    private int classid;
        
	private Integer roleId;	
    
    private int permit;
}
