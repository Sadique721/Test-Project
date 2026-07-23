package com.savbill.integrationsystem.apiAudits.model;

import com.savbill.integrationsystem.core.dto.Auditable;
import com.savbill.integrationsystem.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiAuditsDTO extends Auditable<Long> implements IBaseDto {

    @Id
    private Long id;

    //time duration for api call initiation
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timeStamp;

    //http api url
    private String apiUrl;

    //header of the apis
    private String headerDetails;

    //http method for e.g post or get
    private String httpMethod;

    //request payload details
    private String requestPayload;

    //If data comes in json format
    private Object requestPayloadInJson;

    //response payload details
    private String responsePayload;

    // if data comes in json format
    private Object responsePayloadInJson;

    //http request status code details
    private String httpStatusCode;

    // total time taken to respond the api request
    private String responseTime;

    //authorization token or api key to authenticate the api
    private String autToken;

    //user who has initiate the api request
    private String userName;

    //ipaddress of the user who has initiated the api call request
    private String ipAddress;

    //error message if api call get failed due to some error
    private String errorMessage;

    //api request limit if any
    private String rateLimitInfo;

    //environment like. production or staging
    private String environmentInfo;

    //details of those apis which are mandatory to call the initiated api requst
    private String dependencies;

    //additional metadata relevant to your application or business logic that can aid in analysis
    private String customMetaData;

    private Long mvnoId;

    private Boolean isDeleted;

    private String usernameForAudit;

    private String referenceNumber;


    @Override
    public Long getIdentityKey() {
        return id;
    }

//    @Override
//    public Long getMvnoId() {
//        return null;
//    }
//
//    @Override
//    public void setMvnoId(Long mvnoId) {
//
//    }
}
