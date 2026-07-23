package com.savbill.revenuemanagement.core.schedulers;

public enum Weekly {
    MONDAY("MONDAY"),

    TUESDAY("TUESDAY"),

    WEDNESDAY("WEDNESDAY"),

    THURSDAY("THURSDAY"),

    FRIDAY("FRIDAY"),

    SATURDAY("SATURDAY"),

    SUNDAY("SUNDAY");
    private final String value;

    Weekly(String value) {
        this.value=value;
    }
    public String getValue() {
        return value;
    }
}
