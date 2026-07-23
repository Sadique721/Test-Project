package com.savbill.salescrmsbss.rabbitMq.message;

import com.savbill.salescrmsbss.entity.pojo.RecordPaymentPojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordPaymentPojoMessage {

	private Integer id;

	private String referenceno;

	private String chequedate;

	private String paymentdate;

	private String chequeno;

	private String bank;

	private Integer customerid;

	private String paymode;

	private Double amount;

	private String paymentreferenceno;

	private String remark;

	private String branch;

	private Integer invoiceId;

	private String type;

	private String paytype;

	private Integer mvnoId;

	private Long buId;

	private String bankManagement;

	public RecordPaymentPojoMessage(RecordPaymentPojo recordPaymentPojo) {
		this.id = recordPaymentPojo.getId();
		this.referenceno = recordPaymentPojo.getReferenceno();
		if (recordPaymentPojo.getChequedate() != null)
			this.chequedate = recordPaymentPojo.getChequedate().toString();
		if (recordPaymentPojo.getPaymentdate() != null)
			this.chequedate = recordPaymentPojo.getPaymentdate().toString();
		this.chequeno = recordPaymentPojo.getChequeno();
		this.bank = recordPaymentPojo.getBank();
		this.customerid = recordPaymentPojo.getCustomerid();
		this.paymode = recordPaymentPojo.getPaymode();
		this.amount = recordPaymentPojo.getAmount();
		this.paymentreferenceno = recordPaymentPojo.getPaymentreferenceno();
		this.remark = recordPaymentPojo.getRemark();
		this.branch = recordPaymentPojo.getBranch();
		this.invoiceId = recordPaymentPojo.getInvoiceId();
		this.type = recordPaymentPojo.getType();
		this.paytype = recordPaymentPojo.getPaytype();
		this.mvnoId = recordPaymentPojo.getMvnoId();
		this.buId = recordPaymentPojo.getBuId();
	}
}
