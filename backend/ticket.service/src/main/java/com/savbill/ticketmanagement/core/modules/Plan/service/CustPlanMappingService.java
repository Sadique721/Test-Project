package com.savbill.ticketmanagement.core.modules.Plan.service;


import com.savbill.ticketmanagement.core.dto.CustPlanMapppingDto;
import com.savbill.ticketmanagement.core.modules.Plan.domain.CustPlanMappping;
import com.savbill.ticketmanagement.core.modules.Plan.domain.CustPlanMapppingPojo;
import com.savbill.ticketmanagement.core.modules.Plan.repository.CustPlanMappingRepository;
import com.savbill.ticketmanagement.core.service.AbstractService;
import com.savbill.ticketmanagement.kafka.KafkaMessageSender;
import com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage.UpdateCustplanMappingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustPlanMappingService extends AbstractService<CustPlanMappping, CustPlanMapppingPojo, Long> {

    private static final Logger logger = LoggerFactory.getLogger(CustPlanMappingService.class);

    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    private CustPlanMappingRepository custPlanMapppingRepository;

    @Autowired
    KafkaMessageSender kafkaMessageSender;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    protected JpaRepository<CustPlanMappping, Long> getRepository() {
        return null;
    }
    public CustPlanMappping save(CustPlanMappping entity, String operation) {
        CustPlanMappping custPlanMappping = custPlanMapppingRepository.save(entity);
        CustPlanMapppingPojo custPlanMapppingPojo = convertDomainToDto(custPlanMappping);
        return custPlanMappping;
    }

    public CustPlanMapppingPojo convertDomainToDto(CustPlanMappping custPlanMappping) {
        CustPlanMapppingPojo pojo = new CustPlanMapppingPojo();
        if (custPlanMappping != null) {
            pojo.setId(custPlanMappping.getId());
            pojo.setPlanId(custPlanMappping.getPlanId());
            pojo.setCustid(custPlanMappping.getCustid());
            pojo.setStartDate(custPlanMappping.getStartDate());
            pojo.setEndDate(custPlanMappping.getEndDate());
            pojo.setExpiryDate(custPlanMappping.getExpiryDate());
            pojo.setStatus(custPlanMappping.getStatus());
            //         pojo.setQospolicyId(null != custPlanMappping.getQospolicy() ? custPlanMappping.getQospolicy().getId() : null);
            pojo.setService(custPlanMappping.getService());
            pojo.setIsDelete(custPlanMappping.getIsDelete());
            //       pojo.setQuotaList(custQuotaService.convertQuotaDomainListToQuotaPojoList(custPlanMappping.getQuotaList()));
            pojo.setCreditdocid(custPlanMappping.getCreditdocid());
            pojo.setCustPlanStatus(custPlanMappping.getCustPlanStatus());
        }
        return pojo;
    }

    public void updateCustPlanMapping(UpdateCustplanMappingMessage message) {
        try {
            List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();
            for (CustPlanMapppingDto dto : message.getCustPlanMapppingDtos()) {
                CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(dto.getId());
                custPlanMappping.setStartDate(LocalDateTime.parse(dto.getStartDateString(), formatter2));
                custPlanMappping.setEndDate(LocalDateTime.parse(dto.getEndDateString(), formatter));
                custPlanMappping.setExpiryDate(LocalDateTime.parse(dto.getExpirydateString(), formatter));
                custPlanMappping.setCustPlanStatus(dto.getCustPlanStatus());
                custPlanMapppingList.add(custPlanMappping);
            }
            custPlanMapppingRepository.saveAll(custPlanMapppingList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
