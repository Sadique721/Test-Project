package com.savbill.revenuemanagement.core.entity.ladger;

import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "TBLMCUSTLEDGER")
@EntityListeners(AuditableListener.class)
public class CustomerLedger extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTLEDGERID", nullable = false, length = 40)
    private Integer id;


    @Column(name = "TOTALDUE", nullable = false, length = 40)
    private Double totaldue = 0.0;

    @Column(name = "TOTALPAID", nullable = false, length = 40)
    private Double totalpaid = 0.0;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CUSTID")
    @ToString.Exclude
    private Customers customer;

	@Override
	public String toString() {
		return "CustomerLedger []";
	}
    
  
}
