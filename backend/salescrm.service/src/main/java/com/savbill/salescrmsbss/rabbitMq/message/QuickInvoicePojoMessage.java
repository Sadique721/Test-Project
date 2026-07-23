package com.savbill.salescrmsbss.rabbitMq.message;

import com.savbill.salescrmsbss.entity.pojo.QuickInvoicePojo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuickInvoicePojoMessage {

    public List<CustMilestoneDetailsMessage> custMileStoneDetailsList = new ArrayList<>();

    public QuickInvoicePojoMessage(){}

    public QuickInvoicePojoMessage(QuickInvoicePojo pojo){

        if(pojo.getCustMileStoneDetailsList() != null && pojo.getCustMileStoneDetailsList().size()>0) {
            pojo.getCustMileStoneDetailsList().forEach(item ->{
                custMileStoneDetailsList.add(new CustMilestoneDetailsMessage(item));
            });
        }
    }
}
