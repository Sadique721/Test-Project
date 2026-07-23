package com.savbill.inventorymanagement.modules.DebitDoc;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.modules.Customers.Customers;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "tblmdebitdocument")
@EntityListeners(AuditableListener.class)
public class DebitDocument extends Auditable {


	/*
create table TBLTDEBITDOCUMENT
(
	debitdocumentid serial,
	debitdocumentnumber varchar(200),
	subscriberid BIGINT UNSIGNED,
	billdate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	createdate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	startdate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	enddate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	duedate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	latepaymentdate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	subtotal NUMERIC(20,4) default 0,
	tax NUMERIC(20,4) default 0,
	discount NUMERIC(20,4) default 0,
	totalamount NUMERIC(20,4) default 0,
	previousbalance NUMERIC(20,4) default 0,
	latepaymentfee NUMERIC(20,4) default 0,
	currentpayment NUMERIC(20,4) default 0,
	currentdebit NUMERIC(20,4) default 0,
	currentcredit NUMERIC(20,4) default 0,
	totaldue NUMERIC(20,4) default 0,
	totalamountinwords varchar(200),
	totaldueinwords varchar(200),
	billrunid BIGINT UNSIGNED,
	billrunstatus varchar(200),
	xmldocument LONGTEXT,
	PRIMARY KEY (debitdocumentid ),
	FOREIGN KEY (subscriberid) REFERENCES tblcustomers (custid),
	FOREIGN KEY (billrunid) REFERENCES TBLMBILLRUN (billrunid)
);	 */

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "debitdocumentid", nullable = false, length = 40)
    private Integer id;
    @Column(name = "debitdocumentnumber", nullable = false, length = 40)
    private String docnumber;
    @JoinColumn(name = "custpackrelid")
    private Integer custpackrelid;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subscriberid")
    private Customers customer;
    @Column(name = "payment_status", nullable = false, length = 40)
    private String paymentStatus;
    @Column(name = "inventory_mapping_id")
    private Long inventoryMappingId;
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;
    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
    @Column(name = "status")
    private String  status;
    @Column(name = "billrunstatus", nullable = false, length = 40)
    private String billrunstatus;
}
