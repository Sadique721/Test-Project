package com.savbill.integrationsystem.rabbitmq;

import lombok.Data;

@Data
public class PlanServiceMessage {
    private Integer id;

    private String name;

    private String icname;

    private String iccode;

    private Integer mvnoId;

    private Long buId;

    private Boolean isQoSV;

    private String expiry;

    private String ledgerId;

    private Boolean is_dtv;

    private Boolean installation_charge;

    private Boolean support_charge;

    private Boolean feasibility;

    private Boolean poc;

    private Boolean installation;

    private Boolean provisioning;
    private Boolean Price;
}
