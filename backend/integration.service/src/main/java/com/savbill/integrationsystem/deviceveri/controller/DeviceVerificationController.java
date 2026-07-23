package com.savbill.integrationsystem.deviceveri.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.savbill.integrationsystem.deviceveri.dto.*;
import com.savbill.integrationsystem.deviceveri.dto.*;
import com.savbill.integrationsystem.deviceveri.dto.Package;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.savbill.integrationsystem.deviceveri.dto.devicedetails.DeviceDetailsResponse;
import com.savbill.integrationsystem.deviceveri.dto.subscriberinfo.SubscriberInfoResponse;
import com.savbill.integrationsystem.deviceveri.dto.transactiondetail.NextExpire;
import com.savbill.integrationsystem.deviceveri.dto.transactiondetail.Payment;
import com.savbill.integrationsystem.deviceveri.dto.transactiondetail.PaymentOn;
import com.savbill.integrationsystem.deviceveri.dto.transactiondetail.TransactionDetailsResponse;
import com.savbill.integrationsystem.deviceveri.model.CreditDebitMappingDTO;
import com.savbill.integrationsystem.deviceveri.model.CreditDocDTO;
import com.savbill.integrationsystem.deviceveri.model.CustomerInventoryMappingDTO;
import com.savbill.integrationsystem.deviceveri.model.CustomerPackageRelDTO;
import com.savbill.integrationsystem.deviceveri.model.CustomerServiceMappingDTO;
import com.savbill.integrationsystem.deviceveri.model.CustomersDTO;
import com.savbill.integrationsystem.deviceveri.model.DebitDocDTO;
import com.savbill.integrationsystem.deviceveri.model.PostpaidPlanDTO;
import com.savbill.integrationsystem.deviceveri.model.SerializedItemDTO;
import com.savbill.integrationsystem.deviceveri.service.CreditDebitMappingService;
import com.savbill.integrationsystem.deviceveri.service.CreditDocService;
import com.savbill.integrationsystem.deviceveri.service.CustServiceMappingService;
import com.savbill.integrationsystem.deviceveri.service.CustomerInventoryMappingService;
import com.savbill.integrationsystem.deviceveri.service.CustomerPackageRelService;
import com.savbill.integrationsystem.deviceveri.service.CustomersService;
import com.savbill.integrationsystem.deviceveri.service.DebitDocService;
import com.savbill.integrationsystem.deviceveri.service.PostpaidPlanService;
import com.savbill.integrationsystem.deviceveri.service.SerializedItemService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/api/device-verification")
public class DeviceVerificationController {

    private static final String ACTION_SUBSCRIBER_INFO = "subscriber_info";
    private static final String ACTION_DEVICE_DETAIL = "device_detail";
    private static final String ACTION_TRANSACTION_DETAIL = "transaction_detail";
    private static final Integer NOT_DELETED = 0;

    @Autowired
    SerializedItemService serializedItemService;

    @Autowired
    CustomerInventoryMappingService customerInventoryMappingService;

    @Autowired
    CustServiceMappingService customerServiceMappingService;

    @Autowired
    CustomersService customersService;

    @Autowired
    CustomerPackageRelService customerPackageRelService;

    @Autowired
    PostpaidPlanService postpaidPlanService;

    @Autowired
    CreditDebitMappingService creditDebitMappingService;

    @Autowired
    CreditDocService creditDocService;

    @Autowired
    DebitDocService debitDocumentService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Object> getDeviceVerification(@RequestParam("action") String action,
                                                        @RequestParam(name = "device_id", required = false) String deviceId,
                                                        @RequestParam(name = "from", required = false) String from,
                                                        @RequestParam(name = "to", required = false) String to) {

        SubscriberInfoResponse subscriberInfoResponse = new SubscriberInfoResponse();
        if (action.equalsIgnoreCase(ACTION_DEVICE_DETAIL) || action.equalsIgnoreCase(ACTION_SUBSCRIBER_INFO)) {
            DeviceDetailsResponse deviceDetailsResponse = getDeviceDataResponse(deviceId);
            if (action.equalsIgnoreCase(ACTION_SUBSCRIBER_INFO)) {
                if (deviceDetailsResponse.getSubscriber() != null) {
                    subscriberInfoResponse.setSubscriberData(deviceDetailsResponse.getSubscriber());
                    return ResponseEntity.ok(subscriberInfoResponse);
                } else {
                    return ResponseEntity.ok("No records found with the given details!");
                }
            } else if (action.equalsIgnoreCase(ACTION_DEVICE_DETAIL)) {
                if(!StringUtils.hasLength(deviceDetailsResponse.getPackageName())) {
                    return ResponseEntity.ok("No records found with the given details!");
                } else {
                    return ResponseEntity.ok(deviceDetailsResponse);
                }
            }
        } else if (action.equalsIgnoreCase(ACTION_TRANSACTION_DETAIL)) {
            TransactionDetailsResponse transactionDetailsResponse = new TransactionDetailsResponse();
            List<Payment> listPayments;

            listPayments = getTransactionDetail(from, to);
            transactionDetailsResponse.setFrom(from);
            transactionDetailsResponse.setTo(to);
            transactionDetailsResponse.setPayments(listPayments);
            return ResponseEntity.ok(transactionDetailsResponse);
        } else {
            return ResponseEntity
                    .ok("Please enter a valid action: subscriber_info, device_detail or transaction_detail");
        }
        return ResponseEntity.ok("Cannot find the valid data");
    }

    private DeviceDetailsResponse getDeviceDataResponse(String deviceId) {
        DeviceDetailsResponse deviceDetailsResponse = new DeviceDetailsResponse();

        try {
            List<SerializedItemDTO> listSerializedItem = serializedItemService.findBySerialNumberAndIsDeleted(deviceId, NOT_DELETED);
            if (!CollectionUtils.isEmpty(listSerializedItem)) {
                SerializedItemDTO serializedItemDTO = listSerializedItem.get(0);
                List<CustomerInventoryMappingDTO> listCustomerInventoryMappingDTOs = customerInventoryMappingService.findByItemIdAndIsDeleted(serializedItemDTO.getId(), NOT_DELETED);
                if (!CollectionUtils.isEmpty(listCustomerInventoryMappingDTOs)) {
                    CustomerInventoryMappingDTO customerInventoryMappingDTO = listCustomerInventoryMappingDTOs.get(0);
                    List<CustomerServiceMappingDTO> listCustomerServiceMappingDTOs = customerServiceMappingService.findByConnectionNoAndIsDelete(customerInventoryMappingDTO.getConnectionNo(), NOT_DELETED);
                    if (!CollectionUtils.isEmpty(listCustomerServiceMappingDTOs)) {
                        CustomerServiceMappingDTO customerServiceMappingDTO = listCustomerServiceMappingDTOs.get(0);
                        List<CustomerPackageRelDTO> lisCustomerPackageRelDTOs = customerPackageRelService.findByCustservicemappingid(customerServiceMappingDTO.getId());
                        if (!CollectionUtils.isEmpty(lisCustomerPackageRelDTOs)) {
                            CustomerPackageRelDTO customerPackageRelDTO = lisCustomerPackageRelDTOs.get(0);
                            List<CustomersDTO> listCustomersDTOs = customersService.findByCustid(customerServiceMappingDTO.getCustid());
                            if (!CollectionUtils.isEmpty(listCustomersDTOs)) {
                                CustomersDTO customersDTO = listCustomersDTOs.get(0);
                                Boolean master = Boolean.TRUE;
                                CustomersDTO parentCustomersDTO = customersDTO;
                                if (customersDTO.getParentcustid() != null) {
                                    master = false;
                                    parentCustomersDTO = customersService.findByCustid(Long.valueOf(customersDTO.getParentcustid())).get(0);
                                }

                                SubscriberData subscriberData = new SubscriberData();
                                subscriberData.setSubscriberName((parentCustomersDTO.getFirstname() + " " + parentCustomersDTO.getLastname()).trim());
                                subscriberData.setSubscriberCode(parentCustomersDTO.getCustid().toString());
                                subscriberData.setPhoneNumber(parentCustomersDTO.getMobile());

                                List<PostpaidPlanDTO> listPostpaidPlanDTOs = postpaidPlanService.findByPostpaidplanidAndIsDeleted(customerPackageRelDTO.getPlanid(), NOT_DELETED);
                                if (!CollectionUtils.isEmpty(listPostpaidPlanDTOs)) {
                                    PostpaidPlanDTO postpaidPlanDTO = listPostpaidPlanDTOs.get(0);
                                    String packagePrice = "NPR 0.0";
                                    if (master && postpaidPlanDTO.getOfferprice() != null) {
                                        packagePrice = "NPR " + postpaidPlanDTO.getOfferprice();
                                    }
                                    Price price = new Price(packagePrice, packagePrice);
                                    Package subscriberPackage = new Package();
                                    subscriberPackage.setPackageName(postpaidPlanDTO.getDisplayname());
                                    subscriberPackage.setPrice(price);
                                    subscriberData.setPackageName(subscriberPackage);
                                    Device device = new Device();
                                    device.setDeviceId(deviceId);
                                    device.setPackageName(postpaidPlanDTO.getDisplayname());
                                    device.setMaster(master);
                                    device.setActiveFrom(customerPackageRelDTO.getStartdate() != null ? customerPackageRelDTO.getStartdate().toLocalDate().toString() : "");
                                    device.setExpiryOn(customerPackageRelDTO.getEnddate() != null ? customerPackageRelDTO.getEnddate().toLocalDate().toString() :"");
                                    List<Device> listDevices = new ArrayList<>();
                                    deviceDetailsResponse.setDeviceId(deviceId);
                                    deviceDetailsResponse.setPackageName(postpaidPlanDTO.getDisplayname());
                                    deviceDetailsResponse.setActiveFrom(customerPackageRelDTO.getStartdate() != null ? customerPackageRelDTO.getStartdate().toLocalDate().toString() : "");
                                    deviceDetailsResponse.setExpiryOn(customerPackageRelDTO.getEnddate() != null ? customerPackageRelDTO.getEnddate().toLocalDate().toString() : "");
                                    deviceDetailsResponse.setMaster(master);

                                    PaymentDetail paymentDetail=debitDocumentService.getLatestPayment(lisCustomerPackageRelDTOs,customerInventoryMappingDTO.getMappingId(),subscriberData.getSubscriberCode(),listCustomerServiceMappingDTOs.stream().map(x->x.getServiceid()).collect(Collectors.toList()));
                                    if(paymentDetail.getLatestPaymentAmount()!=null && paymentDetail.getLatestPaymentAmount()>0) {
                                        device.setLastPaid(paymentDetail.getLatestPaymentDate() != null ? paymentDetail.getLatestPaymentDate().toLocalDate().toString() : "");
                                        device.setLastPayment(paymentDetail.getLatestPaymentAmount());
                                        deviceDetailsResponse.setLastPaid(paymentDetail.getLatestPaymentDate() != null ? paymentDetail.getLatestPaymentDate().toLocalDate().toString() : "");
                                        deviceDetailsResponse.setLastPayment(paymentDetail.getLatestPaymentAmount());

                                    }
                                    else {
                                        device.setLastPaid("");
                                        device.setLastPayment(0.0);
                                        deviceDetailsResponse.setLastPaid("");
                                        deviceDetailsResponse.setLastPayment(0.0);
                                    }
                                    listDevices.add(device);
                                    if(device.getLastPayment() == null) {
                                        device.setLastPayment(0d);
                                        deviceDetailsResponse.setLastPayment(0d);
                                    }
                                    if(device.getLastPaid() == null) {
                                        device.setLastPaid("");
                                        deviceDetailsResponse.setLastPaid("");
                                    }
                                    addChildDevices(customersDTO.getCustid(), listDevices);
                                    subscriberData.setDevices(listDevices);
                                    subscriberData.setDeviceCount(listDevices.size());
                                    deviceDetailsResponse.setSubscriber(subscriberData);
                                    deviceDetailsResponse.setDevices(listDevices);
                                }
                            }
                        }
                    }

                }
            }
        } catch (Exception exception) {
            log.error("Exception occurred while getting deviceDetailResponse", exception);
        }

        return deviceDetailsResponse;
    }

    private void addChildDevices(Long parentCustId, List<Device> listDevices) {
        List<CustomersDTO> listChildCustomers = customersService.findByParentcustid(parentCustId.toString());
        if (CollectionUtils.isEmpty(listChildCustomers)) {
            return;
        }
        for (CustomersDTO childCustomersDTO : listChildCustomers) {
            List<CustomerInventoryMappingDTO> listCustomerInventoryMappingDTOs = customerInventoryMappingService
                    .findByCustomerIdAndItemIdNotNullAndIsDeleted(childCustomersDTO.getCustid(), NOT_DELETED);
            for (CustomerInventoryMappingDTO customerInventoryMappingDTO : listCustomerInventoryMappingDTOs) {
                List<SerializedItemDTO> lisSerializedItemDTOs = serializedItemService
                        .findByIdAndIsDeleted(customerInventoryMappingDTO.getItemId(), NOT_DELETED);
                for (SerializedItemDTO serializedItemDTO : lisSerializedItemDTOs) {

                    List<CustomerServiceMappingDTO> listCustomerServiceMappingDTOs = customerServiceMappingService
                            .findByConnectionNoAndIsDelete(customerInventoryMappingDTO.getConnectionNo(), NOT_DELETED);
                    if (!CollectionUtils.isEmpty(listCustomerServiceMappingDTOs)) {
                        CustomerServiceMappingDTO customerServiceMappingDTO = listCustomerServiceMappingDTOs.get(0);

                        List<CustomerPackageRelDTO> lisCustomerPackageRelDTOs = customerPackageRelService
                                .findByCustservicemappingid(customerServiceMappingDTO.getId());

                        if (!CollectionUtils.isEmpty(lisCustomerPackageRelDTOs)) {
                            CustomerPackageRelDTO customerPackageRelDTO = lisCustomerPackageRelDTOs.get(0);

                            List<PostpaidPlanDTO> listPostpaidPlanDTOs = postpaidPlanService
                                    .findByPostpaidplanidAndIsDeleted(customerPackageRelDTO.getPlanid(), NOT_DELETED);
                            if (!CollectionUtils.isEmpty(listPostpaidPlanDTOs)) {
                                PostpaidPlanDTO postpaidPlanDTO = listPostpaidPlanDTOs.get(0);
                                Device device = new Device();
                                device.setDeviceId(serializedItemDTO.getSerialNumber());
                                device.setPackageName(postpaidPlanDTO.getDisplayname());
                                device.setMaster(Boolean.FALSE);
                                device.setActiveFrom(customerPackageRelDTO.getStartdate() != null ? customerPackageRelDTO.getStartdate().toLocalDate().toString() : "");
                                device.setExpiryOn(customerPackageRelDTO.getEnddate() != null ? customerPackageRelDTO.getEnddate().toLocalDate().toString() :"");

                                PaymentDetail paymentDetail = debitDocumentService.getLatestPayment(lisCustomerPackageRelDTOs, customerInventoryMappingDTO.getMappingId(), childCustomersDTO.getCustid().toString(), listCustomerServiceMappingDTOs.stream().map(CustomerServiceMappingDTO::getServiceid).collect(Collectors.toList()));
                                if(paymentDetail.getLatestPaymentAmount()!=null && paymentDetail.getLatestPaymentAmount()>0) {
                                    device.setLastPaid(paymentDetail.getLatestPaymentDate() != null ? paymentDetail.getLatestPaymentDate().toLocalDate().toString() : "");
                                    device.setLastPayment(paymentDetail.getLatestPaymentAmount());

                                }
                                else {
                                    device.setLastPaid("");
                                    device.setLastPayment(0.0);
                                }
                                listDevices.add(device);
                            }
                        }
                    }
                }
            }
        }
    }

    private List<Payment> getTransactionDetail(String from, String to) {
        List<Payment> listPayments = new ArrayList<>();

        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date fromDt = formatter.parse(from);
            Date toDt = formatter.parse(to);
            LocalDateTime fromDate = fromDt.toInstant().atZone(ZoneId.of("UTC")).toLocalDate().atTime(0,0);
            LocalDateTime toDate = toDt.toInstant().atZone(ZoneId.of("UTC")).toLocalDate().atTime(23,59);
            fromDate=fromDate.plusDays(1);
            toDate=toDate.plusDays(1);
            fromDate = fromDate.plusHours(5).plusMinutes(45);
            toDate = toDate.plusHours(5).plusMinutes(45);

            List<CreditDocDTO> listCreditDocDTOs = creditDocService.findByPaymentdateBetween(fromDate.toString(), toDate.toString());
            for (CreditDocDTO creditDocDTO : listCreditDocDTOs) {
                Long customerId = creditDocDTO.getCustomer();
                List<CreditDebitMappingDTO> listCreditDebitMappingDTOs = creditDebitMappingService
                        .findByCreditdocidAndIsDeleted(creditDocDTO.getId(), NOT_DELETED);
                for (CreditDebitMappingDTO creditDebitMappingDTO : listCreditDebitMappingDTOs) {
                    List<DebitDocDTO> listDebitDocumentDTOs = debitDocumentService
                            .findByDebitdocumentidAndIsDelete(creditDebitMappingDTO.getDebitdocumentid(), NOT_DELETED);
                    for (DebitDocDTO debitDocumentDTO : listDebitDocumentDTOs) {
                        List<CustomerPackageRelDTO> listCustomerPackageRelDTOs = customerPackageRelService
                                .findByDebitdocid(debitDocumentDTO.getDebitdocumentid());
                        for (CustomerPackageRelDTO customerPackageRelDTO : listCustomerPackageRelDTOs) {
                            List<PostpaidPlanDTO> listPostpaidPlanDTOs = postpaidPlanService
                                    .findByPostpaidplanidAndIsDeleted(customerPackageRelDTO.getPlanid(), NOT_DELETED);
                            for (PostpaidPlanDTO postpaidPlanDTO : listPostpaidPlanDTOs) {
                                List<CustomerInventoryMappingDTO> listCustomerInventoryMappingDTOs = customerInventoryMappingService
                                        .findByCustomerIdAndIsDeleted(customerId, NOT_DELETED);
                                for (CustomerInventoryMappingDTO customerInventoryMappingDTO : listCustomerInventoryMappingDTOs) {
                                    List<SerializedItemDTO> listSerializedItemDTOs = serializedItemService
                                            .findByIdAndIsDeleted(customerInventoryMappingDTO.getItemId(), NOT_DELETED);
                                    for (SerializedItemDTO serializedItemDTO : listSerializedItemDTOs) {
                                        CustomersDTO customersDTO = customersService.findByCustid(customerId).get(0);
                                        String type = "child";
                                        if (customersDTO.getParentcustid() == null) {
                                            type = "master";
                                        }

                                        NextExpire nextExpire = new NextExpire(postpaidPlanDTO.getEnddate() != null
                                                ? convertToUTC(customerPackageRelDTO.getEnddate()).toString().concat("Z")
                                                : "", "UTC", 3);
                                        PaymentOn paymentOn = new PaymentOn(creditDocDTO.getCreatedate() != null
                                                ? convertToUTC(creditDocDTO.getCreatedate()).toString().concat("Z")
                                                : "", "UTC", 3);
                                        String amount = "NPR 0.0";
                                        if (creditDocDTO.getAmount() != null) {
                                            amount = "NPR." + creditDocDTO.getAmount().intValue();
                                        }
                                        Payment payment = new Payment(serializedItemDTO.getSerialNumber(),paymentOn,amount,
                                                type,postpaidPlanDTO.getDisplayname(),creditDocDTO.getCreditdocumentno(),nextExpire,
                                                customersDTO.getCustid().toString());
                                        listPayments.add(payment);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception exception) {
            log.error("Exception occuurred while getting Transaction Details", exception);
        }

        return listPayments;
    }

    private LocalDateTime convertToUTC(LocalDateTime localDateTime) {
//        ZonedDateTime zoneLocalTime = localDateTime.atZone(ZoneId.systemDefault());
//
//        ZonedDateTime zoneUtcTime = ZonedDateTime.ofInstant(zoneLocalTime.toInstant(), ZoneId.of("UTC"));
//        return zoneUtcTime.toLocalDateTime();
        localDateTime = localDateTime.minusHours(5).minusMinutes(45);
        return localDateTime;
    }
}
