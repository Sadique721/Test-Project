package com.savbill.radius.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.aaa.constant.MenuConstants;
import com.savbill.radius.aaa.data.CustomerCreateData;
import com.savbill.radius.aaa.data.CustomerData;
import com.savbill.radius.aaa.db.DBAuthenticationDriver;
import com.savbill.radius.dto.LogoutCustomerDTO;
import com.savbill.radius.dto.PageableResponse;
import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.*;
import com.savbill.radius.helper.*;
import com.savbill.radius.entity.*;
import com.savbill.radius.helper.*;
import com.savbill.radius.repository.CustPlanMappingRepository;
import com.savbill.radius.repository.CustomersRepository;
import com.savbill.radius.repository.LiveUserRepository;
import com.savbill.radius.repository.MacAddressMappingRepository;
import com.savbill.radius.services.CustomerService;
import com.savbill.radius.services.DeviceService;
import com.savbill.radius.services.LiveUserService;
import com.savbill.radius.services.impl.CustomersServiceImpl;
import com.savbill.radius.utils.*;
import com.savbill.radius.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Api(value = "Radius Customer Management", description = "REST APIs related to Customer Entity!!!!", tags = "Radius Customer")
@RestController
@RequestMapping("/SavbillRadius")
public class CustomerController {

    private static final String CUSTOMER = "customer";
    private static final String CUSTOMER_LIST = "customerList";

    @Autowired
    Tracer tracer;
    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomersServiceImpl customersServiceImpl;

    @Autowired
    private APIResponseController apiResponseController;
    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;
    @Autowired
    private LiveUserRepository liveUserRepository;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private LiveUserService liverUserService;
    @Autowired
    private UpdateDiffFinder updateDiffFinder;
    @Autowired
    DeviceService deviceService;
    @Autowired
    private MacAddressMappingRepository macAddressMappingRepository;

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    @ApiOperation(value = "Get list of customers in the system")
    @GetMapping("/customers")
//	@PreAuthorize("@roleAccesses.hasPermission('radiusCustomer','readAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_CUST + "\")")
    public ResponseEntity<Map<String, Object>> findAllCustomers(@RequestParam(name = "mvnoId", required = true) Integer mvnoId, @RequestParam(name = "staffId", required = true) Integer staffId, HttpServletRequest request, PaginationDTO paginationDTO) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Page<Customers> customerList = customerService.findAllCustomers(mvnoId, staffId, paginationDTO);
            List<String> customerList1 = new ArrayList<>();
            for (Customers customers : customerList) {
                customerList1.add(customers.getUsername());
            }
            for (Customers customer : customerList) {
                Integer custId = customer.getId();
                List<CustPlanMappping> planmappingList = custPlanMappingRepository.findAllByCustid(custId);
                customer.setPlanMappingList(planmappingList);
            }
            UsersDto usersDto = new UsersDto();
            usersDto.setUsers(customerList1);
            List<String> liveUserList = liverUserService.findUserStatusOnlineOrOffline(usersDto);
            response.put("liveUserList", liveUserList);
            response.put(CUSTOMER_LIST, customerList);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Radius Customers has been fetched successfully:," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Fetching customer list" + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @GetMapping("/customerById")
    @ApiOperation(value = "Get customer based on the given customer id.")
//	 @PreAuthorize("@roleAccesses.hasPermission('radiusCustomer','readAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_CUST_DETAILS + "\")")
    public ResponseEntity<Map<String, Object>> findCustomerById(
            @ApiParam(value = "Provide Customer Id", required = true) @RequestParam(name = "custid") Integer custid,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Customers customerVo = customerService.findCustomersByid(custid, mvnoId);
            if (customerVo.getParentCustomers() != null) {
                customerVo.setParentCustomerName(customerVo.getParentCustomers().getCustname());
            }
            if (customerVo.getPlanMappingList().isEmpty()) {
                List<CustPlanMappping> planmappingList = custPlanMappingRepository.findAllByCustid(custid);
                customerVo.setPlanMappingList(planmappingList);
            }
            //fetch mac retention date
            //fetch mac retention date
            List<Timestamp> macRetentionDate = macAddressMappingRepository.findNearestMacRetentionDateByCustomerId(Long.valueOf(custid));
            if (!CollectionUtils.isEmpty(macRetentionDate) && macRetentionDate != null && macRetentionDate.get(0) != null) {
                customerVo.setNearestMacRetentionDate(macRetentionDate.get(0).toLocalDateTime().toLocalDate());
            }
//            Optional<Timestamp> macRetentionDate = macAddressMappingRepository.findNearestMacRetentionDateByCustomerId(Long.valueOf(custid));
//            macRetentionDate.ifPresent(timestamp -> customerVo.setNearestMacRetentionDate(timestamp.toLocalDateTime().toLocalDate()));
            if (!CollectionUtils.isEmpty(customerVo.getPlanMappingList())) {
                LocalDateTime now = LocalDateTime.now();
                Optional<CustPlanMappping> custPlanMappping = customerVo.getPlanMappingList().stream()
                        .filter(plan -> "Active".equalsIgnoreCase(plan.getCustPlanStatus()) &&
                                plan.getPurchaseType() != null &&
                                !plan.getPurchaseType().equalsIgnoreCase("Volume Booster") &&
                                !plan.getPurchaseType().equalsIgnoreCase("Bandwidthbooster") &&
                                (plan.getStartDate().isBefore(now) || plan.getStartDate().isEqual(now)) &&
                                (plan.getEndDate().isAfter(now) || plan.getEndDate().isEqual(now))).findFirst();
                if (custPlanMappping.isPresent()) {
                    LocalDate quotaResetDate = customersServiceImpl.findNearestQuotaResetDateUsingCprId(custPlanMappping.get().getId());
                    customerVo.setQuotaResetDate(quotaResetDate);
                }
            }
            response.put(CUSTOMER, customerVo);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Radius Customer has been fetched successfully of Id:," + custid + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching radius customer with id " + custid + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @PostMapping("/terminateUserSession")
    @ApiOperation(value = "Terminate user session.")
    public ResponseEntity<Map<String, Object>> terminateUserSession(@RequestBody List<TerminateUser> userList, @RequestParam(required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            customerService.terminateUserSession(userList, mvnoId);
            response.put(RadiusConstants.MESSAGE, "User session terminated");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "User Session Is treminated Successfully:," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to terminate User-session " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }


    @GetMapping("/customerByName")
    @ApiOperation(value = "Get list of customers based on the given customer name.")
    // @PreAuthorize("@roleAccesses.hasPermission('radiusCustomer','readAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_CUST + "\")")
    public ResponseEntity<Map<String, Object>> findCustomerByName(PaginationDTO paginationDTO,
                                                                  @RequestParam(name = "name", required = false) String name, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Page<Customers> customerList = customerService.searchCustomersByName(name, mvnoId, paginationDTO);
            //    List<Customers> customerList1 = customerService.findCustomersByName(name , mvnoId);
            List<String> customerList1 = new ArrayList<>();
            for (Customers customers : customerList) {
                customerList1.add(customers.getUsername());
            }
            Integer responseCode = 0;
            if (CollectionUtils.isEmpty(customerList.getContent())) {
                responseCode = RadiusConstants.NULL_VALUE;
                response.put(RadiusConstants.ERROR_MESSAGE, "No Records Found!");
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Radius Customer has been fetched successfully with name," + name + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.NOT_FOUND.value());
            } else {
                UsersDto usersDto = new UsersDto();
                usersDto.setUsers(customerList1);
                List<String> liveusers = liverUserService.findUserStatusOnlineOrOffline(usersDto);
                responseCode = RadiusConstants.SUCCESS;
                response.put("liveUserList", liveusers);
                response.put(CUSTOMER_LIST, customerList);
            }
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Radius customer has been fetched Successfully  with name:," + name + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Fetching customer " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @PostMapping("/addCustomer")
    @ApiOperation(value = "Add new customer")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_CUST_CREATE + "\")")
//	 @PreAuthorize("@roleAccesses.hasPermission('radiusCustomer','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> addNewCustomer(
            @RequestBody CustomerDto customer,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Customer customerVo = customerService.addCustomer(customer, mvnoId);
            response.put(CUSTOMER, customerVo);
            response.put(RadiusConstants.MESSAGE, "Customer has been added successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Radius customer has been added Successfully :," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Creating customer " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @PutMapping("/updateCustomer")
    @ApiOperation(value = "Update eixsting customer based on the user name")

//	 @PreAuthorize("@roleAccesses.hasPermission('radiusCustomer','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> updateCustomer(
            @RequestBody UpdateCustomerDto customer,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Customer customerold = customerService.findCustomerByName(customer.getUserName(), mvnoId);
            Customer customerVo = customerService.updateCustomer(customer, mvnoId);
            response.put(CUSTOMER, customerVo);
            response.put(RadiusConstants.MESSAGE, "Customer has been updated successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Radius customer has been Updated Successfully  with updated:," + updateDiffFinder.getUpdatedDiff(customerVo, customerold) + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Updating customer " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);

        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @PutMapping("/updateCustomers/{custId}")
    @ApiOperation(value = "Update eixsting customer based on the user name")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_CUST_EDIT + "\")")
//	 @PreAuthorize("@roleAccesses.hasPermission('radiusCustomer','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> updateCustomers(
            @PathVariable(name = "custId", required = true) Integer custId,
            @RequestBody CustomerCreateData customer,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Customers customers = customersRepository.findByCustomerId(customer.getCustId());
            customer.setCustId(custId);
            Customers customerVo = customerService.updateCustomers(customer, mvnoId, false);
            response.put(CUSTOMER, customerVo);
            response.put(RadiusConstants.MESSAGE, "Customer has been updated successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Radius customer has been Updated Successfully, updated values ," + updateDiffFinder.getUpdatedDiff(customers, customerVo) + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Updating customer " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @DeleteMapping("/deleteCustomer")
    @ApiOperation(value = "Delete existing customer based on the given user name")
//	 @PreAuthorize("@roleAccesses.hasPermission('radiusCustomer','deleteAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> deleteCustomer(
            @RequestParam(name = "userName", required = true) String userName,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            customerService.deleteCustomer(userName, mvnoId);
            response.put(RadiusConstants.MESSAGE, "Customer has been deleted successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Radius customer has been Deleted Successfully  with name:," + userName + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Deleting customer with name " + userName + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @PutMapping("/updateStatus/{custId}")
    @ApiOperation(value = "Update customer status based on the given user name and status")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_CUST_CHANGE_STATUS + "\")")
//	 @PreAuthorize("@roleAccesses.hasPermission('radiusCustomer','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> updateCustomerStatusById(
            @PathVariable(name = "custId", required = true) Integer custId,
            @RequestParam(name = "status", required = true) String status,
            @RequestParam(name = "ramark", required = false) String remark, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Customers customers = customersRepository.findByCustomerId(custId);
            String message = customerService.updateCustomerStatus(custId, status, remark, false, customers.getUsername());
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(RadiusConstants.MESSAGE, message);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Radius customer Status Has been updated successfully with status:," + customers.getStatus() + " updated to " + status + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Validate customer Concurrency " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @GetMapping("/updateCustomerStatus")
    @ApiOperation(value = "Update customer status based on the given user name and status")

//	 @PreAuthorize("@roleAccesses.hasPermission('radiusCustomer','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> updateCustomerStatus(
            @RequestParam(name = "userName", required = true) String userName,
            @RequestParam(name = "status", required = true) String status,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Customer customers = customerService.findCustomerByName(userName, mvnoId);
            String message = customerService.updateCustomerStatus(userName, status, mvnoId);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(RadiusConstants.MESSAGE, message);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Radius customer's status has been Updated Successfully  with name:," + customers.getCustomerStatus() + "is updated to" + status + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Update Radius Customer's status with name " + userName + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    //	@ApiOperation(value = "Recharge Quota of customer")
//    @PutMapping("/rechargeQuota")
//    @PreAuthorize("@roleAccesses.hasPermission('wifiCustomer','createUpdateAccess',#request.getHeader('requestFrom'))")
//	public ResponseEntity<Map<String, Object>> rechargeQuota(@RequestParam(name = "custId", required = true) Long custId,
//		    @RequestParam(name = "mvnoId", required = true) Long mvnoId,
//		    @RequestBody Customer customer,HttpServletRequest request) {
//	MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
//	Map<String, Object> response = new HashMap<>();
//	try {
//		customerService.rechargeQuota(custId,customer, mvnoId);
//	    response.put(RadiusConstants.MESSAGE, "Customer Quota Recharge has been updated successfully.");
//	    log.debug("Customer Quota successfully updated");
//	    return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
//	} catch (Exception e) {
//	    log.error("Error while recharge customer quota " + e.getMessage());
//	    apiResponseController.buildErrorMessageForResponse(response, e);
//	    return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
//	} finally {
//	    MDC.remove(RadiusConstants.TYPE);
//	}
//    }
    @ApiOperation(value = "Recharge Quota of customer")
    @PutMapping("/rechargeQuota")
//	    @PreAuthorize("@roleAccesses.hasPermission('wifiCustomer','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> crossRechargeQuota(@RequestParam(name = "custId", required = true) Long custId,
                                                                  @RequestParam(name = "allowCrossRecharge", required = true) Boolean allowCrossRecharge,
                                                                  @RequestParam(name = "mvnoId", required = true) Long mvnoId,
                                                                  @RequestBody Customer customer, HttpServletRequest request) {
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            customerService.rechargeQuota(custId, customer, allowCrossRecharge, mvnoId);
            response.put(RadiusConstants.MESSAGE, "Customer Quota Recharge has been updated successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Radius customer Quota has been Updated Successfully  with id:," + custId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Updating Customer Quota  " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            apiResponseController.buildErrorMessageForResponse(response, e);
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Update customer password")
    @PutMapping("/changePassword")
//	 @PreAuthorize("@roleAccesses.hasPermission('radiusCustomer','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestBody CustomerPasswordDto passwordDto,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());

        try {
            customerService.changePassword(passwordDto, mvnoId);
            response.put(RadiusConstants.MESSAGE, "Password has been updated successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Radius customer password has been Updated Successfully  with name:," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "unable to update password " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @PostMapping("/Login")
    @ApiOperation(value = "Login customer based on the given Username and Password.")
    public ResponseEntity<Map<String, Object>> validateCustomer(@RequestBody LoginDto login,
                                                                @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_LOGIN);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put("Customer", customerService.validateLoginUser(login, mvnoId));
            response.put(RadiusConstants.MESSAGE, "Login User successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Login:," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            log.error("Error while login " + login.getUserName() + " " + e.getMessage());
            ResponseEntity responseEntity = null;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            if (e.getMessage().contains(RadiusConstants.QUOTA_USED)) {
                response.put(RadiusConstants.ERROR_MESSAGE, "Your current quota is consumed and can not login");
                responseEntity = apiResponseController.apiResponse(RadiusConstants.QUOTA, response);
            } else if (e.getMessage().contains(RadiusConstants.NOT_FOUND)) {
                responseEntity = apiResponseController.apiResponse(RadiusConstants.NOTFOUND, response);
            } else if (e.getMessage().contains(RadiusConstants.IN_ACTIVE)) {
                responseEntity = apiResponseController.apiResponse(RadiusConstants.INACTIVE, response);
            } else if (e.getMessage().contains(RadiusConstants.EXPIRED_USER)) {
                response.put(RadiusConstants.ERROR_MESSAGE, "Your account is expired, Please recharge.");
                responseEntity = apiResponseController.apiResponse(RadiusConstants.EXPIRED, response);
            } else {
                responseEntity = apiResponseController.apiResponse(RadiusConstants.FAIL, response);
            }
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to login " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return responseEntity;
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @GetMapping("/Logout")
    @ApiOperation(value = "Logout customer based on the given Username")
    public ResponseEntity<Map<String, Object>> validateCustomer(@RequestParam(name = "userName") String userName, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_LOGIN);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            customerService.validateLogoutUser(userName);
            response.put(RadiusConstants.MESSAGE, "Logout User successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Logout User successfully.," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Customer Log out" + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @PutMapping("/updateCustomerExpiry")
    @ApiOperation(value = "Update eixsting customer's end date based on the name")
//	@PreAuthorize("@roleAccesses.hasPermission('radiusCustomer','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> updateCustomerEndDate(
            @RequestParam(name = "endDate", required = true) String endDate, @RequestParam(name = "name", required = true) String name,
            @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Customers customerVo = customerService.updateCustomerEndDate(endDate, name, mvnoId);
            response.put(CUSTOMER, customerVo);
            response.put(RadiusConstants.MESSAGE, "Customer end date has been updated successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Customer end date has been updated successfully..," + endDate + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error While Upadating radius customer end date  " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @GetMapping("/getFuturePlanList/{customerId}")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_CUST_DETAILS_CUST_PLAN + "\")")
    public ResponseEntity<?> getFuturePlanList(@PathVariable Integer customerId, HttpServletRequest request) {
        org.slf4j.MDC.put("type", "Fetch");
        Map<String, Object> response = new HashMap<>();
        String SUBMODULE = "[CustomerController]--> [getFuturePlanList()] ";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            if (null == customerId) {
                throw new RuntimeException("Please enter valid customer id..");
            }
            List<CustPlanMappping> custPlanMapppings = new ArrayList<>();
            custPlanMapppings = customerService.findFutureByCustid(customerId, now);
            if (null == custPlanMapppings) {
                throw new RuntimeException("Customer not found with this id .");
            } else if (custPlanMapppings.size() == 0) {
                response.put("dataList", custPlanMapppings);
                response.put(RadiusConstants.MESSAGE, "no data found");
            } else {
                response.put("dataList", custPlanMapppings);
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching Future Plan List is Successfull for customer .," + customerId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                response.put(RadiusConstants.MESSAGE, "getFuturePlanList for customer is successfull");
            }

            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception ex) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch future planlist for customer " + customerId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            //logger.error(SUBMODULE + ex.getStackTrace(), ex);
            throw new RuntimeException(ex.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @GetMapping("/getExpiredPlanList/{customerId}")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_CUST_DETAILS_CUST_PLAN + "\")")
    public ResponseEntity<?> getExpiredPlanList(@PathVariable Integer customerId, HttpServletRequest request) {
        org.slf4j.MDC.put("type", "Fetch");
        Map<String, Object> response = new HashMap<>();
        String SUBMODULE = "[CustomerController]--> [getExpiredPlanList()] ";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            if (null == customerId) {
                throw new RuntimeException("Please enter valid customer id..");
            }
            List<CustPlanMappping> custPlanMapppings = new ArrayList<>();
            custPlanMapppings = customerService.findExpiredByCustid(customerId, now);
            if (null == custPlanMapppings) {
                throw new RuntimeException("Customer not found with this id .");
            } else if (custPlanMapppings.size() == 0) {
                response.put("dataList", custPlanMapppings);
                response.put(RadiusConstants.MESSAGE, "data not found");
            } else {
                response.put("dataList", custPlanMapppings);
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Customer end date has been updated successfully..," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                response.put(RadiusConstants.MESSAGE, "getExpiredPlanList for customer is successfull");
            }
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception ex) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch expired planlist for customer  " + customerId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            //logger.error(SUBMODULE + ex.getStackTrace(), ex);
            throw new RuntimeException(ex.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @GetMapping("/getActivePlanList/{customerId}")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_CUST_DETAILS_CUST_PLAN + "\")")
    public ResponseEntity<?> getActivePlanList(@PathVariable Integer customerId, HttpServletRequest request) {
        org.slf4j.MDC.put("type", "Fetch");
        Map<String, Object> response = new HashMap<>();
        String SUBMODULE = "[CustomerController]--> [getActivePlanList()] ";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());

        try {
            if (null == customerId) {
                throw new RuntimeException("Please enter valid customer id..");
            }
            List<CustPlanMappping> custPlanMapppings = new ArrayList<>();
            custPlanMapppings = customerService.findActiveByCustid(customerId, now);
            if (null == custPlanMapppings) {
                throw new RuntimeException("Customer not found with this id .");
            } else if (custPlanMapppings.size() == 0) {
                response.put("dataList", custPlanMapppings);
                response.put(RadiusConstants.MESSAGE, "data not found");
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Fetchinf Active plan List," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.NOT_FOUND.value());
            } else {
                response.put("dataList", custPlanMapppings);
                response.put(RadiusConstants.MESSAGE, "getActivePlanList for customer is successfull");
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching Active Plan List is Successfull for customer .," + customerId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            }
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception ex) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch Active plan list for customer id " + customerId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            //logger.error(SUBMODULE + ex.getStackTrace(), ex);
            throw new RuntimeException(ex.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get list of customers by search in the system")
    @PostMapping("/customers/search")
    public ResponseEntity<Map<String, Object>> findAllCustomers(PaginationDTO paginationDTO, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, @RequestBody CustomerSearch customerSearch, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());

        try {
            PageableResponse<Customers> page = customerService.findAllCustomerBySearch(mvnoId, paginationDTO, customerSearch);
            int responseCode;
            if (CollectionUtils.isEmpty(page.getData())) {
                responseCode = RadiusConstants.NULL_VALUE;
                if (StringUtils.isEmpty(customerSearch.getUsername())) {
                    response.put(RadiusConstants.ERROR_MESSAGE, "No Record found.");
                } else {
                    response.put(RadiusConstants.ERROR_MESSAGE, "No Record found for username:" + customerSearch.getUsername());
                    log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Fetching Radius Customer ," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.NOT_FOUND.value());
                }
            } else {
                responseCode = RadiusConstants.SUCCESS;
                response.put(CUSTOMER_LIST, page);
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching Radius customer for username.," + customerSearch.getUsername() + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            }
            if (!StringUtils.isEmpty(customerSearch.getUsername())) {
                //		System.out.println("Radius Customers has been fetched successfully by userName : " + customerSearch.getUsername() + " By: " + MDC.get(RadiusConstants.USER_NAME));
            } else {
                //		System.out.println("Radius Customers has been fetched successfully by " + MDC.get(RadiusConstants.USER_NAME));
            }
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error("Error while fetching radius customers with name : " + customerSearch.getUsername() + " " + e.getMessage());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @PutMapping("/updateCustomerConcurrency")
    @ApiOperation(value = "Update eixsting customer concurrency based on the user name")
//	 @PreAuthorize("@roleAccesses.hasPermission('radiusCustomer','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> updateCustomerConcurrency(
            @RequestBody UpdateCustomerDto customer, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Customers customers = customersRepository.findByCustomerId(customer.getCustId());
            customerService.updateCustomerConcurrency(customer);
            response.put(RadiusConstants.MESSAGE, "Customer concurrency has been updated successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching  customer concurrency for customer., from " + customers.getMaxconcurrentsession() + " to " + customer.getMaxconcurrentsession() + customer + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Updating  customer Concurrency" + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @GetMapping("/checkCompareConcurrency")
    @ApiOperation(value = "Check user concurrency.")
    public ResponseEntity<Map<String, Object>> checkConcurrencyByCompare(
            @RequestParam(name = "userName", required = true) String userName,
            @RequestParam(name = "mac", required = false) String mac,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            String msg = customerService.checkConcurrencyByCompare(userName, mac, mvnoId);
            response.put(RadiusConstants.MESSAGE, msg);
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(mac)) {
                //		System.out.println("Radius customer validated for login successfully, for customer username: " + userName
//                        + " but mac address not found");
            } else {
                //		System.out.println("Radius customer validated for login successfully, for customer username: " + userName
//                        + " and mac: " + mac);
            }
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Radius customer validated Successfully for username.," + userName + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception exception) {
            response.put(RadiusConstants.ERROR_MESSAGE, exception.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Validate customer Concurrency " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @PostMapping("/addNewCustomer")
    @ApiOperation(value = "Add new customer")
//	 @PreAuthorize("@roleAccesses.hasPermission('radiusCustomer','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> addCustomer(
            @RequestBody CustomerCreateData customer,
            @RequestParam(name = "mvnoId", required = false) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            CustomerCreateData customerVo = customerService.addNewCustomers(customer, mvnoId, false);
            response.put(CUSTOMER, customerVo);
            response.put(RadiusConstants.MESSAGE, "Customer has been added successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Customer has been added successfully.," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Creating Concurrency customer" + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @GetMapping("/custUsernameIsAlreadyExists")
    @ApiOperation(value = "Check userName Exists.")
    public ResponseEntity<Map<String, Object>> checkCustomerUserNameExists(
            @RequestParam(name = "userName", required = true) String userName,
            @RequestParam(name = "mvnoId", required = false) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            boolean msg = customerService.customerUserNameExists(userName, mvnoId);
            response.put("isAlreadyExists", msg);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Checking For user concurrency ," + userName + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception exception) {
            response.put(RadiusConstants.ERROR_MESSAGE, exception.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while validating user concurrency" + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            log.error("Error while validate user concurrency : " + userName + " " + exception.getMessage());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);

        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @PostMapping("/defaultprovision")
    @ApiOperation(value = "provision")
//	 @PreAuthorize("@roleAccesses.hasPermission('radiusCustomer','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> defaultLeaseIPv4provision(
            @RequestBody CustomerCreateData customer) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            CustomerCreateData customerVo = customerService.defaultLeaseIPv4provision(customer, false);
            response.put(CUSTOMER, customerVo);
            response.put(RadiusConstants.MESSAGE, "defaultLeaseIPv4 provision has been successfull.");
            log.info(LogConstants.REQUEST_FROM + LogConstants.REQUEST_FOR + "defoult provision has been successfull.," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + LogConstants.REQUEST_FOR + "Error while Creating Concurrency customer" + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    @PutMapping("/defaultLeaseIPv4Update/{username}")
    @ApiOperation(value = "Update existing defaultLeaseIPv4 based on the custId")
    //  @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_CUST_EDIT +"\")")
//	 @PreAuthorize("@roleAccesses.hasPermission('radiusCustomer','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> defaultLeaseIPv4Update(
            @PathVariable(name = "username", required = true) String username,
            @RequestBody CustomerCreateData customer, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());

        try {
            Customers customerVo = customerService.defaultLeaseIPv4Update(customer, username, false);
            response.put(CUSTOMER, customerVo);
            response.put(RadiusConstants.MESSAGE, "Customer has been updated successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "defoult provision  has been Updated Successfully  with updated:," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);

        } catch (Exception e) {

            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Updating defoult provision  " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);

        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    @DeleteMapping("/DefaultDeprovision")
    @ApiOperation(value = "Defoult deprovision based on the given username")
//	 @PreAuthorize("@roleAccesses.hasPermission('radiusCustomer','deleteAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> defoultDeprovision(
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "gatewayIpBind", required = true) String gatewayIP, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            customerService.defoultDeprovision(username, gatewayIP, false);
            response.put(RadiusConstants.MESSAGE, "Defoult deprovision has been  successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "netconf defoult deprovision has been Successfully with username:," + username + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {

            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while defoult deprovision with username " + username + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);

        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    @DeleteMapping("/deleteCustomers")
    @ApiOperation(value = "Delete existing customer based on the given custId")
//	 @PreAuthorize("@roleAccesses.hasPermission('radiusCustomer','deleteAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> deleteCustomer(
            @RequestParam(name = "custId", required = true) Integer custId,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            customerService.deleteCustomers(custId, mvnoId, false);
            response.put(RadiusConstants.MESSAGE, "Customer has been deleted successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "netconf customer has been Deleted Successfully  with name:," + custId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {

            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Deleting customer with custId " + custId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);

        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    @GetMapping("/customer/custQuota/{id}")
    public ResponseEntity<?> custQuotaList(@PathVariable Integer id, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = RadiusConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<CustQuotaDetails> list = customerService.findAllByCustomersId(id);
            list = list.stream().filter(custQuotaDetails -> custQuotaDetails.getCustPlanMappping() != null).peek(custQuotaDetails -> custQuotaDetails.setCprId(custQuotaDetails.getCustPlanMappping().getId())).collect(Collectors.toList());
            response.put("custQuotaList", list);
            RESP_CODE = RadiusConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch custQuota by id: " + id + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ce) {
            //		logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch custQuota by id: " + id + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(RESP_CODE, response);
    }

    @PostMapping("/customer/logoutCustomer")
    @ApiOperation(value = "This api will allow customer to end there internet session.")
    public ResponseEntity<Map<String, Object>> logoutCustomer(
            @RequestBody LogoutCustomerDTO logoutCustomerDTO,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();
            CustomerData custRetrunData = dbAuth.getDBCustomer(null, logoutCustomerDTO.getMvnoId(), logoutCustomerDTO.getCustId().toString(), null, false);
            changeUserData changeUserData = new changeUserData(logoutCustomerDTO.getUsername(),
                    Long.valueOf(logoutCustomerDTO.getMvnoId()));
            List<changeUserData> userList = new ArrayList<changeUserData>();
            userList.add(changeUserData);
            String event = CommonConstants.EVENTCONSTANTS.CUSTOMER_LOGOUT;

            if (event != null || !event.isEmpty()) {
                customerService.CoADMSupport(userList, "COA", custRetrunData, event);
//                RadiusAsyncUtility radaysn=new RadiusAsyncUtility();
//                radaysn.coaDMProcess(userList,"COA",custRetrunData,event);
            }
            response.put("msg", "Customer Logout successfully");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Customer logout by username ," + logoutCustomerDTO.getUsername() + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception exception) {
            response.put(RadiusConstants.ERROR_MESSAGE, exception.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while customer logout" + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            log.error("Error while customer logout by customer : " + logoutCustomerDTO.getUsername() + " " + exception.getMessage());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);

        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @PostMapping("/customer/logoutSNMPCustomer")
    @ApiOperation(value = "This api will allow SNMP  customer to end there internet session.")
    public ResponseEntity<Map<String, Object>> logoutSNMPCustomer(
            @RequestBody LogoutCustomerDTO logoutCustomerDTO,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            String framed_ip_address = request.getHeader("X-Forwarded-For");
            log.info("framed_ip_address from request: " + framed_ip_address);
            deviceService.generateSNMP(logoutCustomerDTO.getMvnoId(), logoutCustomerDTO.getUsername(), framed_ip_address, false, true);
            response.put("msg", "Customer Logout successfully");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Customer logout by username ," + logoutCustomerDTO.getUsername() + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception exception) {
            response.put(RadiusConstants.ERROR_MESSAGE, exception.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while customer logout" + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            log.error("Error while SNMP customer logout by customer : " + logoutCustomerDTO.getUsername() + " " + exception.getMessage());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);

        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }


    @GetMapping("/postpaidplan/{planId}")
    public ResponseEntity<?> getPlanById(@PathVariable Integer planId, HttpServletRequest request) {
        org.slf4j.MDC.put("type", "Fetch");
        Map<String, Object> response = new HashMap<>();
        String SUBMODULE = "[CustomerController]--> [getExpiredPlanList()] ";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            if (null == planId) {
                throw new RuntimeException("Please enter valid Plan id..");
            }

            PostpaidPlan postpaidPlan = customerService.findByPlanId(planId);
            if (null == postpaidPlan) {
                throw new RuntimeException("Plan not found with this id .");
            } else {
                response.put("data", postpaidPlan);
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Plan is fetched successfully..," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                response.put(RadiusConstants.MESSAGE, "Plan for Plan id " + planId + " is successfull");
            }
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception ex) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch pla for plan id  " + planId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            //logger.error(SUBMODULE + ex.getStackTrace(), ex);
            throw new RuntimeException(ex.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @PostMapping("/customer/logout")
    @ApiOperation(value = "This api will allow customer to logout internet session.")
    public ResponseEntity<Map<String, Object>> customerLogout(
            @RequestBody LogoutCustomerDTO logoutCustomerDTO,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();
            CustomerData custRetrunData = dbAuth.getDBCustomer(null, logoutCustomerDTO.getMvnoId(), logoutCustomerDTO.getCustId().toString(), null, false);
            changeUserData changeUserData = new changeUserData(logoutCustomerDTO.getUsername(),
                    Long.valueOf(logoutCustomerDTO.getMvnoId()));
            List<changeUserData> userList = new ArrayList<changeUserData>();
            userList.add(changeUserData);
            String event = CommonConstants.EVENTCONSTANTS.CUSTOMER_LOGOUT;

            String framed_ip_address = request.getHeader("X-Forwarded-For");
            log.info("framed_ip_address from request: " + framed_ip_address);
            if (framed_ip_address.contains(",")) {
                framed_ip_address = framed_ip_address.split(",")[0];
                log.info("updated framed_ip_address from request: " + framed_ip_address);
            }

            logoutCustomerDTO.setFramedIP(framed_ip_address);

            response = customerService.logoutCustomer(userList, logoutCustomerDTO, custRetrunData, event);

//            response.put("msg", "Customer Logout successfully");
            if (response.get("status").equals(500)) {
                response.put(RadiusConstants.MESSAGE, response.get("errorMessage"));
                return apiResponseController.apiResponse(RadiusConstants.EMPTY, response);
            }
            if (response.get("status").equals(417)) {
                response.put(RadiusConstants.MESSAGE, response.get("error"));
                return apiResponseController.apiResponse(RadiusConstants.NULL_VALUE, response);
            }
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Customer logout by username ," + logoutCustomerDTO.getUsername() + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception exception) {
            response.put(RadiusConstants.ERROR_MESSAGE, exception.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while customer logout" + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            log.error("Error while customer logout by customer : " + logoutCustomerDTO.getUsername() + " " + exception.getMessage());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);

        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }
}
