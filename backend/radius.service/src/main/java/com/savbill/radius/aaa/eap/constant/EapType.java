package com.savbill.radius.aaa.eap.constant;

public enum EapType {
    EAP_TLS(13, "EAP-TLS"),
    EAP_TTLS(21, "EAP-TTLS");
    //EAP_PEAP(25, "EAP-PEAP"),
    //EAP_FAST(43, "EAP-FAST"),
    //EAP_SIMP(20, "EAP-SIMP"),
    //EAP_PWD(52, "EAP-PWD"),
    //EAP_LEAP(17, "EAP-LEAP");

    private final Integer value;
    private final String name;

    EapType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " (" + value + ")";
    }

    public static EapType fromValue(Integer value) {
        for (EapType eapType : EapType.values()) {
            if (eapType.getValue() == value) {
                return eapType;
            }
        }
        throw new IllegalArgumentException("No EapType with value " + value);
    }

    public static EapType fromName(String name) {
        for (EapType eapType : EapType.values()) {
            if (eapType.getName().equalsIgnoreCase(name)) {
                return eapType;
            }
        }
        throw new IllegalArgumentException("No EapType with name " + name);
    }
}

