package com.savbill.taskmanagement.core.modules.TicketRemark.service;


import com.savbill.taskmanagement.core.controller.APIResponseController;
import com.savbill.taskmanagement.core.modules.ClientServ.service.ClientServiceSrv;
import com.savbill.taskmanagement.core.modules.Customers.domain.Customers;
import com.savbill.taskmanagement.core.modules.Customers.dto.CustomerPlanDTO;
import com.savbill.taskmanagement.core.modules.Customers.dto.CustomerServiceDTO;
import com.savbill.taskmanagement.core.modules.Customers.dto.CustomerServicePlanDTO;
import com.savbill.taskmanagement.core.modules.Customers.repository.CustomerRepository;
import com.savbill.taskmanagement.core.modules.Plan.domain.CustPlanMappping;
import com.savbill.taskmanagement.core.modules.PlanService.domain.CustomerServiceMapping;
import com.savbill.taskmanagement.core.modules.TicketRemark.controller.TicketRemarkController;
import com.savbill.taskmanagement.core.modules.TicketRemark.domain.QTicketRemark;
import com.savbill.taskmanagement.core.modules.TicketRemark.domain.TicketRemark;
import com.savbill.taskmanagement.core.modules.TicketRemark.model.TicketRemarkDTO;
import com.savbill.taskmanagement.core.modules.TicketRemark.repository.TicketRemarkRepository;
import com.savbill.taskmanagement.core.modules.tasks.domain.Case;
import com.savbill.taskmanagement.core.modules.tasks.domain.QCase;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseRepository;
import com.savbill.taskmanagement.core.modules.utils.APIConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class TicketRemarkService {

    private static final Logger logger = LoggerFactory.getLogger(TicketRemarkService.class);

    @Autowired
    private TicketRemarkController ticketRemarkController;

    @Autowired
    private APIResponseController responseController;

    @Autowired
    private TicketRemarkRepository ticketRemarkRepository;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private CustomerRepository customersRepository;

    @Autowired
    private ClientServiceSrv clientServiceSrv;


    public ResponseEntity<Map<String, Object>> saveRemark(TicketRemarkDTO ticketRemarkDTO) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_VALIDATE);
        try {
            if(Objects.isNull(ticketRemarkDTO.getIsFromCustomer())){
                response.put(APIConstants.MESSAGE, "Is_From_Customer  is NULL");
                logger.info("Is_From_Customer is NULL");
                return responseController.apiResponse(469, response);
            }

            if (Objects.isNull(ticketRemarkDTO.getTicketId())) {
                response.put(APIConstants.MESSAGE, "Ticket Id is NULL");
                logger.info("Ticket Id is NULL");
                return responseController.apiResponse(470, response);
            }

            if(ticketRemarkDTO.getIsFromCustomer().equals(false)) {
                if (Objects.isNull(ticketRemarkDTO.getStaffId())) {
                    response.put(APIConstants.MESSAGE, "Staff Id is NULL");
                    logger.info("Staff Id is NULL");
                    return responseController.apiResponse(472, response);
                }
            }

            if(ticketRemarkDTO.getIsFromCustomer().equals(true)) {
                if(Objects.isNull(ticketRemarkDTO.getExternalRemarks())){
                    response.put(APIConstants.MESSAGE, "External Remark is NULL");
                    logger.info("External Remark is NULL");
                    return responseController.apiResponse(473, response);
                }
            }

            /**Seprating Both staff and customer message send **/
            if(ticketRemarkDTO.getIsFromCustomer().equals(false)){
                if(Objects.nonNull(ticketRemarkDTO.getExternalRemarks()) && !ticketRemarkDTO.getExternalRemarks().isEmpty()) {
                    TicketRemark ticketRemark = DtoToDomain(ticketRemarkDTO);
                    ticketRemarkRepository.save(ticketRemark);
                    response.put(APIConstants.MESSAGE , "Success");
                    logger.info("Remark added for " + ticketRemarkDTO.getTicketId());
                    return responseController.apiResponse(200, response);
                }
                else{
                    TicketRemark ticketRemark = DtoToDomain(ticketRemarkDTO);
                    ticketRemarkRepository.save(ticketRemark);
                    response.put(APIConstants.MESSAGE , "Success");
                    logger.info("Remark added for " + ticketRemarkDTO.getTicketId());
                    return responseController.apiResponse(200, response);
                }
            }
            if(ticketRemarkDTO.getIsFromCustomer().equals(true)){
                TicketRemark ticketRemark = DtoToDomain(ticketRemarkDTO);
                response.put(APIConstants.MESSAGE , "Success");
                ticketRemarkRepository.save(ticketRemark);
                logger.info("Remark added for " + ticketRemarkDTO.getTicketId());
                return responseController.apiResponse(200, response);
            }
            else {
                response.put(APIConstants.MESSAGE, "Server error");
                logger.error("Server error adding remark ");
                return responseController.apiResponse(490, response);
            }
        } catch (Throwable e) {
            logger.equals("Error while add remark for: " + ticketRemarkDTO.getTicketId());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(APIConstants.TYPE);
        }

    }

    public TicketRemark DtoToDomain(TicketRemarkDTO ticketRemarkDTO){

        TicketRemark ticketRemark = new TicketRemark();

        if(Objects.nonNull(ticketRemarkDTO.getTicketId())){
            ticketRemark.setTicketId(ticketRemarkDTO.getTicketId());
            //Integer custId = getCustIdFromTicketId(ticketRemarkDTO.getTicketId()).get(0).getStaffUser().getId();
            //ticketRemark.setCustId(custId);
            String ticketNo = getCustIdFromTicketId(ticketRemarkDTO.getTicketId()).get(0).getCaseNumber();
            ticketRemark.setTicketNo(ticketNo);
        }

        if(Objects.nonNull(ticketRemarkDTO.getExternalRemarks())){
            ticketRemark.setExternalRemarks(ticketRemarkDTO.getExternalRemarks());
        }

        if(Objects.nonNull(ticketRemarkDTO.getInternalRemarks())){
            ticketRemark.setInternalRemarks(ticketRemarkDTO.getInternalRemarks());
        }

        if(Objects.nonNull(ticketRemarkDTO.getStaffId())){
            ticketRemark.setStaffId(ticketRemarkDTO.getStaffId());
        }
        if(Objects.nonNull(ticketRemarkDTO.getIsFromCustomer())){
            ticketRemark.setIsFromCustomer(ticketRemarkDTO.getIsFromCustomer());
        }
        ticketRemark.setCreatedate(LocalDateTime.now());
        return ticketRemark;

    }

    public List<Case> getCustIdFromTicketId(Long ticketId){
        QCase qCase=QCase.case$;
        BooleanExpression booleanExpression=qCase.isNotNull().and(qCase.caseId.eq(ticketId));
        List<Case> caseList= IterableUtils.toList(caseRepository.findAll(booleanExpression));
        return caseList;
    }

    public String saveRemarkFromMail(TicketRemarkDTO ticketRemarkDTO){
        String response = "error";
        try {
            TicketRemark ticketRemark = DtoToDomain(ticketRemarkDTO);
            ticketRemarkRepository.save(ticketRemark);
            response = "success";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;

    }

    public List<TicketRemark> fetchTicketRemark(Long ticketId){
        List<TicketRemark> ticketRemarkList = new ArrayList<>();
        QTicketRemark qTicketRemark  = QTicketRemark.ticketRemark;
        BooleanExpression booleanExpression = qTicketRemark.isNotNull().and(qTicketRemark.ticketId.eq(ticketId));
        ticketRemarkList = IterableUtils.toList(ticketRemarkRepository.findAll(booleanExpression));
        return  ticketRemarkList;

    }

    public  <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        final Set<Object> seen = new HashSet<>();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public Boolean isCommonDomain(String domain){
        boolean flag  = false;
        List<TicketRemark> ticketRemarkList = ticketRemarkRepository.findAll();
        logger.info("ticketRemarkList"+ticketRemarkList);
        if(!ticketRemarkList.isEmpty()){
            for(String testdomain : ticketRemarkList.stream().map(ticketRemark -> ticketRemark.getCommonDomain()).collect(Collectors.toList())){
                if(domain.contains(testdomain)){
                    flag = true;
                }
            }
        }
        logger.info("flag in common domain"+flag);
        return  flag;
    }

    public List<Customers> getCustomerListFromEmail(String email , Integer mvnoId){
        List<Customers> customersList = new ArrayList<>();
        customersList = customersRepository.findAllByEmailAndMvnoId(email , mvnoId);
        logger.info("customer list found with email");
        if(customersList.isEmpty()){
            String domain = email.substring(email.indexOf("@") + 1 , email.length());
            if(!isCommonDomain(domain)){
                customersList = customersRepository.findAllByDomainAndMvnoId(domain,mvnoId);
                logger.info("customer list found with domain");
            }
        }
        return customersList;
    }

    public CustomerServicePlanDTO getCustomerServicePlan(Customers customers){
        List<CustomerServicePlanDTO> customerServicePlanDTOList = new ArrayList<>();
        CustomerServicePlanDTO customerServicePlanDTO = new CustomerServicePlanDTO();
        if(!customers.getPlanMappingList().isEmpty()){
            List<CustomerPlanDTO> customerPlanDTOList = new ArrayList<>();
            List<CustomerServiceDTO> customerServiceDTOList =  new ArrayList<>();
            for(CustPlanMappping custPlanMappping : customers.getPlanMappingList()){
                CustomerPlanDTO customerPlanDTO = new CustomerPlanDTO();
                customerPlanDTO.setPlanId(custPlanMappping.getPlanId());
                customerPlanDTOList.add(customerPlanDTO);
                customerPlanDTOList = customerPlanDTOList.stream().filter(distinctByKey(customerPlanDTO1 -> customerPlanDTO1.getPlanId())).collect(Collectors.toList());
            }
            customerServicePlanDTO.setPlanList(customerPlanDTOList);
            for(CustomerServiceMapping customerServiceMapping : customers.getCustomerServiceMappingList()) {

                CustomerServiceDTO customerServiceDTO = new CustomerServiceDTO();
                customerServiceDTO.setServiceId(customerServiceMapping.getServiceId());
                customerServiceDTOList.add(customerServiceDTO);
                customerServiceDTOList = customerServiceDTOList.stream().filter(distinctByKey(customerServiceDTO1 -> customerServiceDTO1.getServiceId())).collect(Collectors.toList());
            }
            customerServicePlanDTO.setServiceList(customerServiceDTOList);
            customerServicePlanDTO.setName(customers.getUsername());
            customerServicePlanDTO.setIsAvaileble(true);
            customerServicePlanDTO.setCustId(customers.getId());

            String  isshowall = clientServiceSrv.getByName("ServiceThroughEmail").getValue();
            if(isshowall.equalsIgnoreCase("1") || isshowall.equalsIgnoreCase("true")){
                customerServicePlanDTO.setIsShowAllService(true);
            }
            else{
                customerServicePlanDTO.setIsShowAllService(false);
            }
        }
        else{
            customerServicePlanDTO.setIsAvaileble(false);
        }
        return customerServicePlanDTO;
    }

    public List<Customers> getCustomerListFromEmailAndBuIdAndMvnoId(String email ,Long buId,Long mvnoId){
        List<Customers> customersList = new ArrayList<>();
        customersList = customersRepository.findAllByDomainandBuIdAndMvnoId(email , buId ,mvnoId.intValue());
        logger.info("customer list found with email and buID");
        if(customersList.isEmpty()){
            String domain = email.substring(email.indexOf("@") + 1 , email.length());
            if(!isCommonDomain(domain)){
                customersList = customersRepository.findAllByDomainandBuIdAndMvnoId(domain,buId,mvnoId.intValue());
                logger.info("customer list found with domain and buId");
            }
        }
        return customersList;
    }



}
