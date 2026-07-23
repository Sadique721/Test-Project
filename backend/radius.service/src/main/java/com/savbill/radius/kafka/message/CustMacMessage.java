package com.savbill.radius.kafka.message;

import com.savbill.radius.entity.MacAddressMapping;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CustMacMessage {

    private String messageId;
    private String message;
    private Date messageDate;
    private String userName;
    private Integer mvnoId;
    private Integer custPlanMapId;
    private HashMap<String, Timestamp> mac;
    private boolean isBulkDelete;
    private boolean checkConcurrency;
    private List<MacAddressMapping> macAddressMappings;
    private String oldMac;
    private boolean isUpdate;
    private boolean isFromRadius;

    public CustMacMessage() {

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer's used data updates";
    }
}
