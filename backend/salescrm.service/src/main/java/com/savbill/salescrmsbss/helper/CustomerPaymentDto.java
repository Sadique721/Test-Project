package com.savbill.salescrmsbss.helper;

import com.savbill.salescrmsbss.entity.CustomerPayment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPaymentDto {

	private String orderId;

	private Integer leadId;

	private Double payment;

	private String status;

	private Long planId;

	public CustomerPaymentDto(CustomerPayment customerPayment) {
		setOrderId(customerPayment.getOrderId().toString());
		setPayment(customerPayment.getPayment());
		setLeadId(customerPayment.getLeadId());
		setStatus(customerPayment.getStatus());
	}

}
