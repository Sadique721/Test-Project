package com.savbill.inventorymanagement.modules.CustPlanMapping;


import com.savbill.inventorymanagement.core.dto.CustPlanMapppingDto;
import com.savbill.inventorymanagement.core.service.AbstractService;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.UpdateCustplanMappingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustPlanMappingService extends AbstractService<CustPlanMappping, CustPlanMapppingPojo, Long> {

    private static final Logger logger = LoggerFactory.getLogger(CustPlanMappingService.class);

    @Autowired
    private CustPlanMapppingRepository custPlanMappingRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    protected JpaRepository<CustPlanMappping, Long> getRepository() {
        return null;
    }
    public CustPlanMappping save(CustPlanMappping entity, String operation) {
        CustPlanMappping custPlanMappping = custPlanMappingRepository.save(entity);
        CustPlanMapppingPojo custPlanMapppingPojo = convertDomainToDto(custPlanMappping);
        return custPlanMappping;
    }

    public CustPlanMapppingPojo convertDomainToDto(CustPlanMappping custPlanMappping) {
        CustPlanMapppingPojo pojo = new CustPlanMapppingPojo();
        if (custPlanMappping != null) {
            pojo.setId(custPlanMappping.getId());
            pojo.setPlanId(custPlanMappping.getPlanId());
            pojo.setCustid(custPlanMappping.getCustId());
            pojo.setStartDate(custPlanMappping.getStartDate());
            pojo.setEndDate(custPlanMappping.getEndDate());
            pojo.setExpiryDate(custPlanMappping.getExpiryDate());
            pojo.setStatus(custPlanMappping.getStatus());
            //         pojo.setQospolicyId(null != custPlanMappping.getQospolicy() ? custPlanMappping.getQospolicy().getId() : null);
            pojo.setService(custPlanMappping.getService());
            pojo.setIsDelete(custPlanMappping.getIsDelete());
            //       pojo.setQuotaList(custQuotaService.convertQuotaDomainListToQuotaPojoList(custPlanMappping.getQuotaList()));
            pojo.setCustPlanStatus(custPlanMappping.getCustPlanStatus());
        }
        return pojo;
    }

    public void updateCustPlanMapping(UpdateCustplanMappingMessage message) {
        try {
            List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();
            for (CustPlanMapppingDto dto : message.getCustPlanMapppingDtos()) {
                CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(dto.getId()).get();
                custPlanMappping.setStartDate(LocalDateTime.parse(dto.getStartDateString(), formatter2));
                custPlanMappping.setEndDate(LocalDateTime.parse(dto.getEndDateString(), formatter));
                custPlanMappping.setExpiryDate(LocalDateTime.parse(dto.getExpirydateString(), formatter));
                custPlanMappping.setCustPlanStatus(dto.getCustPlanStatus());
                custPlanMapppingList.add(custPlanMappping);
            }
            custPlanMappingRepository.saveAll(custPlanMapppingList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
