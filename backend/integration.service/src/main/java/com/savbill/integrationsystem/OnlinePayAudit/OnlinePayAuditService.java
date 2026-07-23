package com.savbill.integrationsystem.OnlinePayAudit;

import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Model.QCustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Repository.CustomerPaymentRepository;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.dto.GenericSearchModel;
import com.savbill.integrationsystem.core.security.dto.LoggedInUser;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static com.savbill.integrationsystem.core.security.AuditableListener.MODULE;
import static com.savbill.integrationsystem.core.utillity.log.ApplicationLogger.logger;

@Service
public class OnlinePayAuditService {

    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;


    private final Logger log = Logger.getLogger(OnlinePayAuditService.class);

    public PageRequest pageRequest = null;

    public Map<String, String> sortColMap = new HashMap<>();

    public Integer MAX_PAGE_SIZE;

    @PersistenceContext
    EntityManager entityManager;


    public List<java.lang.Long> getBUIdsFromCurrentStaff() {
        List<java.lang.Long> mvnoIds = new ArrayList<java.lang.Long>();
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
            }
        } catch (Exception e) {
            logger.error("MVNO - getBUIdsFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoIds;
    }

    public Page<CustomerPayment> getOnlinePayAuditList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder,
                                                       List<GenericSearchModel> filterList){
        PageRequest pageRequest = PageRequest.of(pageNumber-1, customPageSize, Sort.by(Sort.Direction.DESC, sortBy));

        if(getMvnoIdFromCurrentStaff()==1) {
            return customerPaymentRepository.findAll(pageRequest);
        }
        if (null == filterList || 0 == filterList.size()){
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                return customerPaymentRepository.findAll(pageRequest, Arrays.asList(1, getMvnoIdFromCurrentStaff()));
        }
        else {
            for (GenericSearchModel searchModel : filterList) {
                boolean hasDateFilter = searchModel.getFromDate() != null && !searchModel.getFromDate().isEmpty()
                        && searchModel.getToDate() != null && !searchModel.getToDate().isEmpty();
                boolean hasValueFilter = searchModel.getFilterValue() != null && !searchModel.getFilterValue().isEmpty();

                if (hasDateFilter && hasValueFilter) {
                    throw new IllegalArgumentException("Invalid search: Please use either a search value or a date range, not both.");
                }

                if (hasDateFilter) {
                    // Ensure transactionDate filtering is applied correctly
                    if (searchModel.getFilterDataType() == null || !searchModel.getFilterDataType().equals("transactionDate")) {
                        throw new IllegalArgumentException("Date range can only be used with transaction Date");
                    }
                    return search(filterList, pageNumber - 1, customPageSize, sortBy, sortOrder);
                }
                if (null == searchModel.getFilterColumn() || searchModel.getFilterValue().isEmpty()) {
                    if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                        return customerPaymentRepository.findAll(pageRequest, Arrays.asList(1, getMvnoIdFromCurrentStaff()));
                    else
                        return customerPaymentRepository.findAll(pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
                } else {
                    return search(filterList, pageNumber-1, customPageSize, sortBy, sortOrder);
                }
            }
        }
        return null;
    }

    public List<CustomerPayment> getOnlinePayAuditListByCustId( Integer custId){
        try{
            List<CustomerPayment> onlinePayAudits = customerPaymentRepository.findCustomerPaymentByCustIdOrderByPaymentDateDesc(custId);
            log.info("List of payment for the customer fetch successfully");
            return onlinePayAudits;

        }catch (Exception e){
            log.error("List of payment for the customer fetch failed : "+e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<CustomerPayment> getOnlinePayAuditListByPartner( Integer partnerId){
        try{
            List<CustomerPayment> onlinePayAudits = customerPaymentRepository.findCustomerPaymentByPartnerId(partnerId);
            log.info("List of payment for the partner fetch successfully");
            return onlinePayAudits;

        }catch (Exception e){
            log.error("List of payment for the partner fetch failed : "+e.getMessage());
        }
        return new ArrayList<>();
    }


    public Page<CustomerPayment> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy,
                                        Integer sortOrder) {
        //String SUBMODULE = MODULE + " [search()] ";
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, sortBy));

        try {
            for (GenericSearchModel searchModel : filterList) {
                if (null != searchModel.getFilterColumn()) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase("any")) {
                        if(searchModel.getFilterDataType().equals("customerUsername")){
                            return getAuditByCustomerName(searchModel.getFilterValue(), searchModel.getFilterDataType(),
                                    searchModel.getFilterDataType(), pageRequest);
                        }else if(searchModel.getFilterDataType().equals("status")){
                            return getAuditBySatus(searchModel.getFilterValue(), searchModel.getFilterDataType(),
                                    searchModel.getFilterDataType(), pageRequest);
                        }else if(searchModel.getFilterDataType().equals("orderid")){
                            return getAuditByOrderid(searchModel.getFilterValue(), searchModel.getFilterDataType(),
                                    searchModel.getFilterDataType(), pageRequest);
                        }else if(searchModel.getFilterDataType().equals("merchantName")){
                            return getAuditByMerchant(searchModel.getFilterValue(), searchModel.getFilterDataType(),
                                    searchModel.getFilterDataType(), pageRequest);
                        }else if(searchModel.getFilterDataType().equals("pgTransactionId")){
                            return getAuditByPgTransactionId(searchModel.getFilterValue(), searchModel.getFilterDataType(),
                                    searchModel.getFilterDataType(), pageRequest);
                        }
                        else if(searchModel.getFilterDataType().equals("accountNumber")){
                            return getAuditByAccountNumber(searchModel.getFilterValue(), searchModel.getFilterDataType(),
                                    searchModel.getFilterDataType(), pageRequest);
                        }
                        else if(searchModel.getFilterDataType().equals("payerMobileNumber")){
                            return getAuditByPayerMobileNumber(searchModel.getFilterValue(), searchModel.getFilterDataType(),
                                    searchModel.getFilterDataType(), pageRequest);
                        }
                        else if (searchModel.getFilterDataType().equals("transactionDate")) {
                             if (searchModel.getFilterValue()!= null && !searchModel.getFilterValue().isEmpty()){
                                 return getAuditByTransaction(searchModel.getFilterValue(), pageRequest);
                            } else {
                                 return getAuditByTransactionDate(searchModel.getFromDate(), searchModel.getToDate(), pageRequest);
                            }
                        }
                        else{
                            return customerPaymentRepository.findAll(pageRequest);
                        }
                    }
                } else
                    throw new RuntimeException("Please Provide Search Column!");
            }
        } catch (Exception ex) {
            logger.error("Online Payment Audit" + ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }

    public Page<CustomerPayment> getAuditByTransaction(String transactionDateStr, Pageable pageable) {
        // Convert String to LocalDate
        LocalDate transactionDate = LocalDate.parse(transactionDateStr);

        // Call Repository Method
        return customerPaymentRepository.findByTransactionDate(String.valueOf(transactionDate), pageable);
    }
    public Page<CustomerPayment> getAuditByTransactionDate(String fromDate, String toDate, PageRequest pageRequest) {
        if (fromDate == null || fromDate.isEmpty() || toDate == null || toDate.isEmpty()) {
            throw new IllegalArgumentException("From Date and To Date cannot be null or empty.");
        }

        try {
            logger.info("Parsing dates: fromDate={}, toDate={}", fromDate, toDate);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS]");
            LocalDateTime fromDateTime = LocalDateTime.parse(fromDate.trim() + "T00:00:00", formatter);
            LocalDateTime toDateTime = LocalDateTime.parse(toDate.trim() + "T00:00:00", formatter)
                    .plusDays(1)
                    .minusNanos(1);

            if (fromDateTime.isAfter(toDateTime)) {
                throw new IllegalArgumentException("From date must be before To date.");
            }

            return customerPaymentRepository.findByTransactionDateBetween(fromDateTime, toDateTime, pageRequest);
        } catch (DateTimeParseException e) {
            logger.error("Date parsing error: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid date format. Please use 'yyyy-MM-dd'T'HH:mm:ss' format.", e);
        } catch (IllegalArgumentException e) {
            logger.error("Date parsing error: {}", e.getMessage());
            throw new IllegalArgumentException("From date must be before To date", e);
         } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            throw new RuntimeException("An unexpected error occurred while processing your request.", e);
        }
    }


    public Page<CustomerPayment> getAuditByCustomerName(String s1, String s2, String dataType, PageRequest pageRequest) {

        if (getMvnoIdFromCurrentStaff() == 1)
            return customerPaymentRepository.findAllByCustomerUsername(s1 != null ? s1 : "", pageRequest);
        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
            return customerPaymentRepository.findAllByCustomerUsernameAndMvnoidIn(s1 != null ? s1 : "", pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        else
            return customerPaymentRepository.findAllByCustomerUsernameAndMvnoidInAndBuidIn(s1 != null ? s1 : "", pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
    }

    public Page<CustomerPayment> getAuditBySatus(String s1, String s2, String dataType, PageRequest pageRequest) {

        if (getMvnoIdFromCurrentStaff() == 1)
            return customerPaymentRepository.findAllByStatus(s1 != null ? s1 : "", pageRequest);
        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
            return customerPaymentRepository.findAllByStatusAndMvnoidIn(s1 != null ? s1 : "", pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        else
            return customerPaymentRepository.findAllByStatusAndMvnoidInAndBuidIn(s1 != null ? s1 : "", pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
    }


    public Page<CustomerPayment> getAuditByOrderid(String s1, String s2, String dataType, PageRequest pageRequest) {

        if (getMvnoIdFromCurrentStaff() == 1)
            return customerPaymentRepository.findAllByOrderid(s1 != null ? s1 : "", pageRequest);
        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
            return customerPaymentRepository.findAllByOrderidAndMvnoidIn(s1 != null ? s1 : "", pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        else
            return customerPaymentRepository.findAllByOrderidAndMvnoidInAndBuidIn(s1 != null ? s1 : "", pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
    }


    public Page<CustomerPayment> getAuditByMerchant(String s1, String s2, String dataType, PageRequest pageRequest) {

        if (getMvnoIdFromCurrentStaff() == 1)
            return customerPaymentRepository.findAllByMerchantName(s1 != null ? s1 : "", pageRequest);
        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
            return customerPaymentRepository.findAllByMerchantNameAndMvnoidIn(s1 != null ? s1 : "", pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        else
            return customerPaymentRepository.findAllByMerchantNameAndMvnoidInAndBuidIn(s1 != null ? s1 : "", pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
    }


    public Page<CustomerPayment> getAuditByPgTransactionId(String s1, String s2, String dataType, PageRequest pageRequest) {

        if (getMvnoIdFromCurrentStaff() == 1)
            return customerPaymentRepository.findAllByPgTransactionIdWithSearch(s1 != null ? s1 : "", pageRequest);
        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
            return customerPaymentRepository.findAllByPgTransactionIdWithSearchAndMvnoidIn(s1 != null ? s1 : "", pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        else
            return customerPaymentRepository.findAllByPgTransactionIdWithSearchAndMvnoidInAndBuidIn(s1 != null ? s1 : "", pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
    }

    public Page<CustomerPayment> getAuditByAccountNumber(String s1, String s2, String dataType, PageRequest pageRequest) {

        if (getMvnoIdFromCurrentStaff() == 1)
            return customerPaymentRepository.findAllByAccountNumberWithSearch(s1 != null ? s1 : "", pageRequest);
        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
            return customerPaymentRepository.findAllByAccountNumberWithSearchAndMvnoidIn(s1 != null ? s1 : "", pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        else
            return customerPaymentRepository.findAllByAccountNumberWithSearchAndMvnoidInAndBuidIn(s1 != null ? s1 : "", pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
    }

    public Page<CustomerPayment> getAuditByPayerMobileNumber(String s1, String s2, String dataType, PageRequest pageRequest) {

        if (getMvnoIdFromCurrentStaff() == 1)
            return customerPaymentRepository.findAllByPayerMobileNumberWithSearch(s1 != null ? s1 : "", pageRequest);
        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
            return customerPaymentRepository.findAllByPayerMobileNumberWithSearchAndMvnoidIn(s1 != null ? s1 : "", pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        else
            return customerPaymentRepository.findAllByPayerMobileNumberWithSearchAndMvnoidInAndBuidIn(s1 != null ? s1 : "", pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
    }

    public List<Map<String, String>> getTransactionsForExport(List<GenericSearchModel> filterList) {
        QCustomerPayment qPayment = QCustomerPayment.customerPayment;

        BooleanExpression finalExpression = qPayment.isNotNull();

        for (GenericSearchModel searchModel : filterList) {
            String column = searchModel.getFilterDataType();
            String value = searchModel.getFilterValue();

            if (searchModel.getFromDate() == null && searchModel.getToDate() == null) {
                if (value == null || value.isEmpty()) continue;
            }
            switch (column) {
                case "customerUsername":
                    finalExpression = finalExpression.and(qPayment.customerUsername.equalsIgnoreCase(value));
                    break;
                case "status":
                    finalExpression = finalExpression.and(qPayment.status.eq(value));
                    break;
                case "orderid":
                    finalExpression = finalExpression.and(qPayment.orderId.eq(Long.valueOf(value)));
                    break;
                case "merchantName":
                    finalExpression = finalExpression.and(qPayment.merchantName.containsIgnoreCase(value.trim()));
                    break;
                case "pgTransactionId":
                    finalExpression = finalExpression.and(qPayment.pgTransactionId.equalsIgnoreCase(value));
                    break;
                case "accountNumber":
                    finalExpression = finalExpression.and(qPayment.accountNumber.equalsIgnoreCase(value));
                    break;
                case "transactionDate":
                    if (searchModel.getFromDate() != null || searchModel.getToDate() != null) {
                        LocalDate fromDate = LocalDate.parse(searchModel.getFromDate());
                        LocalDate toDate = LocalDate.parse(searchModel.getToDate());

                        LocalDateTime fromDateTime = fromDate != null
                                ? LocalDateTime.of(fromDate, LocalTime.MIN)  // 00:00:00
                                : LocalDateTime.MIN;

                        LocalDateTime toDateTime = toDate != null
                                ? LocalDateTime.of(toDate, LocalTime.MAX)  // 23:59:59.999999999
                                : LocalDateTime.MAX;

                        finalExpression = finalExpression.and(qPayment.transactionDate.between(fromDateTime, toDateTime));
                    } else if (searchModel.getFilterValue() != null || !searchModel.getFilterValue().isEmpty()) {
                        LocalDate searchDate = LocalDate.parse(searchModel.getFilterValue());

                        LocalDateTime searchStartDateTime = searchDate != null
                                ? LocalDateTime.of(searchDate, LocalTime.MIN)
                                : LocalDateTime.MIN;

                        LocalDateTime searchEndDateTime = searchDate != null
                                ? LocalDateTime.of(searchDate, LocalTime.MAX)  // 23:59:59.999999999
                                : LocalDateTime.MAX;

                        finalExpression = finalExpression.and(qPayment.transactionDate.between(searchStartDateTime, searchEndDateTime));
                    }
                    break;
            }
        }

        JPAQuery<CustomerPayment> query = new JPAQuery<>(entityManager);
        List<Map<String, String>> dataToExport = new ArrayList<>();

        List<Tuple> queryResult = query.select(
                        qPayment.orderId,
                        qPayment.custId,
                        qPayment.payment,
                        qPayment.status,
                        qPayment.pgTransactionId,
                        qPayment.paymentDate,
                        qPayment.merchantName,
                        qPayment.transactionDate,
                        qPayment.customerUsername,
                        qPayment.accountNumber,
                        qPayment.gatewayStatus,
                        qPayment.failureDescription,
                        qPayment.payerMobileNumber,
                        qPayment.autoPaymentInitiator
                )
                .from(qPayment)
                .where(finalExpression)
                .fetch();

        if (!queryResult.isEmpty()) {
            queryResult.forEach(result -> {
                Map<String, String> map = new HashMap<>();
                map.put("Reference No", getStringValue(result.get(qPayment.orderId)));
                map.put("Transaction No", getStringValue(result.get(qPayment.pgTransactionId)));
                map.put("Account Number", getStringValue(result.get(qPayment.accountNumber)));
                map.put("Customer Username", getStringValue(result.get(qPayment.customerUsername)));
                map.put("Payment Amount", getStringValue(result.get(qPayment.payment)));
                map.put("Status", getStringValue(result.get(qPayment.status)));
                map.put("Gateway Status", getStringValue(result.get(qPayment.gatewayStatus)));
                map.put("Failure Reason", getStringValue(result.get(qPayment.failureDescription)));
                map.put("Payment Date", getStringValue(result.get(qPayment.paymentDate)));
                map.put("Merchant Name", getStringValue(result.get(qPayment.merchantName)));
                map.put("Transaction Date", getStringValue(result.get(qPayment.transactionDate)));
                map.put("Payer Mobile Number", getStringValue(result.get(qPayment.payerMobileNumber)));
                map.put("Auto Payment Initiator", getStringValue(result.get(qPayment.autoPaymentInitiator)));

                dataToExport.add(map);
            });
        } else {
            throw new RuntimeException("No data found.");
        }
        return dataToExport;
    }

    /**
     * Helper method to convert null values to empty strings and format non-string values properly.
     */
    private String getStringValue(Object value) {
        return value != null ? value.toString() : "";
    }
    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            logger.error(MODULE + e.getStackTrace(), e);
        }
        return loggedInUser;
    }

    public Integer getMvnoIdFromCurrentStaff() {
        //TODO: Change once API work on live BSS server
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                if(securityContext.getAuthentication().getPrincipal() != null)
                    mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }

    public ResponseEntity<Map<String, Object>> apiResponse(Integer responseCode, Map<String, Object> response) {
        try {
//			log.info(String.format("%s", new ObjectMapper().writeValueAsString(response)));
            response.put("timestamp",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
            response.put("status", responseCode);
            if(response.get(APIConstants.ERROR_MESSAGE) != null)
            {
                String errorMsg = response.get(APIConstants.ERROR_MESSAGE).toString().replace(APIConstants.NOT_FOUND.toString(), "");
                response.put(APIConstants.ERROR_MESSAGE, errorMsg);
            }
            if (responseCode.equals(APIConstants.SUCCESS)) {
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
            } else if(responseCode.equals(APIConstants.NOT_FOUND)){
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            } else if (responseCode.equals(APIConstants.FAIL)) {
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            } else if (responseCode.equals(APIConstants.INTERNAL_SERVER_ERROR)) {
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }else if (responseCode.equals(APIConstants.NO_CONTENT_FOUND)) {
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
            }else if (responseCode.equals(472)) {
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            log.error("Error while performing operation", e);
            if (response == null) {
                response = new HashMap<>();
            }
            response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public GenericDataDTO fetchPaymentReceipt(String pgTransactionId) {
        GenericDataDTO response = new GenericDataDTO();

        try {
            Optional<CustomerPayment> optionalPayment = customerPaymentRepository.findByPgTransaction(pgTransactionId);

            if (optionalPayment.isPresent()) {
                response.setResponseCode(200);
                response.setResponseMessage("Fetched successfully");
                response.setData(optionalPayment.get());
            } else {
                response.setResponseCode(404);
                response.setResponseMessage("Transaction id not found");
            }

        } catch (Exception e) {
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Error: " + e.getMessage());
        }

        return response;
    }

}