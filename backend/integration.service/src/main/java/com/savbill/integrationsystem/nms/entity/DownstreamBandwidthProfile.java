package com.savbill.integrationsystem.nms.entity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownstreamBandwidthProfile{

    @JsonProperty("committed-information-rate")
    private Value committedInformationRate;

    @JsonProperty("committed-burst-size")
    private Value committedBurstSize;

    @JsonProperty("peak-burst-size")
    private Value peakBurstSize;

    @JsonProperty("peak-information-rate")
    private Value peakInformationRate;

    @JsonProperty("color-aware")
    private boolean colorAware;

    @JsonProperty("profile-name")
    private String profileName;

    @JsonProperty("downstream-bandwidth-policy-type")
    private String downstreamBandwidthPolicyType;

    @JsonProperty("is-strom-control")
    private boolean isStromControl;

    @JsonProperty("downstream-bandwidth-profile-level")
    private String downstreamBandwidthProfileLevel;

    @JsonProperty("downstream-bandwidth-profile-type")
    private String downstreamBandwidthProfiletype;

    @JsonProperty("uuid")
    private String uuid;

    // Add getters and setters

    public Value getCommittedInformationRate() {
        return committedInformationRate;
    }

    public void setCommittedInformationRate(Value committedInformationRate) {
        this.committedInformationRate = committedInformationRate;
    }

    public Value getCommittedBurstSize() {
        return committedBurstSize;
    }

    public void setCommittedBurstSize(Value committedBurstSize) {
        this.committedBurstSize = committedBurstSize;
    }

    public Value getPeakBurstSize() {
        return peakBurstSize;
    }

    public void setPeakBurstSize(Value peakBurstSize) {
        this.peakBurstSize = peakBurstSize;
    }

    public boolean isColorAware() {
        return colorAware;
    }

    public void setColorAware(boolean colorAware) {
        this.colorAware = colorAware;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getDownstreamBandwidthPolicyType() {
        return downstreamBandwidthPolicyType;
    }

    public void setDownstreamBandwidthPolicyType(String downstreamBandwidthPolicyType) {
        this.downstreamBandwidthPolicyType = downstreamBandwidthPolicyType;
    }

    public boolean isStromControl() {
        return isStromControl;
    }

    public void setStromControl(boolean stromControl) {
        isStromControl = stromControl;
    }

    public String getDownstreamBandwidthProfileLevel() {
        return downstreamBandwidthProfileLevel;
    }

    public void setDownstreamBandwidthProfileLevel(String downstreamBandwidthProfileLevel) {
        this.downstreamBandwidthProfileLevel = downstreamBandwidthProfileLevel;
    }

    static class Value {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
