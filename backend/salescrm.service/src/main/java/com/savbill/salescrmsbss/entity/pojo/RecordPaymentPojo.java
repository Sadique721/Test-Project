package com.savbill.salescrmsbss.entity.pojo;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.savbill.salescrmsbss.entity.RecordPayment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordPaymentPojo {
	
    private Integer id;
	
	private String referenceno;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate chequedate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentdate;

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
    
    public RecordPaymentPojo(RecordPayment recordPayment) {
    	this.id = recordPayment.getId();
    	this.referenceno = recordPayment.getReferenceno();
    	this.chequedate = recordPayment.getChequedate();
    	this.paymentdate = recordPayment.getPaymentdate();
    	this.chequeno = recordPayment.getChequeno();
    	this.bank = recordPayment.getBank();
    	this.customerid = recordPayment.getCustomerid();
    	this.paymode = recordPayment.getPaymode();
    	this.amount = recordPayment.getAmount();
    	this.paymentreferenceno = recordPayment.getPaymentreferenceno();
    	this.remark = recordPayment.getRemark();
    	this.branch = recordPayment.getBranch();
    	this.invoiceId = recordPayment.getInvoiceId();
    	this.type = recordPayment.getType();
    	this.paytype = recordPayment.getPaytype();
    	this.mvnoId = recordPayment.getMvnoId();
    	this.buId = recordPayment.getBuId();
    }
}
