package com.savbill.ticketmanagement.core.modules.acl.model;

import lombok.Data;

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
}
