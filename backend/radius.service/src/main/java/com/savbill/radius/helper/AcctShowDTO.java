package com.savbill.radius.helper;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class AcctShowDTO {

    private String username;

    private Timestamp createdatetimestamp;

    private String inputOctant;

    private String outputOctant;

    private String acctSessionId;

    private String acctSessionTime;

    private LocalDateTime createdate;

    public AcctShowDTO (String username , Timestamp createdate , String inputOctant , String outputOctant , String acctSessionId , String acctSessionTime){
        this.username = username;
        this.createdatetimestamp = createdate;
        this.inputOctant = inputOctant;
        this.outputOctant = outputOctant;
        this.acctSessionId = acctSessionId;
        this.acctSessionTime = acctSessionTime;
    }
}

