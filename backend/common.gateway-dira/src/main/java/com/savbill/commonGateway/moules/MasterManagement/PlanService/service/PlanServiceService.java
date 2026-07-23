package com.savbill.commonGateway.moules.MasterManagement.PlanService.service;

import com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages.SaveServicesSharedDataMessage;
import com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages.UpdateServicesSharedDataMessage;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.moules.MasterManagement.PlanService.domain.PlanService;
import com.savbill.commonGateway.moules.MasterManagement.PlanService.repository.PlanServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanServiceService {



    @Autowired
    PlanServiceRepository planServiceRepository;
    private static final Logger logger = LoggerFactory.getLogger(PlanService.class);

    public void savePlanServiceEntity(SaveServicesSharedDataMessage message) throws Exception{
        try {
            PlanService planService = new PlanService();
            planService.setId(message.getId());
            planService.setName(message.getName());
            planService.setMvnoId(message.getMvnoId());
            planService.setBuId(message.getBuId());
            planService.setIs_dtv(message.getIs_dtv());
            planService.setCreatedById(message.getCreatedById());
            planService.setLastModifiedById(message.getLastModifiedById());
            planServiceRepository.save(planService);
            logger.info("Services details created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create services details with name " + message.getName(), e.getMessage());
        }
    }

    public void updatePlanServiceEntity(UpdateServicesSharedDataMessage message) throws Exception{
        try {
            PlanService planService = planServiceRepository.findById(message.getId()).orElse(null);
            if(planService != null) {
                planService.setId(message.getId());
                planService.setName(message.getName());
                planService.setMvnoId(message.getMvnoId());
                planService.setBuId(message.getBuId());
                planService.setCreatedById(message.getCreatedById());
                planService.setLastModifiedById(message.getLastModifiedById());
                planService.setIs_dtv(message.getIs_dtv());
                planService.setIsDeleted(message.getIsDeleted());
               // if (message.getIsDeleted().equals(false)) {
                    planServiceRepository.save(planService);
//                } else if (message.getIsDeleted().equals(true)) {
//                    planServiceRepository.deleteById(message.getId());
//                }

                logger.info("Services details updated successfully with name " + message.getName());
            } else {
                PlanService planService1 = new PlanService();
                planService1.setId(message.getId());
                planService1.setName(message.getName());
                planService1.setMvnoId(message.getMvnoId());
                planService1.setBuId(message.getBuId());
                planService1.setIs_dtv(message.getIs_dtv());
                planService1.setCreatedById(message.getCreatedById());
                planService1.setLastModifiedById(message.getLastModifiedById());
                planServiceRepository.save(planService1);
                logger.info("Services details updated successfully with name " + message.getName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update services details with name " + message.getName(), e.getMessage());
        }
    }
}
