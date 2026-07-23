package com.savbill.integrationsystem.NewNMSIntegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WiFiConfigDTO {

        @JsonProperty("SERIALNO")
        private String serialNo;

        @JsonProperty("OLTID")
        private String oltId;

        @JsonProperty("PONID")
        private String ponId;

        @JsonProperty("ONUIDTYPE")
        private String onuIdType;

        @JsonProperty("ONUID")
        private String onuId;

        @JsonProperty("ENABLE")
        private Integer enable;

        @JsonProperty("WIRELESS-AREA")
        private Integer wirelessArea;

        @JsonProperty("WIRELESS-CHANNEL")
        private Integer wirelessChannel;

        @JsonProperty("WIRELESS-STANDARD")
        private Integer wirelessStandard;

        @JsonProperty("WORKING-FREQUENCY")
        private Integer workingFrequency;

        @JsonProperty("T-POWER")
        private Integer tPower;

        @JsonProperty("FREQUENCY-BANDWIDTH")
        private Integer frequencyBandwidth;

        @JsonProperty("SSID")
        private Integer ssid;

        @JsonProperty("SSID-ENABLE")
        private Integer ssidEnable;

        @JsonProperty("SSID-NAME")
        private String ssidName;

        @JsonProperty("SSID-VISIBLE")
        private Integer ssidVisible;

        @JsonProperty("AUTH-MODE")
        private Integer authMode;

        @JsonProperty("ENCRYPT-TYPE")
        private Integer encryptType;

        @JsonProperty("PRESHARED-KEY")
        private String preSharedKey;

        @JsonProperty("UPDATEKEY-INTERVAL")
        private Integer updateKeyInterval;

        @JsonProperty("RADIUS-SERVER")
        private String radiusServer;

        @JsonProperty("RADIUS-PORT")
        private Integer radiusPort;

        @JsonProperty("RADIUS-KEY")
        private String radiusKey;

        @JsonProperty("WEP-ENCRYPTIONLEVEL")
        private Integer wepEncryptionLevel;

        @JsonProperty("WEP-KEYINDEX")
        private Integer wepKeyIndex;

        @JsonProperty("WEPKEY1")
        private String wepKey1;

        @JsonProperty("WEPKEY2")
        private String wepKey2;

        @JsonProperty("WEPKEY3")
        private String wepKey3;

        @JsonProperty("WEPKEY4")
        private String wepKey4;

        @JsonProperty("WAP-IPADDRESS")
        private String wapIpAddress;

        @JsonProperty("WAP-PORT")
        private Integer wapPort;

        @JsonProperty("MAX-WIFIMAC-COUNT")
        private Integer maxWifiMacCount;

        @JsonProperty("PUBLICSSID")
        private Integer publicSsid;

        @JsonProperty("KICKSTATIONSWITCH")
        private Integer kickStationSwitch;

        @JsonProperty("LOWERTHRESHOLD")
        private Integer lowerThreshold;

        public WiFiConfigDTO(String serialNo, String oltId, String ponId, String onuIdType, String onuId, int enable,
                             int wirelessArea, int wirelessChannel, int wirelessStandard, int workingFrequency,
                             int tPower, int frequencyBandwidth, int ssid, int ssidEnable, String ssidName,
                             int ssidVisible, int authMode, int encryptType, String preSharedKey,
                             int updateKeyInterval, String radiusServer, int radiusPort, String radiusKey,
                             int wepEncryptionLevel, int wepKeyIndex, String wepKey1, String wepKey2, String wepKey3,
                             String wepKey4, String wapIpAddress, int wapPort, int maxWifiMacCount, int publicSsid,
                             int kickStationSwitch, int lowerThreshold) {
                this.serialNo = serialNo;
                this.oltId = oltId;
                this.ponId = ponId;
                this.onuIdType = onuIdType;
                this.onuId = onuId;
                this.enable = enable;
                this.wirelessArea = wirelessArea;
                this.wirelessChannel = wirelessChannel;
                this.wirelessStandard = wirelessStandard;
                this.workingFrequency = workingFrequency;
                this.tPower = tPower;
                this.frequencyBandwidth = frequencyBandwidth;
                this.ssid = ssid;
                this.ssidEnable = ssidEnable;
                this.ssidName = ssidName;
                this.ssidVisible = ssidVisible;
                this.authMode = authMode;
                this.encryptType = encryptType;
                this.preSharedKey = preSharedKey;
                this.updateKeyInterval = updateKeyInterval;
                this.radiusServer = radiusServer;
                this.radiusPort = radiusPort;
                this.radiusKey = radiusKey;
                this.wepEncryptionLevel = wepEncryptionLevel;
                this.wepKeyIndex = wepKeyIndex;
                this.wepKey1 = wepKey1;
                this.wepKey2 = wepKey2;
                this.wepKey3 = wepKey3;
                this.wepKey4 = wepKey4;
                this.wapIpAddress = wapIpAddress;
                this.wapPort = wapPort;
                this.maxWifiMacCount = maxWifiMacCount;
                this.publicSsid = publicSsid;
                this.kickStationSwitch = kickStationSwitch;
                this.lowerThreshold = lowerThreshold;
        }
}
