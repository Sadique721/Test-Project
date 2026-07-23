package com.savbill.cpm.pojo.api;

import org.springframework.format.annotation.DateTimeFormat;

import com.savbill.cpm.model.postpaid.DiscountMapping;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class DiscountMappingPojo {

    private Integer id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate validFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate validUpto;

    @NotNull
    private String discountType;

    @NotNull
    private Double amount;

    public DiscountMappingPojo() {
    }

    public DiscountMappingPojo(DiscountMapping discountMapping) {
        this.id = discountMapping.getId();
        this.validFrom = discountMapping.getValidFrom();
        this.validUpto = discountMapping.getValidUPTO();
        this.discountType = discountMapping.getDiscountType();
        this.amount = discountMapping.getAmount();
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

    public LocalDate getValidUpto() {
        return validUpto;
    }

    public void setValidUpto(LocalDate validUpto) {
        this.validUpto = validUpto;
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

    @Override
    public String toString() {
        return "DiscountMappingPojo [id=" + id + ", validFrom=" + validFrom + ", validUpto=" + validUpto
                + ", discountType=" + discountType + ", amount=" + amount + "]";
    }
}
