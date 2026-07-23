package com.savbill.integrationsystem.nms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EthCircuitpackPreprovisionData {
    private int nodeEdgePointCount;

    public EthCircuitpackPreprovisionData(int nodeEdgePointCount) {
        this.nodeEdgePointCount = nodeEdgePointCount;
    }
}
