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
public class TimeBasePolicyMessage {
    private static final String ID = "policy_id";
    private static final String NAME = "policy_name";
    private static final String ISDELETED = "isDeleted";
    private static final String STATUS = "status";
    private static final String MVNOID = "mvnoId";
    private static final String TIMEBASEPOLICYDTOLIST = "timeBasePolicyDetailsList";


    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private String currentUser;
    private Map<String, Object> data;


}
