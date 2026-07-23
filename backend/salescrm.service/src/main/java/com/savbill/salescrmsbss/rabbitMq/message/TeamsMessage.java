package com.savbill.salescrmsbss.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamsMessage {

	private Long id;

	private String name;

	private String status;

	private Boolean isDeleted;

	private Long partnerId;
	
    private Integer mvnoId;
	
    private Long parentTeamsId;

	private Integer lcoId;

}

