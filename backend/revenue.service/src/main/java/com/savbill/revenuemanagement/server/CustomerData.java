package com.savbill.revenuemanagement.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public interface CustomerData {
    public Integer getId();
    public String getUsername();
    public Integer getIsLco();
    public Integer getMvnoId();
    public Integer getPartnerId();
    public String getEmail();
    public String getMobile();
    public String getCountryCode();
    public Long getBuId();
    public String getCusttype();
}
