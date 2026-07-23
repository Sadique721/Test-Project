package com.savbill.integrationsystem.nms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PotsCircuitpackPreprovisionData {
    private int nodeEdgePointCount;

    public PotsCircuitpackPreprovisionData(int nodeEdgePointCount) {
        this.nodeEdgePointCount = nodeEdgePointCount;
    }
}
