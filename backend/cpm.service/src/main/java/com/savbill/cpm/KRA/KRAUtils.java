package com.savbill.cpm.KRA;

import com.savbill.cpm.KRA.dtos.ETimsCustomerListDTO;
import com.savbill.cpm.KRA.dtos.ETimsItemListDTO;
import com.savbill.cpm.constants.ClientServiceConstant;
import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.kafka.KafkaMessageData;
import com.savbill.cpm.kafka.KafkaMessageSender;
import com.savbill.cpm.model.common.ClientService;
import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.model.postpaid.Charge;
import com.savbill.cpm.model.postpaid.CustomerMapper;
import com.savbill.cpm.model.postpaid.PostpaidPlan;
import com.savbill.cpm.model.postpaid.PostpaidPlanMapper;
import com.savbill.cpm.model.postpaid.TaxTypeTier;
import com.savbill.cpm.modules.StaffUserService.Service.StaffUserServiceService;
import com.savbill.cpm.pojo.api.CustomerAddressPojo;
import com.savbill.cpm.pojo.api.CustomersPojo;
import com.savbill.cpm.pojo.api.PostpaidPlanPojo;
import com.savbill.cpm.KRA.dtos.ETimsCustomerDTO;
import com.savbill.cpm.KRA.dtos.ETimsItemDTO;
import com.savbill.cpm.repository.postpaid.PostpaidPlanRepo;
import com.savbill.cpm.repository.postpaid.TaxTypeTierRepository;
import com.savbill.cpm.repository.radius.CustomersRepository;
import com.savbill.cpm.service.common.ClientServiceSrv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Component
public class KRAUtils {
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    ClientServiceSrv clientService;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;
    @Autowired
    private PostpaidPlanMapper postpaidPlanMapper;
    @Autowired
    private TaxTypeTierRepository taxTypeTierRepository;
    private static final Logger logger = LoggerFactory.getLogger(StaffUserServiceService.class);

    public void processEtimsAddCustomer(List<CustomersPojo> customersPojoList, HttpServletRequest req) {

        ETimsCustomerListDTO eTimsCustomerListDTO = new ETimsCustomerListDTO();
        List<ETimsCustomerDTO> eTimsCustomerDTOList = new ArrayList<>();
        for(CustomersPojo customersPojo:customersPojoList) {
            if (customersPojo == null) {
                return;
            }
            ETimsCustomerDTO dto = new ETimsCustomerDTO();
            dto.setCustomerNo(String.valueOf(customersPojo.getId()));
            dto.setCustomerTin(customersPojo.getPan()!=null?customersPojo.getPan():null);
            dto.setCustomerName(customersPojo.getUsername());
            CustomerAddressPojo address = customersPojo.getAddressList().stream().findFirst().orElse(null);

            if (address != null) {
                dto.setAddress(address.getLandmark()); // or address.getAddress1()
            }
            dto.setTelNo(customersPojo.getMobile());
            dto.setEmail(customersPojo.getEmail());
            dto.setFaxNo(customersPojo.getFax());
            dto.setRemark(customersPojo.getRemarks());
            dto.setMvnoId(customersPojo.getMvnoId());
            dto.setIsUsed(true);
            logger.info("Customer DTO: {}", dto);
            eTimsCustomerDTOList.add(dto);
        }
        eTimsCustomerListDTO.setETimsCustomerListDTO(eTimsCustomerDTOList);
        kafkaMessageSender.send(new KafkaMessageData(eTimsCustomerListDTO, ETimsCustomerListDTO.class.getSimpleName(),KRAConstant.ADD_CUSTOMER));
    }

    public void syncCustomersToKRA() {
        logger.info("[KRAUtils] syncCustomersToKRA - Fetching unsynced active customers from DB");
        List<Customers> unsyncedCustomers = customersRepository.findAllUnsyncedActiveCustomers();
        if (CollectionUtils.isEmpty(unsyncedCustomers)) {
            logger.info("[KRAUtils] syncCustomersToKRA - No unsynced customers found, skipping.");
            return;
        }
        logger.info("[KRAUtils] syncCustomersToKRA - Found {} unsynced customers", unsyncedCustomers.size());
        List<CustomersPojo> listPojo = new ArrayList<>();
        for (Customers customer : unsyncedCustomers) {
            CustomersPojo pojo = customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext());
            listPojo.add(pojo);
        }
        processEtimsAddCustomer(listPojo, null);
        logger.info("[KRAUtils] syncCustomersToKRA - Submitted {} customers to E-TIMS", listPojo.size());
    }

    public void syncPlansToKRA() {
        logger.info("[KRAUtils] syncPlansToKRA - Fetching unsynced active plans from DB");
        List<PostpaidPlan> unsyncedPlans = postpaidPlanRepo.findAllUnsyncedActivePlans();
        if (CollectionUtils.isEmpty(unsyncedPlans)) {
            logger.info("[KRAUtils] syncPlansToKRA - No unsynced plans found, skipping.");
            return;
        }
        logger.info("[KRAUtils] syncPlansToKRA - Found {} unsynced plans", unsyncedPlans.size());
        List<PostpaidPlanPojo> listPojo = new ArrayList<>();
        for (PostpaidPlan plan : unsyncedPlans) {
            try {
                PostpaidPlanPojo pojo = postpaidPlanMapper.domainToDTO(plan, new CycleAvoidingMappingContext());
                listPojo.add(pojo);
            } catch (Exception e) {
                logger.error("[KRAUtils] syncPlansToKRA - Error mapping planId={}: {}", plan.getId(), e.getMessage());
            }
        }
        if (!CollectionUtils.isEmpty(listPojo)) {
            processEtimsAddItemsListBatch(listPojo);
            logger.info("[KRAUtils] syncPlansToKRA - Submitted {} plans to E-TIMS", listPojo.size());
        }
    }

    public void processEtimsAddItemsListBatch(List<PostpaidPlanPojo> postpaidPlanPojos) {
        ETimsItemListDTO eTimsItemListDTO=new ETimsItemListDTO();
        List<ETimsItemDTO>dtos=new ArrayList<>();
        for(PostpaidPlanPojo postpaidPlanPojo :postpaidPlanPojos) {


            ETimsItemDTO dto = new ETimsItemDTO();
            dto.setItemCode(String.valueOf(postpaidPlanPojo.getId()));
            dto.setItemClassifiCode("81112101");
            dto.setItemTypeCode("3");
            dto.setItemName(postpaidPlanPojo.getName());
            dto.setItemStrdName(postpaidPlanPojo.getName());
            dto.setCountryCode("KE");
            dto.setPkgUnitCode("NT");
            dto.setQtyUnitCode("U");
            dto.setTaxTypeCode("B");


            dto.setBatchNo(null);
            dto.setBarcode(null);

            Double offerPrice = postpaidPlanPojo.getOfferprice();
            dto.setUnitPrice(offerPrice);
            dto.setGroup1UnitPrice(offerPrice);
            dto.setGroup2UnitPrice(offerPrice);
            dto.setGroup3UnitPrice(offerPrice);
            dto.setGroup4UnitPrice(offerPrice);
            dto.setGroup5UnitPrice(offerPrice);

            dto.setAdditionalInfo(postpaidPlanPojo.getDesc());
            dto.setMvnoId(postpaidPlanPojo.getMvnoId());
            Double qty = 1.0;
            dto.setSaftyQuantity(qty);
            dto.setPackageQuantity(qty.intValue());
            dto.setIsInrcApplicable(false);
            dto.setIsUsed(true);
            dtos.add(dto);

            logger.info("Item DTO: {}", dto);

        }
        eTimsItemListDTO.setResponesDTO(dtos);

        kafkaMessageSender.send(new KafkaMessageData(eTimsItemListDTO,eTimsItemListDTO.getClass().getSimpleName(),KRAConstant.ADD_ITEMS));
    }

    public void processEtimsAddChargeItemsListBatch(List<Charge> charges) {
        ETimsItemListDTO eTimsItemListDTO = new ETimsItemListDTO();
        List<ETimsItemDTO> dtos = new ArrayList<>();
        for (Charge charge : charges) {
            ETimsItemDTO dto = new ETimsItemDTO();
            dto.setItemCode("Charge_" + charge.getId());
            dto.setItemClassifiCode("81112101");
            dto.setItemTypeCode("3");
            dto.setItemName(charge.getName());
            dto.setItemStrdName(charge.getName());
            dto.setCountryCode("KE");
            dto.setPkgUnitCode("NT");
            dto.setQtyUnitCode("U");
            dto.setTaxTypeCode("B");
            dto.setBatchNo(null);
            dto.setBarcode(null);

            double finalPrice = charge.getActualprice();
            if (charge.getTax() != null && charge.getTax().getId() != null) {
                List<TaxTypeTier> taxList = taxTypeTierRepository.getTaxRateList(charge.getTax().getId());
                if (taxList != null) {
                    for (TaxTypeTier taxTier : taxList) {
                        if (taxTier.getRate() != null) {
                            finalPrice += (finalPrice * taxTier.getRate() / 100.0);
                        }
                    }
                }
            }

            dto.setUnitPrice(finalPrice);
            dto.setGroup1UnitPrice(finalPrice);
            dto.setGroup2UnitPrice(finalPrice);
            dto.setGroup3UnitPrice(finalPrice);
            dto.setGroup4UnitPrice(finalPrice);
            dto.setGroup5UnitPrice(finalPrice);
            dto.setAdditionalInfo(charge.getDesc());
            dto.setMvnoId(charge.getMvnoId());
            Double qty = 1.0;
            dto.setSaftyQuantity(qty);
            dto.setPackageQuantity(qty.intValue());
            dto.setIsInrcApplicable(false);
            dto.setIsUsed(true);
            dtos.add(dto);

            logger.info("Charge Item DTO: {}", dto);
        }
        eTimsItemListDTO.setResponesDTO(dtos);

        KafkaMessageData kafkaMessageData = new KafkaMessageData(eTimsItemListDTO, eTimsItemListDTO.getClass().getSimpleName(), KRAConstant.ADD_CHARGE);
        kafkaMessageSender.send(kafkaMessageData);

        logger.info("Charge Kafka payload: {}", kafkaMessageData);
    }

    public void processEtimsUpdateItemsListBatch(PostpaidPlanPojo postpaidPlanPojo) {
        if (postpaidPlanPojo == null) {
            return;
        }

        ETimsItemDTO dto = new ETimsItemDTO();

        dto.setItemCode(String.valueOf(postpaidPlanPojo.getId()));
        dto.setItemClassifiCode("81112101");
        dto.setItemTypeCode("3");
        dto.setItemName(postpaidPlanPojo.getName());
        dto.setItemStrdName(postpaidPlanPojo.getName());
        dto.setCountryCode("KE");
        dto.setPkgUnitCode("NT");
        dto.setQtyUnitCode("U");
        dto.setTaxTypeCode("B");
        dto.setBatchNo(null);
        dto.setBarcode(null);
        Double offerPrice = postpaidPlanPojo.getOfferprice();
        dto.setUnitPrice(offerPrice);
        dto.setGroup1UnitPrice(offerPrice);
        dto.setGroup2UnitPrice(offerPrice);
        dto.setGroup3UnitPrice(offerPrice);
        dto.setGroup4UnitPrice(offerPrice);
        dto.setGroup5UnitPrice(offerPrice);

        dto.setAdditionalInfo(postpaidPlanPojo.getDesc());
        dto.setMvnoId(postpaidPlanPojo.getMvnoId());
        Double qty = 1.0;
        dto.setSaftyQuantity(qty);
        dto.setPackageQuantity(qty.intValue());
        dto.setIsInrcApplicable(false);
        dto.setIsUsed(true);

        logger.info("Item DTO: {}", dto);

        kafkaMessageSender.send(new KafkaMessageData(dto, ETimsItemDTO.class.getSimpleName(),KRAConstant.UPDATE_ITEMS));
    }
}



