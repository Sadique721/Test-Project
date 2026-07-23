package com.savbill.salescrmsbss.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessUnitMessage {

	private Long id;

	private String buname;

	private String bucode;

	private String status;

	private Boolean isDeleted = false;

	private Integer mvnoId;

	private String planBindingType;
}
