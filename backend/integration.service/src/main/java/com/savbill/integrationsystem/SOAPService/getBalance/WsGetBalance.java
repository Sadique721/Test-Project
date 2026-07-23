package com.savbill.integrationsystem.SOAPService.getBalance;


import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "wsGetBalance", namespace = SoapConstants.NAMESPACE_ELITECORE)
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class WsGetBalance {
    @XmlElement(namespace = "")
    private String subscriberId;
}
