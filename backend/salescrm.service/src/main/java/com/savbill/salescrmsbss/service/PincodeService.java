package com.savbill.salescrmsbss.service;

import com.savbill.salescrmsbss.entity.Pincode;
import com.savbill.salescrmsbss.rabbitMq.message.SavePincodeSharedDataMessage;
import com.savbill.salescrmsbss.rabbitMq.message.UpdatePincodeSharedDataMessage;
import com.savbill.salescrmsbss.repository.PincodeRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class PincodeService {


    @Autowired
    PincodeRepository entityRepository;
    //


    private static Log log = LogFactory.getLog(PincodeService.class);
    @Transactional
    public void savePincode(SavePincodeSharedDataMessage message){
        try {
            Pincode pincode = new Pincode();

            pincode.setId(message.getId());
            pincode.setStatus(message.getStatus());
            pincode.setCountryId(message.getCountryId());
            pincode.setStateId(message.getStateId());
            pincode.setCityId(message.getCityId());
            pincode.setMvnoId(message.getMvnoId());
            pincode.setIsDeleted(message.getIsDeleted());
            pincode.setPincode(message.getPincode());

            entityRepository.save(pincode);
        }catch (Exception e){
            log.info("Unable to Create Pincode with Pincode "+message.getPincode()+" :"+e.getMessage());
        }

    }

    @Transactional
    public void updatePincode(UpdatePincodeSharedDataMessage message){
        try {
            if(message.getId()!=null) {
                Pincode pincode = new Pincode();
                pincode = entityRepository.findById(message.getId()).orElse(null);
                if(pincode!=null) {
                    pincode.setStatus(message.getStatus());
                    pincode.setCountryId(message.getCountryId());
                    pincode.setStateId(message.getStateId());
                    pincode.setCityId(message.getCityId());
                    pincode.setMvnoId(message.getMvnoId());
                    pincode.setIsDeleted(message.getIsDeleted());
                    pincode.setPincode(message.getPincode());

                    entityRepository.save(pincode);
                }else{
//                    log.info("No Data Foundd");
                    Pincode pincode1 = new Pincode();

                    pincode1.setId(message.getId());
                    pincode1.setStatus(message.getStatus());
                    pincode1.setCountryId(message.getCountryId());
                    pincode1.setStateId(message.getStateId());
                    pincode1.setCityId(message.getCityId());
                    pincode1.setMvnoId(message.getMvnoId());
                    pincode1.setIsDeleted(message.getIsDeleted());
                    pincode1.setPincode(message.getPincode());

                    entityRepository.save(pincode1);
                }
            }
        }catch (Exception e){
            log.info("Unable to Update Pincode "+e.getMessage());
        }

    }

}
