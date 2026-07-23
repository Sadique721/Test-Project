package com.savbill.salescrmsbss.rabbitMq.message;

import lombok.Data;

@Data
public class NetworkDevicesMessage {

	private Long id;

	private String name;

	private String devicetype;

	private String status;

	private String latitude;

	private String longitude;

	private Long serviceareaId;

	private Boolean isDeleted;

	private Integer mvnoId;

	private Integer totalInPorts;

	private Integer availableInPorts;

	private Integer totalOutPorts;

	private Integer availableOutPorts;

}
