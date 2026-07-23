package com.savbill.integrationsystem.MtnUssd;

import lombok.Data;

@Data
public class MtnUssdDTO {

    private String sessionId;

    private String messageType;

    private String msisdn;

    private String serviceCode;

    private String ussdString;

    private String cellId;

    private String language;

    private String imsi;

    private String username;

    private String password;

}
