package com.savbill.radius.kafka.message;

import lombok.Data;

@Data
public class NasUpdateMessage {

    private String customerId;
    private String nasPort;
    private String framedIp;

    public NasUpdateMessage(String customerId ,String nasPort ,String framedIp) {
        this.customerId = customerId;
        this.nasPort = nasPort;
        this.framedIp = framedIp;
    }
}
