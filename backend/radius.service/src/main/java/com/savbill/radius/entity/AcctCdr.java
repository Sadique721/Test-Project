package com.savbill.radius.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TBLTACCTCDR")
public class AcctCdr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated CDR Id")
    @Column(name = "cdrid", nullable = false)
    private Long cdrId;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "username", length = 250)
    private String userName;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "userpassword", length = 250)
    private String userPassword;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "chappassword", length = 250)
    private String chapPassword;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "nasipaddress", length = 250)
    private String nasIpAddress;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "nasport", length = 250)
    private String nasPort;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "servicetype", length = 250)
    private String serviceType;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "framedprotocol", length = 250)
    private String framedProtocol;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "framedipaddress", length = 250)
    private String framedIpAddress;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "framedipnetmask", length = 250)
    private String framedIpNetmask;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "framedrouting", length = 250)
    private String framedRouting;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "filterid", length = 250)
    private String filterId;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "framedmtu", length = 250)
    private String framedMtu;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "framedcompression", length = 250)
    private String framedCompression;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "loginiphost", length = 250)
    private String loginIpHost;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "loginservice", length = 250)
    private String loginService;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "logintcpport", length = 250)
    private String loginTcpPort;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "replymessage", length = 250)
    private String replyMessage;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "callbacknumber", length = 250)
    private String callbackNumber;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "callbackid", length = 250)
    private String callbackId;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "framedroute", length = 250)
    private String framedRoute;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "framedipxnetwork", length = 250)
    private String framedIpxNetwork;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "state", length = 250)
    private String state;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "class", length = 250)
    private String acctClass;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "vendorspecific", length = 250)
    private String vendorSpecific;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "sessiontimeout", length = 250)
    private String sessionTimeout;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "idletimeout", length = 250)
    private String idleTimeout;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "terminationaction", length = 250)
    private String terminationAction;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "calledstationid", length = 250)
    private String calledStationId;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "callingstationid", length = 250)
    private String callingStationId;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "nasidentifier", length = 250)
    private String nasIdentifier;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "proxystate", length = 250)
    private String proxyState;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "loginlatservice", length = 250)
    private String loginLatService;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "loginlatnode", length = 250)
    private String loginLatNode;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "loginlatgroup", length = 250)
    private String loginLatGroup;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "framedappletalklink", length = 250)
    private String framedAppleTalkLink;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "framedappletalknetwork", length = 250)
    private String framedAppleTalkNetwork;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "framedappletalkzone", length = 250)
    private String framedAppleTalkZone;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "acctstatustype", length = 250)
    private String acctStatusType;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "acctdelaytime", length = 250)
    private String acctDelayTime;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "acctinputoctets", length = 250)
    private String acctInputOctets;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "acctoutputoctets", length = 250)
    private String acctOutputOctets;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "acctsessionid", length = 250)
    private String acctSessionId;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "acctauthentic", length = 250)
    private String acctAuthentic;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "acctsessiontime", length = 250)
    private String acctSessionTime;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "acctinputpackets", length = 250)
    private String acctInputPackets;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "acctoutputpackets", length = 250)
    private String acctOutputPackets;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "acctterminatecause", length = 250)
    private String acctTerminateCause;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "acctmultisessionid", length = 250)
    private String acctMultiSessionId;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "acctlinkcount", length = 250)
    private String acctLinkCount;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "acctinputgigawords", length = 250)
    private String acctInputGigawords;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "acctoutputgigawords", length = 250)
    private String acctOutputGigawords;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "eventtimestamp", length = 250)
    private String eventTimestamp;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "chapchallenge", length = 250)
    private String chapChallenge;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "nasporttype", length = 250)
    private String nasPortType;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "portlimit", length = 250)
    private String portLimit;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "loginlatport", length = 250)
    private String loginLATPort;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "accttunnelconnection", length = 250)
    private String acctTunnelConnection;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "arappassword", length = 250)
    private String arapPassword;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "arapfeatures", length = 250)
    private String arapFeatures;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "arapzoneaccess", length = 250)
    private String arapZoneAccess;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "arapsecurity", length = 250)
    private String arapSecurity;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "arapsecuritydata", length = 250)
    private String arapSecurityData;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "passwordretry", length = 250)
    private String passwordRetry;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "prompt", length = 250)
    private String prompt;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "connectinfo", length = 250)
    private String connectInfo;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "configurationtoken", length = 250)
    private String configurationToken;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "eapmessage", length = 250)
    private String eapMessage;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "messageauthenticator", length = 250)
    private String messageAuthenticator;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "arapchallengeresponse", length = 250)
    private String arapChallengeResponse;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "acctinteriminterval", length = 250)
    private String acctInterimInterval;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "nasportid", length = 250)
    private String nasPortId;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "framedpool", length = 250)
    private String framedPool;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "nasipv6address", length = 250)
    private String nasIPv6Address;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "framedinterfaceid", length = 250)
    private String framedInterfaceId;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "framedipv6prefix", length = 250)
    private String framedIPv6Prefix;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "loginipv6host", length = 250)
    private String loginIPv6Host;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "framedipv6route", length = 250)
    private String framedIPv6Route;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "framedipv6pool", length = 250)
    private String framedIPv6Pool;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "digestresponse", length = 250)
    private String digestResponse;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "digestattributes", length = 250)
    private String digestAttributes;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "framedipv6address", length = 250)
    private String framedipv6address;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "createdate", length = 250)
    @JsonProperty("createDate")
    private Timestamp createdDate;

    @ApiModelProperty(notes = "This is Ip Address of Client")
    @Column(name = "lastmodificationdate", length = 250)
    @JsonProperty("lastModificationDate")
    private Timestamp lastmodifiedDate;

    @ApiModelProperty(hidden = true)
    @Column(name = "mvnoid", nullable = false)
    private Integer mvnoId;

    @ApiModelProperty(notes = "This is location id")
    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @ApiModelProperty(notes = "This is delegated  IPV6 prefix ")
    @Column(name = "DelegatedIPv6Prefix", nullable = false)
    private String delegatedIPv6Prefix;
    @ApiModelProperty(notes = "This is addl1")
    @Column(name = "addl1", nullable = false)
    private String addl1;
    @ApiModelProperty(notes = "This is addl2")
    @Column(name = "addl2", nullable = false)
    private String addl2;
    @ApiModelProperty(notes = "This is custid")
    @Column(name = "custid", nullable = false)
    private String custid;

    @ApiModelProperty(notes = "This is cprid")
    @Column(name = "cprid")
    private String cprid;
}
