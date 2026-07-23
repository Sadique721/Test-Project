package com.savbill.revenuemanagement.productmanagement.Charge.service;


import com.savbill.revenuemanagement.productmanagement.Charge.domain.Charge;
import com.savbill.revenuemanagement.core.repository.partner.PostpaidPlanChargeRepo;
import com.savbill.revenuemanagement.productmanagement.Charge.repocitory.ChargeRepository;
import com.savbill.revenuemanagement.core.service.AbstractService;
import com.savbill.revenuemanagement.core.service.ClientServ.repository.ClientServiceRepository;
import com.savbill.revenuemanagement.core.service.ClientServ.service.ClientServiceSrv;
import com.savbill.revenuemanagement.productmanagement.Tax.domain.Tax;
import com.savbill.revenuemanagement.productmanagement.Tax.repository.TaxRepository;
import com.savbill.revenuemanagement.productmanagement.Tax.service.TaxService;
import com.savbill.revenuemanagement.mastermanagement.BusinessUnit.repository.BusinessUnitRepository;
import com.savbill.revenuemanagement.productmanagement.Charge.dto.ChargePojo;
import com.savbill.revenuemanagement.productmanagement.Charge.mapper.ChargeMapper;
import com.savbill.revenuemanagement.productmanagement.PlanService.repository.ServiceRepository;
import com.savbill.revenuemanagement.productmanagement.servicePlan.repository.ServiceChargemappingRepo;

import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.SaveChargeSharedDataMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.UpdateChargeSharedDataMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import springfox.documentation.swagger2.mappers.ModelMapper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service

public class ChargeService extends AbstractService<Charge, ChargePojo, Integer> {

    private static final ModelMapper modelMapper = new ModelMapper();

    public ChargeService() {
        sortColMap.put("chargeName", "chargename");
        sortColMap.put("chargeType", "chargetype");
        sortColMap.put("chargeGroup", "chargegroup");
        sortColMap.put("id", "chargeid");
        sortColMap.put("price", "price");
    }

//    @Autowired
//    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private ChargeRepository entityRepository;

    @Autowired
    private PostpaidPlanChargeRepo postpaidPlanChargeRepo;

    @Autowired
    private ChargeMapper chargeMapper;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private TaxService taxService;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceChargemappingRepo serviceChargemappingRepo;

    @PersistenceContext
    private EntityManager entityManager;
//    @Autowired
//    private MessageSender messageSender;
//    @Autowired
//    NotificationTemplateRepository templateRepository;

    @Autowired
    ClientServiceRepository clientServiceRepository;
    @Autowired
    ChargeRepository chargeRepository;

    @Autowired
    private BusinessUnitRepository businessUnitRepository;

    @Autowired
    private TaxRepository taxRepository;
//    @Autowired
//    CreateDataSharedService createDataSharedService;

    public static final String MODULE = "[ChargeService]";

    @Override
    protected JpaRepository<Charge, Integer> getRepository() {
        return entityRepository;
    }


    public void saveChargeData(SaveChargeSharedDataMessage saveChargeSharedDataMessage) {
        Charge charge=new Charge();
        charge.setId(saveChargeSharedDataMessage.getId());
        charge.setName(saveChargeSharedDataMessage.getName());
        charge.setDesc(saveChargeSharedDataMessage.getDesc());
        charge.setChargetype(saveChargeSharedDataMessage.getChargetype());
        charge.setPrice(saveChargeSharedDataMessage.getPrice());
        charge.setTax(taxRepository.findById(saveChargeSharedDataMessage.getTaxId().intValue()).get());
        charge.setDbr(saveChargeSharedDataMessage.getDbr());
        charge.setDiscountid(saveChargeSharedDataMessage.getDiscountid());
        charge.setIsDelete(saveChargeSharedDataMessage.getIsDelete());
        charge.setSaccode(saveChargeSharedDataMessage.getSaccode());
        charge.setServiceList(saveChargeSharedDataMessage.getServiceList());
        charge.setMvnoId(saveChargeSharedDataMessage.getMvnoId());
        charge.setBuId(saveChargeSharedDataMessage.getBuId());
        charge.setService(saveChargeSharedDataMessage.getService());
        charge.setStatus(saveChargeSharedDataMessage.getStatus());
        charge.setLedgerId(saveChargeSharedDataMessage.getLedgerId());
        charge.setRoyalty_payable(saveChargeSharedDataMessage.getRoyalty_payable());
        charge.setBusinessType(saveChargeSharedDataMessage.getBusinessType());
        charge.setPushableLedgerId(saveChargeSharedDataMessage.getPushableLedgerId());
        charge.setCreatedById(saveChargeSharedDataMessage.getCreatedById());
        charge.setLastModifiedById(saveChargeSharedDataMessage.getLastModifiedById());
        chargeRepository.save(charge);
    }

    public void updateChargeData(UpdateChargeSharedDataMessage updateChargeSharedDataMessage) {
        Charge charge=chargeRepository.findById(updateChargeSharedDataMessage.getId()).orElse(null);
        if(charge!=null) {
            charge.setId(updateChargeSharedDataMessage.getId());
            charge.setName(updateChargeSharedDataMessage.getName());
            charge.setChargetype(updateChargeSharedDataMessage.getChargetype());
            charge.setPrice(updateChargeSharedDataMessage.getPrice());
            Tax tax= taxRepository.findById(updateChargeSharedDataMessage.getTaxId().intValue()).orElse(null);
            charge.setTax(tax);
            charge.setDbr(updateChargeSharedDataMessage.getDbr());
            charge.setDiscountid(updateChargeSharedDataMessage.getDiscountid());
            charge.setIsDelete(updateChargeSharedDataMessage.getIsDelete());
            charge.setSaccode(updateChargeSharedDataMessage.getSaccode());
            charge.setServiceList(updateChargeSharedDataMessage.getServiceList());
            charge.setMvnoId(updateChargeSharedDataMessage.getMvnoId());
            charge.setBuId(updateChargeSharedDataMessage.getBuId());
            charge.setService(updateChargeSharedDataMessage.getService());
            charge.setStatus(updateChargeSharedDataMessage.getStatus());
            charge.setLedgerId(updateChargeSharedDataMessage.getLedgerId());
            charge.setRoyalty_payable(updateChargeSharedDataMessage.getRoyalty_payable());
            charge.setBusinessType(updateChargeSharedDataMessage.getBusinessType());
            charge.setPushableLedgerId(updateChargeSharedDataMessage.getPushableLedgerId());
            charge.setCreatedById(updateChargeSharedDataMessage.getCreatedById());
            charge.setLastModifiedById(updateChargeSharedDataMessage.getLastModifiedById());
            chargeRepository.save(charge);
        }
    }
}
