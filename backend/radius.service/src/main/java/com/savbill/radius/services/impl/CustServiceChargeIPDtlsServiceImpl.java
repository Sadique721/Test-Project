package com.savbill.radius.services.impl;

import com.savbill.radius.entity.CustServiceChargeIPDetails;
import com.savbill.radius.kafka.message.CustServiceChargeIPDtlsMessage;
import com.savbill.radius.repository.CustServiceChargeIPDtlsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustServiceChargeIPDtlsServiceImpl {
    @Autowired
    private CustServiceChargeIPDtlsRepo custServiceChargeIPDtlsRepo;

    public CustServiceChargeIPDetails save(CustServiceChargeIPDtlsMessage message){
        try {
            if (message.getData() != null) {
                CustServiceChargeIPDetails custServiceChargeIPDetails = new CustServiceChargeIPDetails(message);
                CustServiceChargeIPDetails custServiceChargeIPDetailsSave = custServiceChargeIPDtlsRepo.save(custServiceChargeIPDetails);
                return custServiceChargeIPDetailsSave;
            } else {
                throw new RuntimeException("INVALID_DATA");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    public CustServiceChargeIPDetails update(CustServiceChargeIPDtlsMessage message){
        try {
            if (message.getData() != null) {
                CustServiceChargeIPDetails custServiceChargeIPDetails = new CustServiceChargeIPDetails(message);
                CustServiceChargeIPDetails custServiceChargeIPDetailsSave = new CustServiceChargeIPDetails();
                CustServiceChargeIPDetails details = custServiceChargeIPDtlsRepo.findById(custServiceChargeIPDetails.getId()).get();
                custServiceChargeIPDetailsSave =  custServiceChargeIPDtlsRepo.save(custServiceChargeIPDetails);
                return custServiceChargeIPDetailsSave;
            } else {
                throw new RuntimeException("INVALID_DATA");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
