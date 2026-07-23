package com.diameter.kafka;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = CustomMessage.class)
public class CustomMessage {
    private String messageId;

    private String message;
    private Date messageDate;
    private String sourceName;
    private String traceId;
    private String spanId;
    private String currentUser;
    private boolean isCustomerCreated;
    private Map<String, Object> customerData;
    private Map<String, Object> data;
//    private List<MacAddressMapping> macAddressMapping;
//    private List<CustomerTimeBasePolicyMapping> customerTimeBasePolicyMappings;
//    private List<CustomerQosPolicyMapping> customerQosPolicyMapping;
    private List<Long> locationIdList;
    private String quotaResetInterval;
    private String operation;
    private String planData;

    private String macMapper;
    private boolean isTriggerCoaDm;
    private String custMacMapppingRepository;
    private boolean ignoreOnCreate;
    private boolean updateAllCustPlan;
    List<CustomMessage> postpaidPlanMessages = new ArrayList<>();
}
