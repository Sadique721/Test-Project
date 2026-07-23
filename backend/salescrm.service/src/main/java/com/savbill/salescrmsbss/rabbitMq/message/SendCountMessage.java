package com.savbill.salescrmsbss.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendCountMessage {

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private HashMap<Integer, Long>  count;

    private static final String SAVBILL_API_GATEWAY = "Savbill Api Gateway";

    public SendCountMessage( HashMap<Integer, Long> countListmap) {

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Getting suitable staff for lead management approval";
        this.count = countListmap;
        this.sourceName = SAVBILL_API_GATEWAY;

    }


}
