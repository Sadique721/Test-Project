package com.savbill.revenuemanagement.productmanagement.Discount.domain;

import com.savbill.revenuemanagement.productmanagement.Discount.dto.DiscountPlanMappingPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "TBLMDISCOUNTPOSTPAIDPLANREL")
public class DiscountPlanMapping {
	
	
	/*
CREATE TABLE TBLMDISCOUNTPOSTPAIDPLANREL
  (
    DISCOUNTPLANRELID serial,
  	DISCOUNTID     bigint UNSIGNED,
    POSTPAIDPLANID bigint UNSIGNED,
    PRIMARY KEY (DISCOUNTPLANRELID),
    FOREIGN KEY (DISCOUNTID) REFERENCES TBLMDISCOUNT (DISCOUNTID),
    FOREIGN KEY (POSTPAIDPLANID) REFERENCES TBLMPOSTPAIDPLAN (POSTPAIDPLANID)
  );
 
	 */

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISCOUNTPLANRELID", nullable = false, length = 40)
    private Integer id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "DISCOUNTID")
    private Discount discount;

    @Column(name = "POSTPAIDPLANID", nullable = false, length = 40)
    private Integer planId;

    public DiscountPlanMapping() {
    }

    public DiscountPlanMapping(DiscountPlanMappingPojo discountPlanMappingPojo, Discount discount) {
        this.id = discountPlanMappingPojo.getId();
        this.discount = discount;
        this.planId = discountPlanMappingPojo.getPlanId();
    }


}
