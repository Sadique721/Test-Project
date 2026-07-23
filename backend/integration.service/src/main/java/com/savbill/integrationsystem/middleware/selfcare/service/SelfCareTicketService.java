package com.savbill.integrationsystem.middleware.selfcare.service;

import com.savbill.integrationsystem.Case.Case;
import com.savbill.integrationsystem.Case.CaseRepo;
import com.savbill.integrationsystem.Case.TicketServicemapping;
import com.savbill.integrationsystem.billgen.entity.CustomerData;
import com.savbill.integrationsystem.billgen.repository.CustomersDataRepository;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.savbill.integrationsystem.deviceveri.domain.CustomerServiceMappingData;
import com.savbill.integrationsystem.deviceveri.domain.ServicesData;
import com.savbill.integrationsystem.deviceveri.repository.CustomerServiceMappingRepo;
import com.savbill.integrationsystem.deviceveri.repository.ServicesRepo;
import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
import com.savbill.integrationsystem.middleware.selfcare.model.*;
//import com.savbill.integrationsystem.rabbitmq.MessageSender;
import com.savbill.integrationsystem.middleware.selfcare.model.*;
import com.savbill.integrationsystem.rabbitmq.TicketMessageIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class SelfCareTicketService {

    @Autowired
    CaseRepo caseRepo;

//    @Autowired
//    private MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private CustomersDataRepository customersDataRepository;

    @Autowired
    private CustomerServiceMappingRepo customerServiceMappingRepo;

    @Autowired
    private ServicesRepo servicesRepo;

    OpenAndCloseCaseDetails caseDetails = new OpenAndCloseCaseDetails();

    public OpenAndCloseCaseDetails getOpenAndCloseCaseDetails(String username) {

        try {
            if (username != null) {
                List<Case> caseList = caseRepo.findAllByUserName(username);
                OpenCaseDetails openCaseDetails;
                CloseCaseDetails closeCaseDetails;
                List<OpenCaseDetails> openCaseDetailsList = new ArrayList<OpenCaseDetails>();
                List<CloseCaseDetails> closeCaseDetailsList = new ArrayList<CloseCaseDetails>();
                if (caseList != null) {
                    for (Case selfCareCase : caseList) {
                        if (selfCareCase != null && (selfCareCase.getCaseStatus().equals("Open") || selfCareCase.getCaseStatus().equals("In Progress")
                                || selfCareCase.getCaseStatus().equals("Pending")) || selfCareCase.getCaseStatus().equals("Resolved")
                                || selfCareCase.getCaseStatus().equals("Re-open") || selfCareCase.getCaseStatus().equals("Follow Up")
                                || selfCareCase.getCaseStatus().equals("Out of domain") || selfCareCase.getCaseStatus().equals("On Hold")) {
                            openCaseDetails = new OpenCaseDetails();
                            openCaseDetails.setTicketNo(selfCareCase.getCaseNumber());
                            openCaseDetails.setTitle(selfCareCase.getCaseTitle());
                            openCaseDetails.setStatus(selfCareCase.getCaseStatus());
                            openCaseDetails.setCreatedDate(selfCareCase.getCreateDate());
                            openCaseDetails.setLastModifiedDate(selfCareCase.getModifyDate());
                            openCaseDetails.setSubSubCategory(selfCareCase.getTicketReasonCategoryName());
                            openCaseDetailsList.add(openCaseDetails);
                        } else if (selfCareCase != null && (selfCareCase.getCaseStatus().equals("Raise and Close") || selfCareCase.getCaseStatus().equals("Closed"))) {
                            closeCaseDetails = new CloseCaseDetails();
                            closeCaseDetails.setTicketNo(selfCareCase.getCaseNumber());
                            closeCaseDetails.setTitle(selfCareCase.getCaseTitle());
                            closeCaseDetails.setStatus(selfCareCase.getCaseStatus());
                            closeCaseDetails.setCreatedDate(selfCareCase.getCreateDate());
                            closeCaseDetails.setLastModifiedDate(selfCareCase.getModifyDate());
                            closeCaseDetails.setSubSubCategory(selfCareCase.getTicketReasonCategoryName());
                            closeCaseDetailsList.add(closeCaseDetails);
                        }
                    }
                }
                caseDetails.setOpenCaseDetails(openCaseDetailsList);
                caseDetails.setCloseCaseDetails(closeCaseDetailsList);
            }
            return caseDetails;
        } catch (CustomValidationException ce) {
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, ce.getMessage(), null);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(APIConstants.SELFCARE + ex.getMessage(), ex);
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, ex.getMessage(), null);
        }
    }


    public TicketResponse saveSelfCareTicket(TicketRequest ticketRequest) {
        try {
            TicketResponse ticketResponse = new TicketResponse();
            TicketMessageIntegration createSelfCareTicketMessage = new TicketMessageIntegration();

            createSelfCareTicketMessage.setCustomerName(ticketRequest.getUserId());
            createSelfCareTicketMessage.setCaseTitle(ticketRequest.getTitle());
            createSelfCareTicketMessage = setSelfCareTicketCategoryType(ticketRequest, createSelfCareTicketMessage);
            createSelfCareTicketMessage = setSelfCareTicketSubCategory(ticketRequest, createSelfCareTicketMessage);
            createSelfCareTicketMessage.setCustomerAdditionalEmail(ticketRequest.getEmail());
            createSelfCareTicketMessage.setCustomerAdditionalMobileNumber(ticketRequest.getMobile());
            createSelfCareTicketMessage = setSelfCareTicketPriority(ticketRequest, createSelfCareTicketMessage);
            createSelfCareTicketMessage.setCaseStatus(ticketRequest.getStatus());
            createSelfCareTicketMessage.setFirstRemark(ticketRequest.getRemarks());
            createSelfCareTicketMessage.setSource("SelfCare");
            createSelfCareTicketMessage.setSubSource("SelfCare");
            createSelfCareTicketMessage = setSelfCareTicketCategory(ticketRequest, createSelfCareTicketMessage);
            createSelfCareTicketMessage.setCaseFor("Customer");
            createSelfCareTicketMessage.setCaseOrigin("SelfCare");
            createSelfCareTicketMessage = setCustomerID(ticketRequest, createSelfCareTicketMessage);
            createSelfCareTicketMessage.setCaseForPartner("Customer");
            createSelfCareTicketMessage.setUserName(ticketRequest.getUserId());
            createSelfCareTicketMessage = setFollowUpDateAndTime(createSelfCareTicketMessage);

            ticketResponse = setTicketResponse(ticketResponse, ticketRequest, createSelfCareTicketMessage);
//            messageSender.send(createSelfCareTicketMessage, RabbitMqConstants.QUEUE_INTEGRATION_CREATE_SELFCARE_TICKET);
            kafkaMessageSender.send(new KafkaMessageData(createSelfCareTicketMessage,createSelfCareTicketMessage.getClass().getSimpleName()));
            return  ticketResponse;

        } catch (CustomValidationException ce) {
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, ce.getMessage(), null);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(APIConstants.SELFCARE + ex.getMessage(), ex);
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, ex.getMessage(), null);
        }
    }

    private TicketMessageIntegration setFollowUpDateAndTime(TicketMessageIntegration createSelfCareTicketMessage) {
        String followUpDate = LocalDate.now().toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String followUpTime = LocalTime.now().format(formatter).toString();
        createSelfCareTicketMessage.setNextFollowupDate(followUpDate);
        createSelfCareTicketMessage.setNextFollowupTime(followUpTime);
        return createSelfCareTicketMessage;
    }

    private TicketMessageIntegration setCustomerID(TicketRequest ticketRequest, TicketMessageIntegration createSelfCareTicketMessage) {
        try {
            if (ticketRequest.getUserId() != null) {
                CustomerData customerData = customersDataRepository.findCustomerDataByUsername(ticketRequest.getUserId());
                if (customerData != null) {
                    createSelfCareTicketMessage.setCustomers(customerData.getId());
                }
            }
            return createSelfCareTicketMessage;
        } catch (CustomValidationException ce) {
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, ce.getMessage(), null);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(APIConstants.SELFCARE + ex.getMessage(), ex);
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, ex.getMessage(), null);
        }
    }

    private TicketMessageIntegration setSelfCareTicketCategory(TicketRequest ticketRequest, TicketMessageIntegration createSelfCareTicketMessage) {
        try {
            if (ticketRequest.getCategoryId() != null && ticketRequest.getCategoryId() == 10113) {
                createSelfCareTicketMessage.setDepartment("Technical");
            } else {
                createSelfCareTicketMessage.setDepartment("Sales");
            }
            return createSelfCareTicketMessage;
        } catch (CustomValidationException ce) {
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, ce.getMessage(), null);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(APIConstants.SELFCARE + ex.getMessage(), ex);
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, ex.getMessage(), null);
        }
    }

    private TicketMessageIntegration setSelfCareTicketPriority(TicketRequest ticketRequest, TicketMessageIntegration createSelfCareTicketMessage) {
        try {
            if (ticketRequest.getPriority() != null && ticketRequest.getPriority() == 1) {
                createSelfCareTicketMessage.setPriority("Low");
                return createSelfCareTicketMessage;
            } else {
                createSelfCareTicketMessage.setPriority("Low");
                return createSelfCareTicketMessage;
            }
        } catch (CustomValidationException ce) {
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, ce.getMessage(), null);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(APIConstants.SELFCARE + ex.getMessage(), ex);
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, ex.getMessage(), null);
        }
    }

    private TicketMessageIntegration setSelfCareTicketSubCategory(TicketRequest ticketRequest, TicketMessageIntegration createSelfCareTicketMessage) {
        try {
            CustomerData customerData = customersDataRepository.findCustomerDataByUsername(ticketRequest.getUserId());
            Long custID;
            Long caseServiceId;
            List<TicketServicemapping> ticketServicemappingList = new ArrayList<>();
            TicketServicemapping ticketServicemapping = new TicketServicemapping();
            if (customerData != null) {
                custID = Long.valueOf(customerData.getId());
                List<CustomerServiceMappingData> customerServiceMappingList = customerServiceMappingRepo.findByCustidAndIsDelete(custID, 0);
                for (CustomerServiceMappingData customerServiceMapping : customerServiceMappingList) {
                    Long customerServiceId = customerServiceMapping.getServiceid();
                    List<ServicesData> customerServiceList = servicesRepo.findByServiceid(customerServiceId);
                    for(ServicesData customerService : customerServiceList){
                        //Setting service Id if the SubCategoryID is 'FTTH/X' in ticket request
                        if (ticketRequest.getSubCategoryId() != null && ticketRequest.getSubCategoryId() == 10115 && customerService.getIsDtv() == false) {
                            caseServiceId = customerService.getServiceid();
                            ticketServicemapping.setServiceid(caseServiceId);
                            ticketServicemappingList.add(ticketServicemapping);
                        }
                        //Setting service Id if the SubCategoryID is 'Clear TV' in ticket request
                        else if (ticketRequest.getSubCategoryId() != null && ticketRequest.getSubCategoryId() == 10116 && customerService.getIsDtv() == true) {
                            caseServiceId = customerService.getServiceid();
                            ticketServicemapping.setServiceid(caseServiceId);
                            ticketServicemappingList.add(ticketServicemapping);
                        }
                    }
                }
            }
            createSelfCareTicketMessage.setTicketServicemappingList(ticketServicemappingList);
            return createSelfCareTicketMessage;

        } catch (CustomValidationException ce) {
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, ce.getMessage(), null);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(APIConstants.SELFCARE + ex.getMessage(), ex);
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, ex.getMessage(), null);
        }
    }

    private TicketMessageIntegration setSelfCareTicketCategoryType(TicketRequest ticketRequest, TicketMessageIntegration createSelfCareTicketMessage) {
        //Case Type = Issue
        if (ticketRequest.getCategoryType() == 88) {
            createSelfCareTicketMessage.setCaseType("Issue");
        }
        //Case Type = Request
        else if (ticketRequest.getCategoryType() == 89) {
            createSelfCareTicketMessage.setCaseType("Request");
        }
        //Case Type = Inquiry
        else if (ticketRequest.getCategoryType() == 90) {
            createSelfCareTicketMessage.setCaseType("Inquiry");
        }
        //Case Type = Default CaseType coming from Selfcare
        else if (ticketRequest.getCategoryType() == 1) {
            createSelfCareTicketMessage.setCaseType("Issue");
        }
        return createSelfCareTicketMessage;
    }

    private TicketResponse setTicketResponse(TicketResponse ticketResponse, TicketRequest ticketRequest, TicketMessageIntegration createSelfCareTicketMessage){
        try{
            ticketResponse.setUserId(ticketRequest.getUserId());
            ticketResponse.setTitle(ticketRequest.getTitle());
            ticketResponse.setCategoryType(ticketRequest.getCategoryType());
            ticketResponse.setSubCategoryId(ticketRequest.getSubCategoryId());
            ticketResponse.setEmail(ticketRequest.getEmail());
            ticketResponse.setMobile(ticketRequest.getMobile());
            ticketResponse.setPriority(ticketRequest.getPriority());
            ticketResponse.setStatus(ticketRequest.getStatus());
            ticketResponse.setCombassign(ticketRequest.getCombassign());
            ticketResponse.setRemarks(ticketRequest.getRemarks());
            ticketResponse.setSolution(ticketRequest.getSolution());
            ticketResponse.setCategoryId(ticketRequest.getCategoryId());
            ticketResponse.setOpenAndCloseCaseDetails(null);
            ticketResponse.setOpenCaseDetails(null);
            ticketResponse.setCloseCaseDetails(null);
            ticketResponse.setCustomerDetailsModel(null);
            ticketResponse.setTypeList(null);
            ticketResponse.setSubCategoryList(null);
            ticketResponse.setCustId(createSelfCareTicketMessage.getCustomers());
            ticketResponse.setIpaddress(null);
            ticketResponse.setPartnerId(null);
            ticketResponse.setResellerId(null);
            ticketResponse.setOtz(null);
            ticketResponse.setPartnerName(null);
            ticketResponse.setPartnerCode(null);
            ticketResponse.setResponseMesg("Your Data has been successfully saved.");
            ticketResponse.setResponseCode(0);
            ticketResponse.setSubSubCategoryList(null);
        } catch (CustomValidationException ce) {
            ticketResponse.setResponseCode(400);
            ticketResponse.setResponseMesg("Error while saving data.");
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, ce.getMessage(), null);
        } catch (Exception ex) {
            ticketResponse.setResponseCode(400);
            ticketResponse.setResponseMesg("Error while saving data.");
            ApplicationLogger.logger.error(APIConstants.SELFCARE + ex.getMessage(), ex);
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, ex.getMessage(), null);
        }
        return ticketResponse;
    }
}
