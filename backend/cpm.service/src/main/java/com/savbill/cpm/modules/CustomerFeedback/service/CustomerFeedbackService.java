package com.savbill.cpm.modules.CustomerFeedback.service;

import com.savbill.cpm.exception.CustomValidationException;
import com.savbill.cpm.model.common.ClientService;
import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.modules.CustomerFeedback.DTO.FeedbackDetailDTO;
import com.savbill.cpm.modules.CustomerFeedback.DTO.FeedbackSummaryDTO;
import com.savbill.cpm.modules.CustomerFeedback.domain.CustomerFeedback;
import com.savbill.cpm.modules.CustomerFeedback.model.CustomerFeedbackDTO;
import com.savbill.cpm.modules.CustomerFeedback.repository.CustomerFeedbackRepository;
import com.savbill.cpm.modules.subscriber.service.SubscriberService;
import com.savbill.cpm.repository.common.ClientServiceRepository;
import com.savbill.cpm.repository.radius.CustomersRepository;
import com.savbill.cpm.utils.APIConstants;
import com.savbill.cpm.utils.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerFeedbackService {

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private CustomerFeedbackRepository customerFeedbackRepository;

    @Autowired
    ClientServiceRepository clientServiceRepository;


    /**@Author Dhaval Khalasi
     * Validate request method for  create
     * **/

    public void validateSaveRequest(List<CustomerFeedbackDTO> feedbackDTOList) {
        for (CustomerFeedbackDTO dto : feedbackDTOList) {
            if (dto.getRating() == null || dto.getRating().trim().isEmpty()) {
                throw new CustomValidationException(APIConstants.FAIL, "Rating can't be empty.", null);
            }

            if (dto.getCustId() == null) {
                throw new CustomValidationException(APIConstants.FAIL, "Customer Id is null.", null);
            }

            Optional<Customers> customerOpt = customersRepository.findById(dto.getCustId().intValue());
            if (!customerOpt.isPresent()) {
                throw new CustomValidationException(
                        APIConstants.FAIL,
                        "Customer with ID: " + dto.getCustId() + " not found.",
                        null
                );
            }
        }
    }

    /**
   * @Author Dhaval Khalasi
   * validate update customer feedback request
   * **/
    public void validateUpdateRequest(CustomerFeedbackDTO customerFeedbackDTO){
        if(customerFeedbackDTO.getRating().equalsIgnoreCase("") || customerFeedbackDTO.getRating().isEmpty() || customerFeedbackDTO.getRating() == null){
            throw new CustomValidationException(APIConstants.FAIL , "Rating can't be empty." , null);
        }
    }

    /**@Author Dhaval Khalasi
     * Save Customer Feedback Method
     * **/

    public List<CustomerFeedbackDTO> saveCustomerFeedback(List<CustomerFeedbackDTO> feedbackDTOList) {
        Integer mvnoId = subscriberService.getMvnoIdFromCurrentStaff();
        List<Long> buIds = subscriberService.getBUIdsFromCurrentStaff();
        Long buId = (buIds != null && !buIds.isEmpty()) ? buIds.get(0) : null;

        List<CustomerFeedback> feedbackEntities = new ArrayList<>();

        for (CustomerFeedbackDTO dto : feedbackDTOList) {
            CustomerFeedback feedback = new CustomerFeedback();
            feedback.setRating(dto.getRating());

            if (dto.getFeedback() != null && !dto.getFeedback().isEmpty()) {
                feedback.setFeedback(dto.getFeedback());
            }

            feedback.setMvnoId(mvnoId);
            feedback.setBuId(buId);
            feedback.setEvent(dto.getEvent());
            feedback.setIsDelete(false);
            feedback.setCreateDate(LocalDateTime.now());
            feedback.setCustId(dto.getCustId());

            feedbackEntities.add(feedback);
        }

        List<CustomerFeedback> savedEntities = customerFeedbackRepository.saveAll(feedbackEntities);
        return savedEntities.stream()
                .map(this::DomainToDTO)
                .collect(Collectors.toList());
    }


    /**@Author Dhaval Khalasi
     * Update Customer Method
     * **/
    public CustomerFeedbackDTO updateCustomerFeedBack(CustomerFeedbackDTO customerFeedbackDTO){
        Optional<CustomerFeedback> customerFeedback = customerFeedbackRepository.findById(customerFeedbackDTO.getId());
        if(customerFeedback.isPresent()) {
            CustomerFeedback updatedCustomerFeedback = customerFeedback.get();
            updatedCustomerFeedback.setRating(customerFeedbackDTO.getRating());
            if (customerFeedbackDTO.getFeedback() != null && !customerFeedbackDTO.getFeedback().equalsIgnoreCase("")) {
                updatedCustomerFeedback.setFeedback(customerFeedbackDTO.getFeedback());
            }
            Integer mvnoId = subscriberService.getMvnoIdFromCurrentStaff();
            updatedCustomerFeedback.setMvnoId(mvnoId);
            List<Long> buId = subscriberService.getBUIdsFromCurrentStaff();
            if (buId != null && !buId.isEmpty()) {
                updatedCustomerFeedback.setBuId(buId.get(0));
            }
            updatedCustomerFeedback.setIsDelete(false);
            CustomerFeedback customerFeedback1 = customerFeedbackRepository.save(updatedCustomerFeedback);
            return DomainToDTO(customerFeedback1);  /**Here it is converted to dto by below method**/
        }
        else{
            throw new CustomValidationException(APIConstants.FAIL , "Customer Feedback is not found given feedback Id",null);
        }

    }

    /**@Author Dhaval Khalasi
     * Delete Customer Feedback Method
     * **/
    public void deleteCustomerFeedBack(Long customerId){
        Optional<CustomerFeedback> customerFeedback = customerFeedbackRepository.findById(customerId);
        if(customerFeedback.isPresent()) {
            customerFeedback.get().setIsDelete(true);
            customerFeedbackRepository.save(customerFeedback.get());
             /**Here it is converted to dto by below method**/
        }
        else{
            throw new CustomValidationException(APIConstants.FAIL , "Customer Feedback is not found given feedback Id",null);
        }

    }
    /**@Author Dhaval Khalasi
     * Find Customer Feedback Method By Customer Id
     * **/
    public List<CustomerFeedbackDTO> findCustomerFeedBackByCustomerId(Long Id){
        List<CustomerFeedback> customerFeedbackList =  customerFeedbackRepository.findByCustIdAndIsDeleteFalse(Id);
        if(customerFeedbackList != null){
            return customerFeedbackList.stream().map(customerFeedback -> DomainToDTO(customerFeedback)).collect(Collectors.toList());
        }
        else{
            return null;
        }

    }

    /**@Author Dhaval Khalasi
     * Method for convert entity to DTO for response purposes
     * **/

    public CustomerFeedbackDTO DomainToDTO(CustomerFeedback customerFeedback){
        CustomerFeedbackDTO customerFeedbackDTO =  new CustomerFeedbackDTO();
        customerFeedbackDTO.setId(customerFeedback.getId());
        customerFeedbackDTO.setRating(customerFeedback.getRating());
        if(customerFeedback.getFeedback() != null && !customerFeedback.getFeedback().equalsIgnoreCase("")){
            customerFeedbackDTO.setFeedback(customerFeedback.getFeedback());
        }
        customerFeedbackDTO.setMvnoId(customerFeedback.getMvnoId());
        if(customerFeedback.getBuId() != null){
            customerFeedbackDTO.setBuId(customerFeedbackDTO.getBuId());
        }
        if(customerFeedback.getEvent() != null){
            customerFeedbackDTO.setEvent(customerFeedback.getEvent());
        }
        customerFeedbackDTO.setCustId(customerFeedback.getCustId());
        customerFeedbackDTO.setIsDelete(customerFeedback.getIsDelete());
        return customerFeedbackDTO;
    }


    /**@Author Akshay Mewada
     * Check frequency feedback Customer Feedback Method
     * **/
    public boolean checkCustomerfeedbackFrequecny(Integer customerId){
        Optional<Customers> customers=customersRepository.findCustomerById(customerId);
        if(!customers.isPresent() || customers.get().getStatus().equalsIgnoreCase("NewActivation")){
        return false;
        }
        List<CustomerFeedback> customerFeedbackList = customerFeedbackRepository.findCustomerFeedbackByCustIdOrderByCreateDateDesc(customerId.longValue());
        if(customerFeedbackList!=null && !customerFeedbackList.isEmpty()) {
            LocalDate lastCustomerfeedBackDate = LocalDate.from(customerFeedbackList.get(0).getCreateDate());
            ClientService clientService = clientServiceRepository.getByNameAndMvnoId(CommonConstants.FEEDBACKFORM_FREQUENCY, subscriberService.getMvnoIdFromCurrentStaff());
            if(Objects.isNull(clientService)){
                throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR,"System configuration not found for name FEEDBACKFORM_FREQUENCY and current mvno",null);
            }
            if (clientService.getValue().equalsIgnoreCase("DAILY")) {
                return !LocalDate.now().isBefore(lastCustomerfeedBackDate.plusDays(1));
            } else if (clientService.getValue().equalsIgnoreCase("MONTHLY")) {
                return !LocalDate.now().isBefore(lastCustomerfeedBackDate.plusMonths(1));
            } else if (clientService.getValue().equalsIgnoreCase("WEEKLY")) {
                return !LocalDate.now().isBefore(lastCustomerfeedBackDate.plusWeeks(1));
            }
        }
        return true;
    }



    /**@Author Akshay Mewada
     * Check feedback Customer details Method
     * **/
    public List<FeedbackSummaryDTO> getAvragefeedbackBasedOnEvent(Integer customerId){
        List<CustomerFeedback> customerFeedbackList = customerFeedbackRepository.findCustomerFeedbackByCustIdOrderByCreateDateDesc(customerId.longValue());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");

        return customerFeedbackList.stream()
                .filter(fb -> fb.getRating() != null && fb.getEvent() != null && !fb.getRating().isEmpty() && !Boolean.TRUE.equals(fb.getIsDelete()))
                .collect(Collectors.groupingBy(CustomerFeedback::getEvent))
                .entrySet().stream()
                .map(entry -> {
                    String event = entry.getKey();
                    List<CustomerFeedback> feedbacks = entry.getValue();

                    List<FeedbackDetailDTO> detailDTOs = feedbacks.stream()
                            .map(fb -> new FeedbackDetailDTO(
                                    parseRating(fb.getRating()),
                                    fb.getCreateDate().format(formatter),fb.getFeedback()))
                            .collect(Collectors.toList());

                    double avg = detailDTOs.stream()
                            .mapToDouble(FeedbackDetailDTO::getRating)
                            .average()
                            .orElse(0.0);

                    return new FeedbackSummaryDTO(event, avg, detailDTOs);
                })
                .collect(Collectors.toList());
       }


    private double parseRating(String ratingStr) {
        try {
            return Double.parseDouble(ratingStr);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }


}
