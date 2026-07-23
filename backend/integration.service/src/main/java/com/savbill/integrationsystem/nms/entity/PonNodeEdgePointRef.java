package com.savbill.integrationsystem.nms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PonNodeEdgePointRef {
    private String nodeName;
    private String ciSiPn;
    private String isLct;
}
