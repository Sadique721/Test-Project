package com.savbill.salescrmsbss.rabbitMq.message;

import com.savbill.salescrmsbss.entity.State;

import lombok.Data;

@Data
public class CityMessage {

	private Integer id;

	private String name;

	private String status;

	private Integer countryId;

	private State state;

	private Boolean isDelete;

	private Integer mvnoId;
}
