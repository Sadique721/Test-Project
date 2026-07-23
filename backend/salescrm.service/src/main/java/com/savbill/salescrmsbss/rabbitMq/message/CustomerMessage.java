package com.savbill.salescrmsbss.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerMessage {

	private Integer id;

	private String title;

	private String username;

	private String password;

	private String firstname;

	private String lastname;

	private String status;

	private Boolean isDeleted;
	private Integer mvnoId;
	private Integer buId;
}
