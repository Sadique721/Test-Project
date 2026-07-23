package com.savbill.integrationsystem.SOAPService.WsLoggOffSubSession;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "logoffSubSession", namespace = "http://npm.redback.com")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "logoffSubSession", namespace = "http://npm.redback.com")
@Getter
@Setter
public class LoggOffSubsession {
    @XmlElement(namespace = "")
    private String String_1;

}