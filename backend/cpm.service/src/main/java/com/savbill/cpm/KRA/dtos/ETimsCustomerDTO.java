package com.savbill.cpm.KRA.dtos;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ETimsCustomerDTO {
    private String customerNo;
    private String customerTin;
    private String customerName;
    private String address;
    private String telNo;
    private String email;
    private String faxNo;
    private Boolean isUsed;
    private String remark;
    private Integer mvnoId;
}

