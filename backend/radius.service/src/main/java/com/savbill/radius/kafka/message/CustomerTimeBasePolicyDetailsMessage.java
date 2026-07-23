package com.savbill.radius.kafka.message;

import com.savbill.radius.entity.CustQuotaDetails;
import com.savbill.radius.entity.CustomerTimeBasePolicyMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerTimeBasePolicyDetailsMessage {
    //TimeBasePolicy
    private static final String SAVBILL_API_GATEWAY = "SAVBILL_API_GATEWAY";
    private static final String ID = "id";
    private static final String FROMDAY = "fromday";
    private static final String TODAY = "today";
    private static final String FROMTIME = "fromtime";
    private static final String TOTIME = "totime";
    private static final String SPEED = "speed";
    private static final String ACCESS = "access";
    private static final String CUSTID = "custid";
    private static final String PLANID = "planid";
    private static final String QUOTADTLID = "quotadtlid";

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private String currentUser;

    private Map<String, Object> data;

    public CustomerTimeBasePolicyDetailsMessage(List<CustomerTimeBasePolicyMapping> timeBasePolicyDetailsDTO, List<CustQuotaDetails> custQuotaDtlsPojo) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < timeBasePolicyDetailsDTO.size(); i++) {
            map.put(ID, timeBasePolicyDetailsDTO.get(i).getId());
            map.put(FROMDAY, timeBasePolicyDetailsDTO.get(i).getFromDay());
            map.put(TODAY, timeBasePolicyDetailsDTO.get(i).getToDay());
            map.put(FROMTIME, timeBasePolicyDetailsDTO.get(i).getFromTime());
            map.put(TOTIME, timeBasePolicyDetailsDTO.get(i).getToTime());
            map.put(SPEED, timeBasePolicyDetailsDTO.get(i).getSpeed());
            map.put(ACCESS, timeBasePolicyDetailsDTO.get(i).getAccess());
            map.put(CUSTID, custQuotaDtlsPojo.get(i).getCustid());
            map.put(PLANID, custQuotaDtlsPojo.get(i).getPlanId());
            map.put(QUOTADTLID, custQuotaDtlsPojo.get(i).getId());
        }
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Timebasepolicy from Api Gateway";
        this.setData(map);
        //this.data = map;
        this.sourceName = SAVBILL_API_GATEWAY;
    }
}
