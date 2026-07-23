package com.diameter.enums;


import lombok.Getter;

@Getter
public enum ServiceType {
    VOICE("Voice Call Service"),
    DATA("Data Service"),
    SMS("SMS Service"),
    VIDEO("Video Call Service"),
    BUNDLE("Bundle Service"),
    MMS("MMS Service"),
    ROAMING_VOICE("Roaming Voice"),
    ROAMING_DATA("Roaming Data"),
    INTERNATIONAL("International Service"),
    QUOTA_DATA("DATA"),
    TIME_DATA("TIME"),
    TIME("Time Service");

    private final String description;

    ServiceType(String description) {
        this.description = description;
    }
}
