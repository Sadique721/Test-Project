package com.savbill.salescrmsbss.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PopManagementMessage {

	private Long id;

	private String popName;

	private String latitude;

	private String longitude;

	private String status;

	private Boolean isDeleted;

	private Integer mvnoId;

}
