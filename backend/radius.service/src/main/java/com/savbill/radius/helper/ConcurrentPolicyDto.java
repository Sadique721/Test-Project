package com.savbill.radius.helper;

import com.savbill.radius.entity.ConcurrentPolicy;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConcurrentPolicyDto {

	private String name;
	private Long noOfConcurrentConnections;
	private String status;
	
	public ConcurrentPolicyDto(ConcurrentPolicy concurrentPolicy) {
		this.name = concurrentPolicy.getName();
		this.noOfConcurrentConnections = concurrentPolicy.getNoOfConcurrentConnections();
		this.status = concurrentPolicy.getStatus();
	}
}
