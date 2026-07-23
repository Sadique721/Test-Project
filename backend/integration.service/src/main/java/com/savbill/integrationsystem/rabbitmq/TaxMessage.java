package com.savbill.integrationsystem.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxMessage {


    private Integer id;

    private String name;

    private String desc;

    private String taxtype;

    private String status;
    private Integer mvnoId;
    private Long buId;
//    private String ledgerId;
    private Boolean isDelete = false;


}
