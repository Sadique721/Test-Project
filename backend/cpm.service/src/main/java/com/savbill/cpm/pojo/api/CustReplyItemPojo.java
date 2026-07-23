package com.savbill.cpm.pojo.api;

import lombok.Data;

@Data
public class CustReplyItemPojo {
    private Integer id;
    private Integer custid;
    private String attribute;
    private String attributevalue;
    private String tempid;
}
