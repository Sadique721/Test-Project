package com.savbill.salescrmsbss.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.salescrmsbss.entity.pojo.RecordPaymentPojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TBLTRECORDPAYMENT")
public class RecordPayment {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_payment_id", nullable = false, length = 40)
    private Integer id;
	
	private String referenceno;

    private LocalDate chequedate;

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
    
    public RecordPayment(RecordPaymentPojo recordPaymentPojo) {
    	this.id = recordPaymentPojo.getId();
    	this.referenceno = recordPaymentPojo.getReferenceno();
    	this.chequedate = recordPaymentPojo.getChequedate();
    	this.paymentdate = recordPaymentPojo.getPaymentdate();
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
