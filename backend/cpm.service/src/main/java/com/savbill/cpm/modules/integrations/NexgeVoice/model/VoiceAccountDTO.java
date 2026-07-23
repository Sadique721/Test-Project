package com.savbill.cpm.modules.integrations.NexgeVoice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class VoiceAccountDTO {
    private String id;
    private String accountType;
    private String billType;
    private String servicePlanId;
    private Double creditLimit;
    @JsonProperty("DIDProfile")
    private List<DIDProfile> DIDProfile;
    @JsonProperty("IntercomGroup")
    private String IntercomGroup;
    @JsonProperty("IntercomNumber")
    private String IntercomNumber;
}
