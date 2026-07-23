package com.savbill.inventorymanagement.modules.PlanService;

import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.SaveServicesSharedDataMessage;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.UpdateServicesSharedDataMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanServiceService extends ExBaseAbstractService<PlanServiceDto, PlanService, Integer> {

    public PlanServiceService(PlanServiceRepository repository, PlanServiceMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[PlanServiceService]";
    }

    @Autowired
    PlanServiceRepository planServiceRepository;
    private static final Logger logger = Logger.getLogger(PlanService.class);

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
            planService.setProductCategories(message.getProductCategories());
            planServiceRepository.save(planService);
            logger.info("Services details created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create services details with name " + message.getName() + " , Error: " + e.getMessage());
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
                planService.setProductCategories(message.getProductCategories());
                if (message.getIsDeleted().equals(false)) {
                    planServiceRepository.save(planService);
                } else if (message.getIsDeleted().equals(true)) {
                    planServiceRepository.deleteById(message.getId());
                }
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
                planService1.setProductCategories(message.getProductCategories());
                planServiceRepository.save(planService1);
                logger.info("Services details updated successfully with name " + message.getName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update services details with name " + message.getName() + " , Error: " + e.getMessage());
        }
    }
}
