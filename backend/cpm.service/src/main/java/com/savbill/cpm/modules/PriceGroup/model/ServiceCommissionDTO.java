package com.savbill.cpm.modules.PriceGroup.model;

import com.savbill.cpm.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
public class ServiceCommissionDTO implements IBaseDto {

    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private PriceBookDTO priceBook;

    private Long serviceId;

    private String serviceName;

    private Integer revenue_share_percentage;

    private Integer mvnoId;

    private Boolean isDeleted = false;

    private Double royaltyPercentage;

    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }
}
