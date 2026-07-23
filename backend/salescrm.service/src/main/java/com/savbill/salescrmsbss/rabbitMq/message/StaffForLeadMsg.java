package com.savbill.salescrmsbss.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffForLeadMsg {

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private Map<String, Integer> leadMgmtData;

    private static final String SAVBILL_API_GATEWAY = "Savbill Api Gateway";



}
