package com.savbill.radius.aaa.constant;

public enum RequestType {
    AccessRequest(1, "Access-Request"),
    AccessAccept(2, "Access-Accept"),
    AccessReject(3, "Access-Reject"),
    AccessChallenge(11, "Access-Challenge");

    private final int value;
    private final String packetType;

    RequestType(int value, String packetType) {
        this.value = value;
        this.packetType = packetType;
    }

    public int getValue() {
        return value;
    }

    public String getPacketType() {
        return packetType;
    }

    public static RequestType fromValue(int value) {
        for (RequestType type : RequestType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + value);
    }

    public static RequestType fromPacketType(String description) {
        for (RequestType type : RequestType.values()) {
            if (type.packetType.equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown enum description: " + description);
    }

    @Override
    public String toString() {
        return name() + "(" + value + ", \"" + packetType + "\")";
    }
}

