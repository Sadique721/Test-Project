package com.savbill.radius.aaa.constant;

public enum RadiusAttributes {
    USER_NAME(1, "User-Name"),
    USER_PASSWORD(2, "User-Password"),
    CHAP_PASSWORD(3, "CHAP-Password"),
    NAS_IP_ADDRESS(4, "NAS-IP-Address"),
    NAS_PORT(5, "NAS-Port"),
    SERVICE_TYPE(6, "Service-Type"),
    FRAMED_PROTOCOL(7, "Framed-Protocol"),
    FRAMED_IP_ADDRESS(8, "Framed-IP-Address"),
    FRAMED_IP_NETMASK(9, "Framed-IP-Netmask"),
    FRAMED_ROUTING(10, "Framed-Routing"),
    FILTER_ID(11, "Filter-Id"),
    FRAMED_MTU(12, "Framed-MTU"),
    FRAMED_COMPRESSION(13, "Framed-Compression"),
    LOGIN_IP_HOST(14, "Login-IP-Host"),
    LOGIN_SERVICE(15, "Login-Service"),
    LOGIN_TCP_PORT(16, "Login-TCP-Port"),
    REPLY_MESSAGE(18, "Reply-Message"),
    CALLBACK_NUMBER(19, "Callback-Number"),
    CALLBACK_ID(20, "Callback-Id"),
    FRAMED_ROUTE(22, "Framed-Route"),
    FRAMED_IPX_NETWORK(23, "Framed-IPX-Network"),
    STATE(24, "State"),
    CLASS(25, "Class"),
    VENDOR_SPECIFIC(26, "Vendor-Specific"),
    SESSION_TIMEOUT(27, "Session-Timeout"),
    IDLE_TIMEOUT(28, "Idle-Timeout"),
    TERMINATION_ACTION(29, "Termination-Action"),
    CALLED_STATION_ID(30, "Called-Station-Id"),
    CALLING_STATION_ID(31, "Calling-Station-Id"),
    NAS_IDENTIFIER(32, "NAS-Identifier"),
    PROXY_STATE(33, "Proxy-State"),
    LOGIN_LAT_SERVICE(34, "Login-LAT-Service"),
    LOGIN_LAT_NODE(35, "Login-LAT-Node"),
    LOGIN_LAT_GROUP(36, "Login-LAT-Group"),
    FRAMED_APPLETALK_LINK(37, "Framed-AppleTalk-Link"),
    FRAMED_APPLETALK_NETWORK(38, "Framed-AppleTalk-Network"),
    FRAMED_APPLETALK_ZONE(39, "Framed-AppleTalk-Zone"),
    ACCT_SESSION_ID(44, "Acct-Session-Id"),
    NAS_PORT_ID(87, "NAS-Port-Id"),
    ACCT_MULTI_SESSION_ID(50, "Acct-Multi-Session-Id");

    private final int code;
    private final String name;

    RadiusAttributes(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static RadiusAttributes fromCode(int code) {
        for (RadiusAttributes attr : values()) {
            if (attr.code == code) {
                return attr;
            }
        }
        throw new IllegalArgumentException("Invalid RADIUS attribute code: " + code);
    }
}


