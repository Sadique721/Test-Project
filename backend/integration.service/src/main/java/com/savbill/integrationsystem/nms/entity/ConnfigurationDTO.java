package com.savbill.integrationsystem.nms.entity;

import com.savbill.integrationsystem.core.dto.IBaseDto;

public class ConnfigurationDTO implements IBaseDto {
    private  Long id;
    private String name;

    private String baseurl;

    private Integer port;

    private Boolean isdeleted;

    private String username;

    private String password;

    @Override
    public Long getIdentityKey() {
        return null;
    }

    @Override
    public Long getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Long mvnoId) {

    }
}
