package com.savbill.salescrmsbss.entity.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuickInvoicePojo {
    public List<CustMilestoneDetailsPojo> custMileStoneDetailsList = new ArrayList<>();

    public QuickInvoicePojo(){}
//    public QuickInvoicePojo(QuickInvoicePojoMessage message){
//        if(message != null){
//            if(message.getLeadMasterPojoMessage()!= null)
//                this.leadMasterPojo = new LeadMasterPojo(message.getLeadMasterPojoMessage());
//            if(message.getCustMileStoneDetailsList() != null && message.getCustMileStoneDetailsList().size()>0)
//                this.custMileStoneDetailsList = message.getCustMileStoneDetailsList();
//        }
//    }
}
