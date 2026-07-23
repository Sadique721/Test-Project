package com.savbill.integrationsystem.middleware.selfcare.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class TicketResponse {
    @JsonProperty("UserId")
    String userId;

    @JsonProperty("Title")
    String title;

    @JsonProperty("CategoryType")
    Double categoryType;

    @JsonProperty("SubCategoryId")
    Double subCategoryId;

    @JsonProperty("Email")
    String email;

    @JsonProperty("Mobile")
    String mobile;

    @JsonProperty("Priority")
    Double priority;

    @JsonProperty("Status")
    String status;

//    @JsonProperty("PartnerId")
//    String partnerId;

    @JsonProperty("combassign")
    String combassign;

    @JsonProperty("Remarks")
    String remarks;

    @JsonProperty("Solution")
    String solution;

    @JsonProperty("CategoryId")
    Double categoryId;

    @JsonProperty("OpenCaseDetails")
    OpenCaseDetails openCaseDetails;

    @JsonProperty("CloseCaseDetails")
    CloseCaseDetails closeCaseDetails;

    @JsonProperty("AllCaseDetails")
    OpenAndCloseCaseDetails openAndCloseCaseDetails;

    @JsonProperty("customerDetailsModel")
    String customerDetailsModel;

    @JsonProperty("TypeList")
    String TypeList;

    @JsonProperty("SubCategoryList")
    ArrayList subCategoryList;

    @JsonProperty("CustId")
    Integer custId;

    @JsonProperty("SubSubCategoryId")
    Double subSubCategoryId = Double.valueOf(16751L);

    @JsonProperty("IPADDRESS")
    String ipaddress;

    @JsonProperty("PartnerId")
    Double partnerId;

    @JsonProperty("ResellerId")
    Double resellerId;

    @JsonProperty("OTZ")
    String otz;

    @JsonProperty("PartnerName")
    String partnerName;

    @JsonProperty("PartnerCode")
    String partnerCode;

    @JsonProperty("ResponseMsg")
    String responseMesg;

    @JsonProperty("ResponseCode")
    Integer responseCode;

    @JsonProperty("SubSubCategoryList")
    ArrayList subSubCategoryList;
}
