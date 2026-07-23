package com.savbill.integrationsystem.waveMoney;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class WaveMoneyRequest {

    private String secretKey;
    private String gatewayUrl;
    private String payload;
    private WaveMoneyPayload waveMoneyObject;
//    private String scheduleTime;

}
