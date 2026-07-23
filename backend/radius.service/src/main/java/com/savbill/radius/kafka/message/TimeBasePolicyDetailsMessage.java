package com.savbill.radius.kafka.message;

import com.savbill.radius.kafka.CustomMessage;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = CustomMessage.class)
public class TimeBasePolicyDetailsMessage {
    private static final String SAVBILL_API_GATEWAY = "SAVBILL_API_GATEWAY";
    private static final String ID = "id";
    private static final String FROMDAY = "fromday";
    private static final String TODAY = "today";
    private static final String FROMTIME = "fromtime";
    private static final String TOTIME = "totime";
    private static final String SPEED = "speed";
    private static final String ACCESS = "access";
    private static final String QQSID = "qqsid";
    private static final String POLICY_ID = "policy_id";
    private static final String IS_FREE_QUOTA = "isFreeQuota";

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private String currentUser;

    private Map<String, Object> data;



}
