package com.savbill.salescrmsbss.service;

import com.savbill.salescrmsbss.entity.Area;
import com.savbill.salescrmsbss.rabbitMq.message.SaveAreaSharedDataMessage;
import com.savbill.salescrmsbss.rabbitMq.message.UpdateAreaSharedDataMessage;
import com.savbill.salescrmsbss.repository.AreaRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AreaService {
    @Autowired
    AreaRepository entityRepository;
    //
    private static Log log = LogFactory.getLog(AreaService.class);


    @Transactional
    public void saveAreaEntity(SaveAreaSharedDataMessage message){
        try {
            Area area = new Area();
            area.setId(message.getId());
            area.setName(message.getName());
            area.setStatus(message.getStatus());
            area.setMvnoId(message.getMvnoId());
            area.setCountryId(message.getCountryId());
            area.setStateId(message.getStateId());
            area.setCityId(message.getCityId());
            area.setPincode(message.getPincode());
            area.setIsDeleted(message.getIsDeleted());

            entityRepository.save(area);
        }catch (Exception e){
            log.info("Unable to Create Area with name "+message.getName()+" :"+e.getMessage());
        }


    }
    @Transactional
    public void updateAreaEntity(UpdateAreaSharedDataMessage message){
        try {
            if(message.getId()!=null) {
                Area area = entityRepository.findById(message.getId()).orElse(null);
                if(area!=null) {
                    area.setName(message.getName());
                    area.setStatus(message.getStatus());
                    area.setMvnoId(message.getMvnoId());
                    area.setCountryId(message.getCountryId());
                    area.setStateId(message.getStateId());
                    area.setCityId(message.getCityId());
                    area.setPincode(message.getPincode());
                    area.setIsDeleted(message.getIsDeleted());

                    entityRepository.save(area);
                }else{
//                    log.info("No Data found:");
                    Area area1 = new Area();
                    area1.setId(message.getId());
                    area1.setName(message.getName());
                    area1.setStatus(message.getStatus());
                    area1.setMvnoId(message.getMvnoId());
                    area1.setCountryId(message.getCountryId());
                    area1.setStateId(message.getStateId());
                    area1.setCityId(message.getCityId());
                    area1.setPincode(message.getPincode());
                    area1.setIsDeleted(message.getIsDeleted());

                    entityRepository.save(area1);
                }
            }
        }catch (Exception e){
            log.info("Unable to Create Area with name "+message.getName()+" :"+e.getMessage());
        }



    }
}
