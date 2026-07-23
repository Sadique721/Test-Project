package com.savbill.integrationsystem.middleware.payment.service;

import com.savbill.integrationsystem.billgen.service.BranchService;
import com.savbill.integrationsystem.core.CommonConstant;
import com.savbill.integrationsystem.deviceveri.model.*;
import com.savbill.integrationsystem.deviceveri.service.*;
import com.savbill.integrationsystem.deviceveri.model.*;
import com.savbill.integrationsystem.deviceveri.service.*;
import com.savbill.integrationsystem.middleware.payment.dto.customerdetail.CustomerDetailsResponse;
import com.savbill.integrationsystem.middleware.payment.dto.customerdetail.PgCustomerDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final CustomersService customersService;

    private final CustServiceMappingService custServiceMappingService;

    private final TblServicesService servicesService;

    private final CustomerInventoryMappingService customerInventoryMappingService;

    private final SerializedItemService serializedItemService;

    private final CustomerPackageRelService customerPackageRelService;

    private final PostpaidPlanService postpaidPlanService;

    private final BranchService branchService;

    public CustomerDetailsResponse getCustomerDetail(String username) {
        CustomerDetailsResponse customerDetailsResponse = new CustomerDetailsResponse();
        PgCustomerDetail pgCustomerDetail = new PgCustomerDetail();
        try {
            CustomersDTO customersDTO = customersService.findByUsername(username).stream().findFirst().orElse(null);
            if(ObjectUtils.isEmpty(customersDTO)){
                log.debug("Cannot find user with the username: {}", username);
                pgCustomerDetail.setReturnCode("1");
                pgCustomerDetail.setReturnMessage("UserId is not valid");
                customerDetailsResponse.setPgCustomerDetail(pgCustomerDetail);
                return customerDetailsResponse;
            }
            log.debug("Customer found customerID: {}", customersDTO.getCustid());
            pgCustomerDetail.setStatus(customersDTO.getStatus());
            pgCustomerDetail.setName((customersDTO.getFirstname() + " " + customersDTO.getLastname().trim()));
            pgCustomerDetail.setUserId(username);

            /*tblmsubscriberaddressrel*/
//            pgCustomerDetail.setAddress(customersDTO.getA);
            pgCustomerDetail.setArea("");
            pgCustomerDetail.setArea("");
            pgCustomerDetail.setCity("");
            pgCustomerDetail.setNation("");
            pgCustomerDetail.setEMail(customersDTO.getEmail());
            pgCustomerDetail.setMobileNo(customersDTO.getMobile());
            pgCustomerDetail.setTelephone(CommonConstant.FIELD_NOT_AVAILABLE);

            /*tblmcustledger*/
            pgCustomerDetail.setOutstandingAmount("");

            /*tblpartners, tblmbranch*/
            if(customersDTO.getBranchid() != null) {

            }
            pgCustomerDetail.setPartnerName("");

            CustomerPackageRelDTO customerPackageRelDTO = customerPackageRelService.findByCustidAndStartdateBeforeAndEnddateAfterAndIsDeleteFalse(
                    customersDTO.getCustid(),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            ).stream().findFirst().orElse(null);
            if(!ObjectUtils.isEmpty(customerPackageRelDTO)) {
                PostpaidPlanDTO postpaidPlanDTO = postpaidPlanService.findByPostpaidplanidAndIsDeleted(customerPackageRelDTO.getPlanid(), 0)
                        .stream()
                        .findFirst()
                        .orElse(null);
                if(!ObjectUtils.isEmpty(postpaidPlanDTO)) {
                    pgCustomerDetail.setCurrentPlanName(postpaidPlanDTO.getDisplayname());
                }
            }

            List<CustomerServiceMappingDTO> listCustomerServiceMappingDTOS = custServiceMappingService.findByCustidAndIsDelete(customersDTO.getCustid(), 0);
            for(CustomerServiceMappingDTO customerServiceMappingDTO : listCustomerServiceMappingDTOS) {
                ServicesDTO servicesDTO = servicesService.findByServiceid(customerServiceMappingDTO.getServiceid()).stream().findFirst().orElse(null);
                if(ObjectUtils.isEmpty(servicesDTO)){
                    log.warn("Cannot find the service for serviceIs: {}", customerServiceMappingDTO.getServiceid());
                    continue;
                }
                Boolean isDTV = servicesDTO.getIsDtv();
                if(isDTV) {
                    CustomerInventoryMappingDTO customerInventoryMappingDTO = customerInventoryMappingService.findByCustomerIdAndIsDeleted(customersDTO.getCustid(), 0).stream().findFirst().orElse(null);
                    if(ObjectUtils.isEmpty(customerInventoryMappingDTO)) {
                        log.debug("Cannot find customerInventoryMappingDTO for custId: {}", customersDTO.getCustid());
                        continue;
                    }
                    SerializedItemDTO serializedItemDTO = serializedItemService.findByIdAndIsDeleted(customerInventoryMappingDTO.getItemId(), 0).stream().findFirst().orElse(null);
                    if(ObjectUtils.isEmpty(serializedItemDTO)){
                        log.debug("Cannot find serializedItemDTO with itemId: {}", customerInventoryMappingDTO.getItemId());
                        continue;
                    }
                    pgCustomerDetail.setOnuId(serializedItemDTO.getSerialNumber());
                } else {

                }
            }

        } catch (Exception exception) {
            log.error("Exception occurred while getting custome details", exception);
            pgCustomerDetail.setReturnMessage("Fail");
            pgCustomerDetail.setReturnCode("1");
        }
        customerDetailsResponse.setPgCustomerDetail(pgCustomerDetail);
        return customerDetailsResponse;
    }
}
