package com.savbill.cpm.rabbitMq.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class StaffForLeadMsg {

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private Map<String, Integer> leadMgmtData;

    private static final String SAVBILL_API_GATEWAY = "Savbill Api Gateway";

    public StaffForLeadMsg(Integer staffId, Integer leadId) {
        Map<String, Integer> map = new HashMap<>();
        map.put("staffId",staffId);
        map.put("leadId",leadId);

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "New staff created from Api Gateway";
        this.leadMgmtData = map;
        this.sourceName = SAVBILL_API_GATEWAY;

    }


}
