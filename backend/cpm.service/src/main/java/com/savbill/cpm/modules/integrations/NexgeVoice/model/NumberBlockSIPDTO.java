package com.savbill.cpm.modules.integrations.NexgeVoice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NumberBlockSIPDTO {
    private String customerPilotNumber;
    @JsonProperty("DIDNumbers")
    private List<String> DIDNumbers = new ArrayList<>();
}
