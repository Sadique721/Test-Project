package com.savbill.integrationsystem.nms.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpstreamBandwidthProfile {
    @JsonProperty("upstream-bandwidth-profile")
    private UpstreamProfileDetails profileDetails;

    public UpstreamProfileDetails getProfileDetails() {
        return profileDetails;
    }

    public void setProfileDetails(UpstreamProfileDetails profileDetails) {
        this.profileDetails = profileDetails;
    }
}
