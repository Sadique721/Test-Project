package com.savbill.commonGateway.moules.acl.model;

import lombok.Data;

import javax.persistence.Column;

@Data
public class AclMenuDtoNew {
    private Long id;
    private String name;
    private String code;
    private Long parentid;
    private Boolean ismenu;
    private String icon;
    private String url;
    private Boolean isweb;
    private Boolean ismobile;
    private Boolean isSelected = false;
    private Boolean isDelete;
    private String product;
    private Long position;
}
