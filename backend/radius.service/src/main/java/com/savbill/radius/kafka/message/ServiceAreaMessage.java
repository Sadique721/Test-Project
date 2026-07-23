package com.savbill.radius.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = ServiceAreaMessage.class)
public class ServiceAreaMessage {

    private static final String ID = "id";
    private static final String AREA = "areaid";
    private static final String AREANAME = "areaname";
    private static final String STATUSSERVICEAREA = "statusservicearea";
    private static final String  IDENTITYKEY = "identityKey";
    private static final String  ISDELETED = "isdeleted";
    private static final String  LATITUDE = "latitude";
    private static final String  LONGITUDE = "longitude";
    private static final String  MVNOID = "MvnoId";
    private static final String CREATED_DATE = "createdate";
    private static final String LAST_MODIFIED_DATE = "lastmodifieddate";
    private static final String CREATEDBYNAMESERVICEAREA = "createdByName";
    private static final String LASTMODIFIEDBYNAMESERVICEAREA = "lastModifiedByName";
    private static final String CREATED_BY_ID_SERVICEAREA= "createdById";
    private static final String LASTMODIFIED_BY_ID_SERVICEAREA = "lastModifiedById";
    private static final String SAVBILL_API_GATEWAY = "Savbill Api Gateway";

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private Map<String, Object> customerData;


}
