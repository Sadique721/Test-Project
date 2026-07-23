package com.savbill.radius.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TerminateUser 
{
	private String userName;
	private Long mvnoId;
}
