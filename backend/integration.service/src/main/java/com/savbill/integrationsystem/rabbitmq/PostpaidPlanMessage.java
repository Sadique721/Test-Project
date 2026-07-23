package com.savbill.integrationsystem.rabbitmq;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostpaidPlanMessage {

    //Plan
    private static final String SAVBILL_API_GATEWAY = "SAVBILL_API_GATEWAY";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DISPLAYNAME = "displayName";
    private static final String CODE = "code";
    private static final String DESC = "desc";
    private static final String CATEGORY = "category";
    private static final String STARTDATE = "startDate";
    private static final String ENDDATE = "endDate";
    private static final String UPLOADQOS = "uploadQOS";
    private static final String DOWNLOADQOS = "downloadQOS";
    private static final String UPLOADTS = "uploadTs";
    private static final String DOWNLOADTS = "downloadTs";
    private static final String ALLOWOVERUSAGE = "allowOverUsage";
    private static final String QUOTAUNIT = "quotaUnit";
    private static final String QUOTA = "quota";
    private static final String PLANSTATUS = "planStatus";
    private static final String CHILDQUOTA = "childQuota";
    private static final String CHILDQUOTAUNIT = "childQuotaUnit";
    private static final String SLICE = "slice";
    private static final String SLICEUNIT = "sliceUnit";
    private static final String ATTACHEDTOALLHOTSPOTS = "attachedToAllHotSpots";
    private static final String PARAM1 = "param1";
    private static final String PARAM2 = "param2";
    private static final String PARAM3 = "param3";
    private static final String MVNOID = "mvnoId";
    private static final String STATUS = "status";
    private static final String TAXID = "taxId";
    private static final String SERVICEID = "serviceId";
    private static final String SERVICENAME = "serviceName";
    private static final String PLANTYPE = "plantype";
    private static final String MAXCHILD = "maxChild";
    private static final String CHARGELIST = "chargeList";
    private static final String DBR = "dbr";
    private static final String PLANGROUP = "planGroup";
    private static final String VALIDITY = "validity";
    private static final String SACCODE = "saccode";
    private static final String MAXCONCURRENTSESSION = "maxconcurrentsession";
    private static final String QUOTAUNITTIME = "quotaunittime";
    private static final String QUOTATIME = "quotatime";
    private static final String QUOTATYPE = "quotatype";
    private static final String OFFERPRICE = "offerprice";
    private static final String QOSPOLICYID = "qospolicyid";
    private static final String TIMEBASEPOLICYID = "timebasepolicyId";
    private static final String RADIUSPROFILEIDS = "radiusprofileIds";
    private static final String ISDELETE = "isDelete";
    private static final String CREATEDATESTRING = "createDateString";
    private static final String UPDATEDATESTRING = "updateDateString";
    private static final String QUOTADID = "quotadid";
    private static final String QUOTAINTERCOM = "quotaintercom";
    private static final String QUOTAUNITDID = "quotaunitdid";
    private static final String QUOTAUNITINTERCOM = "quotaunitintercom";
    private static final String DATACATEGORY = "dataCategory";
    private static final String TAXAMOUNT = "taxamount";
    private static final String SERVICEAREAIDS = "serviceAreaIds";
    private static final String SERVICEAREANAMELIST = "serviceAreaNameList";
    private static final String  QUOTARESETINTERVAL = "quotaresetInterval";
    private static final String  UNITSOFVALIDITY = "unitsOfValidity";

    private String operation;

    private String messageId;
    private String message;
    private String sourceName;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date messageDate;
    private String traceId;
    private String spanId;
    private String currentUser;
    private Map<String, Object> data;

    private String planData;
    public PostpaidPlanMessage(){
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Plan from Api Gateway";
    }
}
