package com.savbill.revenuemanagement.productmanagement.Discount.domain;

import com.savbill.revenuemanagement.productmanagement.Discount.dto.DiscountMappingPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@ToString
@Table(name = "TBLMDISCOUNTFIELDMAPPING")
public class DiscountMapping {
	
	
	/*
CREATE TABLE TBLMDISCOUNTFIELDMAPPING
  (
    DISCOUNTFIELDMAPPINGID serial,
    VALIDFROM              TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    VALIDUPTO              TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    DISCOUNTTYPE           VARCHAR(16),
    DISCOUNT               NUMERIC(16,4),
    DISCOUNTID             bigint UNSIGNED,
    PRIMARY KEY (DISCOUNTFIELDMAPPINGID),
    FOREIGN KEY (DISCOUNTID) REFERENCES TBLMDISCOUNT (DISCOUNTID)
  );
 
	 */

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISCOUNTFIELDMAPPINGID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "VALIDFROM", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate validFrom;

    @Column(name = "VALIDUPTO", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate validUPTO;

    @Column(name = "DISCOUNTTYPE", nullable = false, length = 40)
    private String discountType;

    @Column(name = "DISCOUNT", nullable = false, length = 40)
    private Double amount;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "DISCOUNTID")
    private Discount discount;

    @Transient
    private String validFromString;
    @Transient
    private String validUPTOString;
    public DiscountMapping() {
    }
    public DiscountMapping(Integer id, String discountType, Double amount, String validFromString, String validUPTOString, Discount discount) {
        this.id = id;
        this.discountType = discountType;
        this.amount = amount;
        this.validFromString = validFromString;
        this.validUPTOString = validUPTOString;
        this.discount = discount;
    }

    public DiscountMapping(DiscountMappingPojo discountMappingPojo, Discount discount) {
        this.id = discountMappingPojo.getId();
        this.validFrom = discountMappingPojo.getValidFrom();
        this.validUPTO = discountMappingPojo.getValidUpto();
        this.discountType = discountMappingPojo.getDiscountType();
        this.amount = discountMappingPojo.getAmount();
        this.discount = discount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidUPTO() {
        return validUPTO;
    }

    public void setValidUPTO(LocalDate validUPTO) {
        this.validUPTO = validUPTO;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }


}
