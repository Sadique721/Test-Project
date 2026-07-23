package com.savbill.radius.helper;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenDetail 
{
	public String firstName;
	public String lastName;
	public int userId;
	public int partnerId;
	public String rolesList;
	public Integer mvnoId;
	public List<Long> serviceAreaIdList;
	public int staffId;
}
