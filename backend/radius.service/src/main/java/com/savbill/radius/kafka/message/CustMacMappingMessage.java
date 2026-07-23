package com.savbill.radius.kafka.message;

import com.savbill.radius.entity.CustMacMappping;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = CustMacMappingMessage.class)
public class CustMacMappingMessage {

    private static final String SAVBILL_API_GATEWAY = "SAVBILL_API_GATEWAY";
    private static final String ID = "id";
    private static final String CUST_ID = "custid";
    private static final String MAC_ADDRESS = "macAddress";
    private static final String IS_DELETE = "isDelete";
    private static final String MVNO_ID = "mvnoId";
    private static final String USER_NAME = "userName";

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private String currentUser;
    private Map<String, Object> data;

    public CustMacMappingMessage(){}

    public CustMacMappingMessage(CustMacMappping custMacMappping, Integer mvnoId, String userName){
        Map<String, Object> map = new HashMap<>();
        map.put(ID, custMacMappping.getId());
        map.put(MAC_ADDRESS, custMacMappping.getMacAddress());
        map.put(CUST_ID, custMacMappping.getCustomer() != null ? custMacMappping.getCustomer().getId() : null);
        map.put(IS_DELETE, custMacMappping.getIsDeleted());
        map.put(MVNO_ID, mvnoId);
        map.put(USER_NAME, userName);
        

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer Mac Mapping from Api Gateway";
        this.data = map;
        this.sourceName = SAVBILL_API_GATEWAY;
    }
}
