package com.savbill.integrationsystem.NewNMSIntegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WANConfigReqBodyDTO {
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

    @JsonProperty("MODE")
    private Integer mode;

    @JsonProperty("CONNTYPE")
    private Integer connType;

    @JsonProperty("VLAN")
    private Integer vlan;

    @JsonProperty("NAT")
    private Integer nat;

    @JsonProperty("IPMODE")
    private Integer ipMode;

    @JsonProperty("IPSTACKMODE")
    private Integer ipStackMode;

    @JsonProperty("IP6SRCTYPE")
    private Integer ip6SrcType;

    @JsonProperty("IP6PREFIXSRCTYPE")
    private Integer ip6PrefixSrcType;

    @JsonProperty("WANIP")
    private String wanIp;

    @JsonProperty("WANMASK")
    private String wanMask;

    @JsonProperty("WANGATEWAY")
    private String wanGateway;

    @JsonProperty("MASTERDNS")
    private String masterDns;

    @JsonProperty("SLAVEDNS")
    private String slaveDns;

    @JsonProperty("IP6ADDRESS")
    private String ip6Address;

    @JsonProperty("IP6GATEWAY")
    private String ip6Gateway;

    @JsonProperty("IP6MASTERDNS")
    private String ip6MasterDns;

    @JsonProperty("IP6SLAVEDNS")
    private String ip6SlaveDns;

    @JsonProperty("IP6STATICPREFIX")
    private String ip6StaticPrefix;

    @JsonProperty("PPPOEPROXY")
    private Integer pppoeProxy;

    @JsonProperty("PPPOEUSER")
    private String pppoeUser;

    @JsonProperty("PPPOEPASSWD")
    private String pppoePasswd;

    @JsonProperty("PPPOENAME")
    private String pppoeName;

    @JsonProperty("PPPOEAUTHMODE")
    private Integer pppoeAuthMode;

    @JsonProperty("PPPOEMODE")
    private Integer pppoeMode;

    @JsonProperty("PPPOEIDLETIME")
    private Integer pppoeIdleTime;

    @JsonProperty("QOS")
    private Integer qos;

    @JsonProperty("UPORT")
    private Integer uPort;

    @JsonProperty("SSID")
    private Integer ssid;

    @JsonProperty("VLANMODE")
    private Integer vlanMode;

    @JsonProperty("TRANSSTATE")
    private Integer transState;

    @JsonProperty("TRANSVALUE")
    private Integer transValue;

    @JsonProperty("TRANSCOS")
    private Integer transCos;

    @JsonProperty("QINQSTATE")
    private Integer qinqState;

    @JsonProperty("TPID")
    private Integer tpid;

    @JsonProperty("SVLAN")
    private Integer sVlan;

    @JsonProperty("QINQCOS")
    private Integer qinqCos;

    @JsonProperty("DHCPREMOTEID")
    private String dhcpRemoteId;

    @JsonProperty("TCONT")
    private Integer tcont;

    @JsonProperty("GEMPORT")
    private Integer gemPort;

    @JsonProperty("UPNP")
    private Integer upnp;

    public WANConfigReqBodyDTO(String serialno, String oltid, String ponid, String onuidtype, String onuid, int mode, int conntype, int vlan, int nat, int ipmode, int ipstackmode, int ip6SRCTYPE, int ip6PREFIXSRCTYPE, String wanip, String wanmask, String wangateway, String masterdns, String slavedns, String ip6ADDRESS, String ip6GATEWAY, String ip6MASTERDNS, String ip6SLAVEDNS, String ip6STATICPREFIX, int pppoeproxy, String pppoeuser, String pppoepasswd, String pppoename, int pppoeauthmode, int pppoemode, int pppoeidletime, int qos, int uport, int ssid, int vlanmode, int transstate, int qinqstate, int tpid, int qinqcos, String dhcpremoteid, int tcont, int gemport, int upnp) {
        this.serialNo = serialno;
        this.oltId = oltid;
        this.ponId = ponid;
        this.onuIdType = onuidtype;
        this.onuId = onuid;
        this.mode = mode;
        this.connType = conntype;
        this.vlan = vlan;
        this.nat = nat;
        this.ipMode = ipmode;
        this.ipStackMode = ipstackmode;
        this.ip6SrcType = ip6SRCTYPE;
        this.ip6PrefixSrcType = ip6PREFIXSRCTYPE;
        this.wanIp = wanip;
        this.wanMask = wanmask;
        this.wanGateway = wangateway;
        this.masterDns = masterdns;
        this.slaveDns = slavedns;
        this.ip6Address = ip6ADDRESS;
        this.ip6Gateway = ip6GATEWAY;
        this.ip6MasterDns = ip6MASTERDNS;
        this.ip6SlaveDns = ip6SLAVEDNS;
        this.ip6StaticPrefix = ip6STATICPREFIX;
        this.pppoeProxy = pppoeproxy;
        this.pppoeUser = pppoeuser;
        this.pppoePasswd = pppoepasswd;
        this.pppoeName = pppoename;
        this.pppoeAuthMode = pppoeauthmode;
        this.pppoeMode = pppoemode;
        this.pppoeIdleTime = pppoeidletime;
        this.qos = qos;
        this.uPort = uport;
        this.ssid = ssid;
        this.vlanMode = vlanmode;
        this.transState = transstate;
        this.qinqState = qinqstate;
        this.tpid = tpid;
        this.qinqCos = qinqcos;
        this.dhcpRemoteId = dhcpremoteid;
        this.tcont = tcont;
        this.gemPort = gemport;
        this.upnp = upnp;
    }
}
