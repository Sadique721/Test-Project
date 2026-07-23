package com.savbill.inventorymanagement.modules.CustPlanMapping;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.modules.PlanGroup.PlanGroup;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@ToString
@Table(name = "tbltcustpackagerel")
@EntityListeners(AuditableListener.class)
public class CustPlanMappping extends Auditable {
	

	/*
CREATE TABLE TBLCUSTPACKAGEREL
(
	custpackageid SERIAL PRIMARY KEY,
	custid BIGINT UNSIGNED NOT NULL,
	planid BIGINT UNSIGNED NOT NULL,
	startdate timestamp not null,
	enddate timestamp not null,
	expirydate timestamp not null,
	status char(1),
	FOREIGN KEY(custid) REFERENCES tblcustomers(custid),
	FOREIGN KEY(planid) REFERENCES TBLMPOSTPAIDPLAN(postpaidplanid)
);
 
	 */

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custpackageid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "planid", nullable = false, length = 40)
    private Integer planId;

    @Column(nullable = false, length = 40)
    private String service;

    @Column(name = "startdate", length = 40)
    private LocalDateTime startDate;

    @Column(name = "enddate", length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endDate;

    @Column(name = "expirydate", length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime expiryDate;

    @Column(name = "status", length = 150)
    private String status;

//    @JsonBackReference
//    @ManyToOne
//    @JoinColumn(name = "custid")
//    private Customers customer;
    @Column(name = "custid")
    private Integer custId;
    @Column(name = "is_delete",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;
    @Column(name = "cust_plan_status")
    private String custPlanStatus;
    @Column(name = "custservicemappingid")
    private Integer custServiceMappingId;
    @OneToOne
    @JoinColumn(name = "plangroupid")
    private PlanGroup planGroup;
    @Column(name = "is_invoice_to_org")
    private Boolean isInvoiceToOrg;

    @Column(name = "bill_to")
    private String billTo;
    public CustPlanMappping() {
    }

    public CustPlanMappping(CustPlanMappping custPlanMappping) {
        this.expiryDate = custPlanMappping.getExpiryDate();
    }

//    private Long creditdocid;

    @Override
    public String toString() {
        return "CustPlanMappping{}";
    }
}
