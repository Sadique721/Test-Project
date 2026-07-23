package com.savbill.integrationsystem.SOAPService.SubAccountNameIsLoggendIn;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

    @XmlRootElement(name = "subAcctNameIsLoggedOn", namespace = "http://npm.redback.com")
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "subAcctNameIsLoggedOn", namespace = "http://npm.redback.com")
    @Getter
    @Setter
    public class SubAcctNameIsLogged{
        @XmlElement(namespace = "")
        private String String_1;

    }


