package com.savbill.integrationsystem.rms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ProductCategoryDto {

    private Long id;
    private String name;
    private String unit;
    private String type;
    private String status;
    private Long mvnoId;
    private Boolean isDeleted = false;
    private boolean hasMac;
    private boolean hasSerial;
    private String productId;
    private boolean hasTrackable;
    private boolean hasPort;
    private boolean hasCas=false;
    private String dtvCategory;

    @JsonIgnore
    public Long getIdentityKey() {
        return id;
    }

    public Long getMvnoId() {
        return mvnoId;
    }

    public void setMvnoId(Long mvnoId) {
        this.mvnoId = mvnoId;
    }
}
