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
public class SendQuotaMsg implements Serializable {
    private String messageId;
    private String message;
    private String sourceName;
    private Map<String, Object> quotaData;

    private static final String TITLE = "title";

    private static final String PLANID = "cprid";

    private static final String CUSTQUOTAID = "custQuotaId";

    private static  final String TOTALQUOTA = "totalQuota";

    private static final String USEDQUOTA = "usedQuota";

    private static final String PERCENTAGE = "percentage";

    private static final String RESERVED_QUOTA = "reservequota";

    private static final String IS_CHUNK_AVAILABLE = "isChunkAvaibale";



    public SendQuotaMsg(SendQuotaDTO sendQuotaDTO){
        Map<String, Object> map = new HashMap<>();
        if(sendQuotaDTO.getCprId() != null) {
            map.put(PLANID, sendQuotaDTO.getCprId().toString());
        }
        if(sendQuotaDTO.getPercentage() != null) {
            map.put(PERCENTAGE, sendQuotaDTO.getPercentage().toString());
        }
        if(sendQuotaDTO.getUsedQuota() != null) {
            map.put(USEDQUOTA, sendQuotaDTO.getUsedQuota().toString());
        }
        if(sendQuotaDTO.getTotalQuota() != null) {
            map.put(TOTALQUOTA, sendQuotaDTO.getTotalQuota().toString());
        }
       // this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Radius quota send to API Gateway";
        this.quotaData = map;
        this.sourceName = "RADIUS";
    }

    public SendQuotaMsg(Double totalReservedQuota, boolean isChunkAvailable, Integer cprId) {
        Map<String, Object> map = new HashMap<>();
        map.put(PLANID, cprId);
        map.put(IS_CHUNK_AVAILABLE, isChunkAvailable);
        map.put(RESERVED_QUOTA, totalReservedQuota);
        this.messageId = UUID.randomUUID().toString();
        this.message = "Radius quota send to API Gateway";
        this.quotaData = map;
        this.sourceName = "RADIUS";
    }

}
