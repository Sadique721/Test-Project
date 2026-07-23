package com.savbill.integrationsystem.SOAPService.UpdateAccountService;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "wsUpdateAccount", namespace = "http://api.act.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class wsUpdateAccount {

    @XmlElement(namespace = "")
    private String actionItem;

    @XmlElement(namespace = "")
    private String requestId;

    @XmlElement(namespace = "")
    private String userName;

    @XmlElement(namespace = "")
    private String password;

    @XmlElement(namespace = "")
    private List<Item> item;

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"key", "value"})
    @Getter
    @Setter
    public static class Item {

        @XmlElement(namespace = "")
        private String key;

        @XmlElement(namespace = "")
        private String value;
    }
}
