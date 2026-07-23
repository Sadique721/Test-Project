package com.savbill.integrationsystem.SOAPService.changeAndApplyServicesToSubAcctNameXML;


import javax.xml.bind.annotation.XmlElement;

public class Override {  // Make this class public

    private String name;

    private String value;

    public Override() {

    }


    @XmlElement(name = "overrideName")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "overrideValue")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Override(com.savbill.integrationsystem.RestApiService.updateSubscriberAccountXML.Override override) {
    }
    public Override(Override dto) {
        this.name = dto.getName(); // Map key to name
        this.value = dto.getValue();
    }


}
