package com.savbill.integrationsystem.SOAPService.UpdateAccountService;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "UpdateAccount", namespace = "http://api.act.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class UpdateAccount {

    @XmlElement(namespace = "")
    private String actionItem;

    @XmlElement(namespace = "")
    private String requestId;

    @XmlElement(namespace = "")
    private String userName;

    @XmlElement(namespace = "")
    private String password;

    @XmlElement(namespace = "")
    private List<wsUpdateAccount.Item> item;

}


