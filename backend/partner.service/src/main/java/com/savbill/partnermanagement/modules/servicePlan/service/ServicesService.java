package com.savbill.partnermanagement.modules.servicePlan.service;

import com.savbill.partnermanagement.core.service.ExBaseAbstractService;
import com.savbill.partnermanagement.modules.PlanService.PlanService;
import com.savbill.partnermanagement.modules.PlanService.PlanServiceRepository;
import com.savbill.partnermanagement.modules.Services.ServiceRepository;
import com.savbill.partnermanagement.modules.Services.Services;
import com.savbill.partnermanagement.modules.Services.ServicesDTO;
import com.savbill.partnermanagement.modules.servicePlan.mapper.ServicesMapper;
import com.savbill.partnermanagement.rabbitmq.product.SaveServicesSharedDataMessage;
import com.savbill.partnermanagement.rabbitmq.product.UpdateServicesSharedDataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicesService extends ExBaseAbstractService<ServicesDTO, Services, Long> {

@Autowired
PlanServiceRepository serviceRepository;


    public ServicesService(ServiceRepository repository, ServicesMapper mapper) {
        super(repository, mapper);
    }

    private static final Logger logger = LoggerFactory.getLogger(ServicesService.class);

    @Override
    public String getModuleNameForLog() {
        return "[ServicesService]";
    }

    public void saveService(SaveServicesSharedDataMessage servicesSharedDataMessage) {
       try {
           PlanService service = new PlanService();
           service.setId(servicesSharedDataMessage.getId());
           service.setName(servicesSharedDataMessage.getName());
           service.setMvnoId(servicesSharedDataMessage.getMvnoId());
           service.setIcname(servicesSharedDataMessage.getIcname());
           service.setIccode(servicesSharedDataMessage.getIccode());
           service.setBuId(servicesSharedDataMessage.getBuId());
           service.setIsQoSV(servicesSharedDataMessage.getIsQoSV());
           service.setExpiry(servicesSharedDataMessage.getExpiry());
           service.setLedgerId(servicesSharedDataMessage.getLedgerId());
           service.setFeasibility(servicesSharedDataMessage.getFeasibility());
           service.setIs_dtv(servicesSharedDataMessage.getIs_dtv());
           service.setInvestmentid(servicesSharedDataMessage.getInvestmentid());
           service.setServiceParamMappingList(servicesSharedDataMessage.getServiceParamMappingList());
           service.setPoc(servicesSharedDataMessage.getPoc());
           service.setInstallation(servicesSharedDataMessage.getInstallation());
           service.setProvisioning(servicesSharedDataMessage.getProvisioning());
           service.setIsPriceEditable(servicesSharedDataMessage.getIsPriceEditable());
           service.setFeasibilityTeamId(servicesSharedDataMessage.getFeasibilityTeamId());
           service.setPocTeamId(servicesSharedDataMessage.getPocTeamId());
           service.setProvisioningTeamId(servicesSharedDataMessage.getProvisioningTeamId());
           service.setDeleteFlag(servicesSharedDataMessage.getIsDeleted());
           service.setCreatedById(servicesSharedDataMessage.getCreatedById());
           service.setLastModifiedById(servicesSharedDataMessage.getLastModifiedById());

           serviceRepository.save(service);
           logger.info("{} - Saved Service with ID: {} and Name: {}", getModuleNameForLog(), servicesSharedDataMessage.getId(), servicesSharedDataMessage.getName());
       }catch (Exception e){
           logger.error("{} - Error saving Service with ID: {} - {}", getModuleNameForLog(), servicesSharedDataMessage.getId(), e.getMessage(), e);
           throw new RuntimeException("Failed to save service", e);
       }
    }

    public void UpdateService(UpdateServicesSharedDataMessage servicesSharedDataMessage) {
       try{
        PlanService service=serviceRepository.findById(servicesSharedDataMessage.getId()).orElse(null);
        if(service!=null) {
            service.setId(servicesSharedDataMessage.getId());
            service.setName(servicesSharedDataMessage.getName());
            service.setMvnoId(servicesSharedDataMessage.getMvnoId());
            service.setIcname(servicesSharedDataMessage.getIcname());
            service.setIccode(servicesSharedDataMessage.getIccode());
            service.setBuId(servicesSharedDataMessage.getBuId());
            service.setIsQoSV(servicesSharedDataMessage.getIsQoSV());
            service.setExpiry(servicesSharedDataMessage.getExpiry());
            service.setLedgerId(servicesSharedDataMessage.getLedgerId());
            service.setFeasibility(servicesSharedDataMessage.getFeasibility());
            service.setIs_dtv(servicesSharedDataMessage.getIs_dtv());
            service.setInvestmentid(servicesSharedDataMessage.getInvestmentid());
            service.setServiceParamMappingList(servicesSharedDataMessage.getServiceParamMappingList());
            service.setPoc(servicesSharedDataMessage.getPoc());
            service.setInstallation(servicesSharedDataMessage.getInstallation());
            service.setProvisioning(servicesSharedDataMessage.getProvisioning());
            service.setIsPriceEditable(servicesSharedDataMessage.getIsPriceEditable());
            service.setFeasibilityTeamId(servicesSharedDataMessage.getFeasibilityTeamId());
            service.setPocTeamId(servicesSharedDataMessage.getPocTeamId());
            service.setProvisioningTeamId(servicesSharedDataMessage.getProvisioningTeamId());
            service.setDeleteFlag(servicesSharedDataMessage.getIsDeleted());
            service.setIsDeleted(servicesSharedDataMessage.getIsDeleted());
            serviceRepository.save(service);
            logger.info("{} - Updated Service with ID: {} and Name: {}", getModuleNameForLog(), servicesSharedDataMessage.getId(), servicesSharedDataMessage.getName());
        }
        else {
            logger.warn("{} - Service with ID: {} not found for update", getModuleNameForLog(), servicesSharedDataMessage.getId());

        }
       }catch (Exception e){
           logger.error("{} - Error updating Service with ID: {} - {}", getModuleNameForLog(), servicesSharedDataMessage.getId(), e.getMessage(), e);
           throw new RuntimeException("Failed to update service", e);
        }
    }
}
