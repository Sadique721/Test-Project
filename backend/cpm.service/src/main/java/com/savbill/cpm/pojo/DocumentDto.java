package com.savbill.cpm.pojo;

import lombok.Data;

@Data
public class DocumentDto {
    private Long docId;
    private String documentNumber;
    private String documentType;
}
