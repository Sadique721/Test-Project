package com.savbill.integrationsystem.SOAPService.ReauthSession;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "wsReauthSessionsBySubscriberIdentity", namespace = "http://sessionmanagement.ws.nvsmx.elitecore.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class WsReauthSessionsBySubscriberIdentity {

    @XmlElement(namespace = "")
    private String subscriberId;

    @XmlElement(namespace = "")
    private String alternateId;

    @XmlElement(namespace = "")
    private String parameter1;

    @XmlElement(namespace = "")
    private String parameter2;
}
