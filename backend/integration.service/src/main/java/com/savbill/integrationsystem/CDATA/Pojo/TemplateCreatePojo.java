package com.savbill.integrationsystem.CDATA.Pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateCreatePojo {

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;

    @JsonProperty("tmplDesc")
    private String tmplDesc;

    @JsonProperty("items")
    private Object items; // Adjust type as per actual structure

    @JsonProperty("paramObject")
    private ParamObject paramObject;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ParamObject {
        @JsonProperty("wan")
        private List<Wan> wan;

        @JsonProperty("wlan")
        private Object wlan; // Adjust type as per actual structure

        @JsonProperty("voip")
        private Object voip; // Adjust type as per actual structure
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Wan {
        @JsonProperty("enable_qos")
        private int enableQos;

        @JsonProperty("admin_status")
        private int adminStatus;

        @JsonProperty("enable_napt")
        private int enableNapt;

        @JsonProperty("enable_vlan")
        private int enableVlan;

        @JsonProperty("vlanId")
        private int vlanId;

        @JsonProperty("_802_1_mark")
        private int _802_1Mark;

        @JsonProperty("service_type")
        private int serviceType;

        @JsonProperty("connection_type")
        private int connectionType;

        @JsonProperty("ip_protocol")
        private int ipProtocol;

        @JsonProperty("mtu")
        private int mtu;

        @JsonProperty("enable_igmp_mld_proxy")
        private int enableIgmpMldProxy;

        @JsonProperty("multicast_vlan_id")
        private Object multicastVlanId; // Adjust type as per actual structure

        @JsonProperty("port_binding")
        private PortBinding portBinding;

        @JsonProperty("ppp")
        private Ppp ppp;

        @JsonProperty("ipv4")
        private Ipv4 ipv4;

        @JsonProperty("ipv6")
        private Object ipv6; // Adjust type as per actual structure
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PortBinding {
        @JsonProperty("lan")
        private List<Object> lan; // Adjust type as per actual structure

        @JsonProperty("g24")
        private List<Object> g24; // Adjust type as per actual structure

        @JsonProperty("g5")
        private List<Object> g5; // Adjust type as per actual structure
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Ppp {
        @JsonProperty("username")
        private String username;

        @JsonProperty("password")
        private String password;

        @JsonProperty("type")
        private int type;

        @JsonProperty("idle_time")
        private Object idleTime; // Adjust type as per actual structure

        @JsonProperty("authentication")
        private int authentication;

        @JsonProperty("ac_name")
        private Object acName; // Adjust type as per actual structure

        @JsonProperty("service_name")
        private Object serviceName; // Adjust type as per actual structure
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Ipv4 {
        @JsonProperty("enable_napt")
        private int enableNapt;

        @JsonProperty("dhcp_enable")
        private int dhcpEnable;

        @JsonProperty("request_dns")
        private int requestDns;
    }
}

