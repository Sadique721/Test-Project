package com.savbill.revenuemanagement.productmanagement.servicePlan.service;

import com.savbill.revenuemanagement.core.service.ExBaseAbstractService;
import com.savbill.revenuemanagement.productmanagement.PlanService.domain.PlanService;
import com.savbill.revenuemanagement.productmanagement.PlanService.domain.Services;
import com.savbill.revenuemanagement.productmanagement.PlanService.repository.PlanServiceRepository;
import com.savbill.revenuemanagement.productmanagement.PlanService.repository.ServiceRepository;
import com.savbill.revenuemanagement.productmanagement.servicePlan.mapper.ServicesMapper;
import com.savbill.revenuemanagement.productmanagement.servicePlan.model.ServicesDTO;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.SaveServicesSharedDataMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.UpdateServicesSharedDataMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicesService extends ExBaseAbstractService<ServicesDTO, Services, Long> {

@Autowired
PlanServiceRepository serviceRepository;


    public ServicesService(ServiceRepository repository, ServicesMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ServicesService]";
    }

    public void saveService(SaveServicesSharedDataMessage servicesSharedDataMessage) {
        PlanService service=new PlanService();
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
    }

    public void UpdateService(UpdateServicesSharedDataMessage servicesSharedDataMessage) {
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
        }
    }

//    @Override
//    public void excelGenerate(Workbook workbook) throws Exception {
//        Sheet sheet = workbook.createSheet("Services");
//        createExcel(workbook, sheet, ServicesDTO.class, null);
//    }
//
//    @Override
//    public void pdfGenerate(Document doc) throws Exception {
//        createPDF(doc, ServicesDTO.class, null);
//    }
}
