package com.savbill.taskmanagement.core.modules.PlanService.service;

import com.savbill.taskmanagement.core.exceptions.CustomValidationException;
import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.PlanService.domain.PlanService;
import com.savbill.taskmanagement.core.modules.PlanService.dto.PlanServiceDto;
import com.savbill.taskmanagement.core.modules.PlanService.repository.PlanServiceRepository;
import com.savbill.taskmanagement.core.service.ExBaseAbstractService;
import com.savbill.taskmanagement.core.utillity.log.ApplicationLogger;
import com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage.SaveServicesSharedDataMessage;
import com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage.UpdateServicesSharedDataMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlanServicesService extends ExBaseAbstractService<PlanServiceDto, PlanService, Integer> {
    public PlanServicesService(JpaRepository<PlanService, Integer> repository, IBaseMapper<PlanServiceDto, PlanService> mapper) {
        super(repository, mapper);
    }


//    public PlanServicesService(JpaRepository<PlanService, Integer> repository, IBaseMapper<PlanServiceDto, PlanService> mapper) {
//        super(repository, mapper);
//    }

    @Override
    public String getModuleNameForLog() {
        return "[PlanServiceService]";
    }

    @Autowired
    PlanServiceRepository planServiceRepository;

@Transactional
    public void savePlanServiceEntity(SaveServicesSharedDataMessage message) throws Exception {
        try {
            PlanService planService = new PlanService();
            planService.setId(message.getId());
            planService.setName(message.getName());
            planService.setMvnoId(message.getMvnoId());
            planService.setBuId(message.getBuId());
            planService.setIs_dtv(message.getIs_dtv());
            planService.setServiceParamMappingList(message.getServiceParamMappingList());

            planServiceRepository.save(planService);
            ApplicationLogger.logger.info("Services details created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error("Unable to create services details with name " + message.getName(), e.getMessage());
        }
    }
@Transactional
    public void updatePlanServiceEntity(UpdateServicesSharedDataMessage message) throws Exception {
        try {
            PlanService planService = planServiceRepository.findById(message.getId()).orElse(null);
            planService.setId(message.getId());
            planService.setName(message.getName());
            planService.setMvnoId(message.getMvnoId());
            planService.setBuId(message.getBuId());
            planService.setIs_dtv(message.getIs_dtv());
            planService.setServiceParamMappingList(message.getServiceParamMappingList());

            if (message.getIsDeleted().equals(false)) {
                planServiceRepository.save(planService);
            } else if (message.getIsDeleted().equals(true)) {
                planServiceRepository.deleteById(message.getId());
            }
            ApplicationLogger.logger.info("Services details updated successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error("Unable to update services details with name " + message.getName(), e.getMessage());
        }
    }
}
