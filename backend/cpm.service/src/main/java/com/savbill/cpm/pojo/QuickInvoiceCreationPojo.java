package com.savbill.cpm.pojo;

import com.savbill.cpm.rabbitMq.message.QuickInvoicePojoMessage;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuickInvoiceCreationPojo {

    public List<CustMilestoneDetailsPojo> custMileStoneDetailsList = new ArrayList<>();

    public QuickInvoiceCreationPojo(){}
    public QuickInvoiceCreationPojo(QuickInvoicePojoMessage message){
        if(message != null){
            if(message.getCustMileStoneDetailsList() != null && message.getCustMileStoneDetailsList().size()>0){
                message.getCustMileStoneDetailsList().forEach(item->{
                    custMileStoneDetailsList.add(new CustMilestoneDetailsPojo(item));
                });
            }
        }
    }
}
