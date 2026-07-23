package com.savbill.integrationsystem.apiAudits.entity;


import com.savbill.integrationsystem.core.data.IBaseData;
import com.savbill.integrationsystem.core.dto.Auditable;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "tblmapiauditdetails")
public class ApiAudits extends Auditable implements IBaseData<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //time duration for api call initiation

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "time_stamp",nullable = false)
    private LocalDateTime timeStamp;

    //http api url
    @Column(name="api_url", nullable = false)
    private String apiUrl;

    @Column(name="header_details")
    private String headerDetails;

    //http method for e.g post or get
    @Column(name = "http_method",nullable = false)
    private String httpMethod;

    //request payload details
    @Column(name= "request_payload")
    private String requestPayload;

    //response payload details
    @Column(name="response_payload")
    private String responsePayload;

    //http request status code details
    @Column(name = "http_status_code")
    private String httpStatusCode;

    // total time taken to respond the api request
    @Column(name = "response_time",nullable = false)
    private String responseTime;

    //authorization token or api key to authenticate the api
    @Column(name="auth_token")
    private String autToken;

    //user who has initiate the api request
    @Column(name="user_name",nullable = false)
    private String userName;

    //ipaddress of the user who has initiated the api call request
    @Column(name="ip_address",nullable = false)
    private String ipAddress;

    //error message if api call get failed due to some error
    @Column(name="error_message")
    private String errorMessage;

    //api request limit if any
    @Column(name="rate_limit_info")
    private String rateLimitInfo;

    //environment like. production or staging
    @Column(name="environment_info")
    private String environmentInfo;

    //details of those apis which are mandatory to call the initiated api requst
    @Column(name="dependencies")
    private String dependencies;

    //additional metadata relevant to your application or business logic that can aid in analysis
    @Column(name="custom_meta_data")
    private String customMetaData;

    @Column(name="mvno_id")
    private Long mvnoId;

    @Column(name="is_deleted")
    private Boolean isDeleted;

    @Column(name="username_for_audit")
    private String usernameForAudit;

    @Column(name="reference_number")
    private String referenceNumber;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }
}
