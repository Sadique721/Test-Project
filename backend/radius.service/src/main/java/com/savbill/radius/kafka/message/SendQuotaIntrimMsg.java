package com.savbill.radius.kafka.message;


import com.savbill.radius.dto.SendQuotaDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendQuotaIntrimMsg implements Serializable {
    private String messageId;
    private String message;
    private String sourceName;
    private Map<String, Object> quotaData;

    private static final String TITLE = "title";

    private static final String PLANID = "cprid";

    private static final String CURRENTSESSIONUSAGETIME = "currentsessionusagetime";

    private static final String CURRENTSESSIONUSAGEVOLUME = "currentsessionusagevolume";


    public SendQuotaIntrimMsg(SendQuotaDTO sendQuotaDTO){
        Map<String, Object> map = new HashMap<>();
        if(sendQuotaDTO.getCprId() != null) {
            map.put(PLANID, sendQuotaDTO.getCprId().toString());
        }
        if(sendQuotaDTO.getCurrentSessionUsageTime() != null) {
            map.put(CURRENTSESSIONUSAGETIME, sendQuotaDTO.getCurrentSessionUsageTime().toString());
        }
        if(sendQuotaDTO.getCurrentSessionUsageVolume() != null) {
            map.put(CURRENTSESSIONUSAGEVOLUME, sendQuotaDTO.getCurrentSessionUsageVolume().toString());
        }
       // this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Radius quota Intrim send to API Gateway";
        this.quotaData = map;
        this.sourceName = "RADIUS";
    }

}
