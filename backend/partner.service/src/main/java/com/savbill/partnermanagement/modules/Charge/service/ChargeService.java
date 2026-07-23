package com.savbill.partnermanagement.modules.Charge.service;


import com.savbill.partnermanagement.core.utillity.log.ApplicationLogger;
import com.savbill.partnermanagement.modules.Charge.domain.Charge;
import com.savbill.partnermanagement.modules.Charge.mapper.ChargeMapper;
import com.savbill.partnermanagement.modules.Charge.repocitory.ChargeRepository;
import com.savbill.partnermanagement.modules.ClientServ.repository.ClientServiceRepository;
import com.savbill.partnermanagement.modules.ClientServ.service.ClientServiceSrv;
import com.savbill.partnermanagement.modules.MasterManagement.BusinessUnit.BusinessUnitRepository;
import com.savbill.partnermanagement.modules.partner.repository.PostpaidPlanChargeRepo;
import com.savbill.partnermanagement.modules.Services.ServiceRepository;
import com.savbill.partnermanagement.modules.Tax.domain.Tax;
import com.savbill.partnermanagement.modules.Tax.repository.TaxRepository;
import com.savbill.partnermanagement.modules.Tax.service.TaxService;
import com.savbill.partnermanagement.modules.servicePlan.repository.ServiceChargemappingRepo;
//import com.savbill.partnermanagement.rabbitmq.MessageSender;
import com.savbill.partnermanagement.rabbitmq.product.SaveChargeSharedDataMessage;
import com.savbill.partnermanagement.rabbitmq.product.UpdateChargeSharedDataMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.Map;

@Service
public class ChargeService {

    //    private static final ModelMapper modelMapper = new ModelMapper();
    public Map<String, String> sortColMap = new HashMap<>();

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

//    @Override
//    protected JpaRepository<Charge, Integer> getRepository() {
//        return entityRepository;
//    }


    public void saveChargeData(SaveChargeSharedDataMessage saveChargeSharedDataMessage) {
        ApplicationLogger.logger.info("Partner Service Receive Kafka Message From CMS-Micro-Service  : " + saveChargeSharedDataMessage);
        Charge charge = new Charge();
        charge.setId(saveChargeSharedDataMessage.getId());
        charge.setName(saveChargeSharedDataMessage.getName());
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
        ApplicationLogger.logger.info("Charge saved successfully");
    }

    public void updateChargeData(UpdateChargeSharedDataMessage updateChargeSharedDataMessage) {
        ApplicationLogger.logger.info("Partner Service Receive Kafka Message From CMS-Micro-Service  : " + updateChargeSharedDataMessage);
        Charge charge = chargeRepository.findById(updateChargeSharedDataMessage.getId()).orElse(null);
        if (charge != null) {
            charge.setId(updateChargeSharedDataMessage.getId());
            charge.setName(updateChargeSharedDataMessage.getName());
            charge.setChargetype(updateChargeSharedDataMessage.getChargetype());
            charge.setPrice(updateChargeSharedDataMessage.getPrice());
            Tax tax = taxRepository.findById(updateChargeSharedDataMessage.getTaxId().intValue()).orElse(null);
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
            ApplicationLogger.logger.info("Charge updated successfully");
        }
    }
}
