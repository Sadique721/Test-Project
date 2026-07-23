package com.savbill.integrationsystem.nms.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpstreamProfileDetails {

    @JsonProperty("fixed-bandwidth")
    private String fixedBandwidth;

    @JsonProperty("profile-name")
    private String profileName;

    @JsonProperty("max-bandwidth")
    private String maxBandwidth;

    @JsonProperty("assured-bandwidth")
    private String assuredBandwidth;

    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("bandwidth-unit")
    private String bandwidthUnit;

    // You can add more getters and setters if needed
}
