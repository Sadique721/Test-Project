package com.savbill.radius.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class changeUserData 
{
	private String userName;
	private Long mvnoId;
	private Long cprId;


	public changeUserData(String userName, Long mvnoId) {
		this.userName = userName;
		this.mvnoId = mvnoId;
		this.cprId = null;
	}
}
