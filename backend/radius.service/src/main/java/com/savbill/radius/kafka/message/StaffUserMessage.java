package com.savbill.radius.kafka.message;

import com.savbill.radius.entity.StaffUserBusinessUnitMapping;
import com.savbill.radius.entity.StaffUserServiceAreaMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = StaffUserMessage.class)
public class StaffUserMessage {

    private static final String SAVBILL_API_GATEWAY = "Savbill Api Gateway";
    private static final String STAFF_USER_SEND_RADIUS = "staff user send radius";
    private static final String ID = "id";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String FIRSTNAME = "firstname";
    private static final String LASTNAME = "lastname";
    private static final String EMAIL = "email";
    private static final String PHONE_NO = "phone";
    private static final String FAIL_COUNT = "failcount";
    private static final String STATUS = "status";
    private static final String LAST_LOGIN_TIME = "last_login_time";
    private static final String CREATEDATE = "createdate";
    private static final String UPDATEDATE = "updatedate";
    private static final String PARTNER_ID = "partnerid";
    private static final String NEWPASSWORD = "newpassword";
    private static final String ROLEIDS = "roleIds";
    private static final String TEAMIDS = "teamIds";
    private static final String TEAMNAMELIST = "teamNameList";
    private static final String ISDELETE = "isDelete";
    private static final String FULL_NAME = "fullName";
    private static final String SYS_STAFF = "sysstaff";
    private static final String OTP = "otp";
    private static final String OTP_VALIDATE = "otpvalidate";
    private static final String SERVICE_AREA = "service_area_id";
    private static final String STAFF_USER_PARENT = "parent_staff_id";
    private static final String MVNO_ID = "mvnoId";
    private static final String SERIVICE_AREA_NAME_LIST = "serviceAreaNameList";
    private static final String CREATEDBYID = "createdbyid";
    private static final String LASTMODIFIEDBYID = "lastmodifiedbyid";
    private static final String CREATEDBYNAME = "createdByName";
    private static final String LASTMODIFIEDBYNAME = "lastmodifiedbyname";
     private  List<StaffUserServiceAreaMapping> serviceAreaMapping ;
    private List<StaffUserBusinessUnitMapping> staffUserBusinessUnitMappings;
    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private Map<String, Object> customerData;



}


