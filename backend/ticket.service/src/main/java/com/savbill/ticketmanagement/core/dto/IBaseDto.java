package com.savbill.ticketmanagement.core.dto;

public interface IBaseDto {
     Long getIdentityKey();
    
     Integer getMvnoId();

     void setMvnoId(Integer mvnoId);
     Long getBuId();

}
