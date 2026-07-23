package com.savbill.commonGateway.rabbitmq.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchMessage {

	private Long id;

	private String name;

	private String status;

	private Boolean isDeleted = false;
	private Integer mvnoId;

}
