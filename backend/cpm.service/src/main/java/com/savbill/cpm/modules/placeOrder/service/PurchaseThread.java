package com.savbill.cpm.modules.placeOrder.service;

import lombok.SneakyThrows;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.savbill.cpm.constants.PGConstants;
import com.savbill.cpm.constants.SubscriberConstants;
import com.savbill.cpm.exception.PGException;
import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.model.postpaid.CustomerLedger;
import com.savbill.cpm.model.postpaid.CustomerLedgerDtls;
import com.savbill.cpm.modules.PartnerLedger.model.PartnerLedgerBalanceDTO;
import com.savbill.cpm.modules.PartnerLedger.service.PartnerLedgerDetailsService;
import com.savbill.cpm.modules.PartnerLedger.service.PartnerLedgerService;
import com.savbill.cpm.modules.PartnerLedger.service.PartnerPaymentService;
import com.savbill.cpm.modules.auditLog.service.AuditLogService;
import com.savbill.cpm.modules.placeOrder.model.OrderDTO;
import com.savbill.cpm.modules.purchaseDetails.model.PurchaseDetailsDTO;
import com.savbill.cpm.modules.purchaseDetails.service.PurchaseDetailsService;
import com.savbill.cpm.modules.subscriber.model.ChangePlanRequestDTO;
import com.savbill.cpm.modules.subscriber.model.CustomChangePlanDTO;
import com.savbill.cpm.modules.subscriber.service.InvoiceThread;
import com.savbill.cpm.modules.subscriber.service.SubscriberService;
import com.savbill.cpm.service.common.CustomersService;
import com.savbill.cpm.service.postpaid.BillRunService;
import com.savbill.cpm.service.postpaid.CustomerLedgerDtlsService;
import com.savbill.cpm.service.postpaid.CustomerLedgerService;
import com.savbill.cpm.spring.SpringContext;
import com.savbill.cpm.utils.CommonConstants;
import com.savbill.cpm.utils.CommonUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PurchaseThread implements Runnable {
    private PurchaseDetailsDTO purchaseDetailsDTO;
    private PurchaseDetailsService purchaseDetailsService;
    private OrderService orderService;
    private SubscriberService subscriberService;
    private CustomersService customersService;
    private PartnerLedgerDetailsService partnerLedgerDetailsService;
    private PartnerLedgerService partnerLedgerService;
    private PartnerPaymentService partnerPaymentService;
    private CustomerLedgerService customerLedgerService;
    private CustomerLedgerDtlsService customerLedgerDtlsService;
    private BillRunService billRunService;
    private HttpServletRequest request;
    private SecurityContext securityContext;


    public PurchaseThread(PurchaseDetailsDTO dto, PurchaseDetailsService service, SubscriberService subscriberService, CustomersService customersService, OrderService orderService, PartnerLedgerDetailsService partnerLedgerDetailsService, PartnerLedgerService partnerLedgerService, PartnerPaymentService partnerPaymentService, CustomerLedgerService customerLedgerService, CustomerLedgerDtlsService customerLedgerDtlsService, HttpServletRequest request) {
        this.purchaseDetailsDTO = dto;
        this.purchaseDetailsService = service;
        this.customersService = customersService;
        this.subscriberService = subscriberService;
        this.orderService = orderService;
        this.partnerLedgerDetailsService = partnerLedgerDetailsService;
        this.partnerLedgerService = partnerLedgerService;
        this.partnerPaymentService = partnerPaymentService;
        this.customerLedgerService = customerLedgerService;
        this.customerLedgerDtlsService = customerLedgerDtlsService;
        this.request = request;
        this.securityContext = SecurityContextHolder.getContext();
    }

    @SneakyThrows
    @Override
    public void run() {
        this.billRunService = SpringContext.getBean(BillRunService.class);
        purchaseProcess(purchaseDetailsDTO);
    }

    @Transactional
    public void purchaseProcess(PurchaseDetailsDTO dto) throws Exception {
        AuditLogService auditLogService = SpringContext.getBean(AuditLogService.class);

        purchaseDetailsDTO = dto;
        purchaseDetailsDTO = this.purchaseDetailsService.getPurchaseBYTxnId(purchaseDetailsDTO.getTransid());
        OrderDTO orderDTO = orderService.getEntityById(purchaseDetailsDTO.getOrderid());
        if (purchaseDetailsDTO.getPaymentstatus().equalsIgnoreCase(CommonUtils.getPaymentStatus().get(PGConstants.FAILED_STATUS))) {
            purchaseDetailsDTO.setPurchaseStatus(PGConstants.FAILED_STATUS);
            purchaseDetailsService.saveEntity(purchaseDetailsDTO);

//            auditLogService.addAuditForPlaceOrder(AclConstants.ACL_CLASS_ORDER_PLACE,
//                    request.getRemoteAddr(), null, orderDTO.getId());
//            auditLogService.addAuditForPlaceOrder(AclConstants.ACL_CLASS_ORDER_PAYMENT,
//                    request.getRemoteAddr(), null, purchaseDetailsDTO.getId());
//            auditLogService.addAuditForPlaceOrder(AclConstants.ACL_CLASS_ORDER_PURCHASE,
//                    request.getRemoteAddr(), null, purchaseDetailsDTO.getId());

            //Settle ledger if balance used
            if (orderDTO.getIs_balance_used() && orderDTO.getBalanced_used() > 0 && orderDTO.getLedger_details_id() != null) {
                Double settleAmount = this.customerLedgerDtlsService.get(orderDTO.getLedger_details_id().intValue()).getAmount();
                Customers customers = customersService.get(purchaseDetailsDTO.getCustid());
                if (settleAmount != null && settleAmount > 0) {
                    CustomerLedgerDtls ledgerDtls = new CustomerLedgerDtls();
                    ledgerDtls.setCustomer(customers);
                    ledgerDtls.setDebitdocid(null);
                    ledgerDtls.setAmount(settleAmount);
                    ledgerDtls.setDescription("Settle balance against used amount in plan purchase");
                    ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
                    ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_WALLET_ADJUST);
                    ledgerDtls = customerLedgerDtlsService.save(ledgerDtls);

                    CustomerLedger customerLedger = this.customerLedgerService.getCustomerLeger(customers).get(0);
                    customerLedger.setTotalpaid(customerLedger.getTotalpaid() + settleAmount);
                    customerLedger.setCustomer(customers);
                    customerLedger.setUpdatedate(LocalDateTime.now());
                    customerLedger = customerLedgerService.save(customerLedger);

                    orderDTO.setIs_settled(true);
                    orderDTO = orderService.saveEntity(orderDTO);
                }
            }

            throw new PGException("Payment is failed due to cancellation");
        } else if (purchaseDetailsDTO.getPaymentstatus().equalsIgnoreCase(CommonUtils.getPaymentStatus().get(PGConstants.PENDING_STATUS))) {
            purchaseDetailsDTO.setPurchaseStatus(PGConstants.PENDING_STATUS);
            purchaseDetailsService.saveEntity(purchaseDetailsDTO);

//            auditLogService.addAuditForPlaceOrder(AclConstants.ACL_CLASS_ORDER_PLACE,
//                    request.getRemoteAddr(), null, orderDTO.getId());
//            auditLogService.addAuditForPlaceOrder(AclConstants.ACL_CLASS_ORDER_PAYMENT,
//                    request.getRemoteAddr(), null, purchaseDetailsDTO.getId());
//            auditLogService.addAuditForPlaceOrder(AclConstants.ACL_CLASS_ORDER_PURCHASE,
//                    request.getRemoteAddr(), null, purchaseDetailsDTO.getId());
            throw new PGException("Payment is pending");
        }
        try {
            if (purchaseDetailsDTO.getCustid() != null) {
                if (orderDTO != null) {
                    if (orderDTO.getEntityid() != null) {
                        if (orderDTO.getOrdertype().equalsIgnoreCase(PGConstants.ORDER_TYPE_PLAN)) {
                            ChangePlanRequestDTO changePlanRequestDTO = new ChangePlanRequestDTO(orderDTO.getCustId().intValue()
                                    , orderDTO.getEntityid().intValue(), orderDTO.getPurchase_type(), true, false, "Online purchase",
                                    orderDTO.getBalanced_used(), purchaseDetailsDTO.getId(), SubscriberConstants.PURCHASE_FROM_CUSTPORTAL, SubscriberConstants.PURCHASE_TYPE_RENEW,null,null);
                            Customers customers = customersService.get(orderDTO.getCustId().intValue());

                            //Payment record
                            /*RecordPaymentRequestDTO recordPaymentDto = new RecordPaymentRequestDTO(CommonUtils.PAYMENT_MODE_ONLINE
                                    , LocalDateTime.now().toLocalDate(), purchaseDetailsDTO.getAmount(), false, "", customers.getId());
                            RecordpaymentResponseDTO recordpaymentResponseDTO = subscriberService.recordPayment(recordPaymentDto, customers);

                            //Invoke Receipt therad
                            if (null != customers) {
                                List<CreditDocument> creditDocumentList = recordpaymentResponseDTO.getCreditDocument();
                                Runnable receiptRunnable = new ReceiptThread(billRunService, creditDocumentList);
                                Thread receiptThread = new Thread(receiptRunnable);
                                receiptThread.start();
                            }*/

                            //Settle ledger if balance used
                            if (orderDTO.getIs_balance_used() && orderDTO.getBalanced_used() > 0 && orderDTO.getLedger_details_id() != null) {
                                Double settleAmount = this.customerLedgerDtlsService.get(orderDTO.getLedger_details_id().intValue()).getAmount();
                                if (settleAmount != null && settleAmount > 0) {
                                    CustomerLedgerDtls ledgerDtls = new CustomerLedgerDtls();
                                    ledgerDtls.setCustomer(customers);
                                    ledgerDtls.setDebitdocid(null);
                                    ledgerDtls.setAmount(settleAmount);
                                    ledgerDtls.setDescription("Settle balance against used amount in plan purchase");
                                    ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
                                    ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_WALLET_ADJUST);
                                    ledgerDtls = customerLedgerDtlsService.save(ledgerDtls);

                                    CustomerLedger customerLedger = this.customerLedgerService.getCustomerLeger(customers).get(0);
                                    customerLedger.setTotalpaid(customerLedger.getTotalpaid() + settleAmount);
                                    customerLedger.setCustomer(customers);
                                    customerLedger.setUpdatedate(LocalDateTime.now());
                                    customerLedger = customerLedgerService.save(customerLedger);

                                    orderDTO.setIs_settled(true);
                                    orderDTO = orderService.saveEntity(orderDTO);
                                }
                            }

                            changePlanRequestDTO.setIsPaymentReceived(false);
                            CustomChangePlanDTO customChangePlanDTO = subscriberService.changePlan(changePlanRequestDTO, customers, true, purchaseDetailsDTO.getAmount(),"", null);

                            // Invoke billing engine

                            try {
                                Customers customer = customersService.get(customers.getId());
                                customer.setBillRunCustPackageRelId(customChangePlanDTO.getCustpackagerelid());

                                Runnable invoiceRunnable = new InvoiceThread(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), customer, customersService,"",null,null);
                                Thread invoiceThread = new Thread(invoiceRunnable);
                                invoiceThread.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            purchaseDetailsDTO.setPurchaseStatus(PGConstants.SUCCESSFUL_STATUS);
                            purchaseDetailsService.saveEntity(purchaseDetailsDTO);
                        }
                    }
                }
            } else if (purchaseDetailsDTO.getPartnerid() != null) {
                if (orderDTO.getOrdertype().equalsIgnoreCase(PGConstants.ORDER_TYPE_PARTNER_ADD_BALANCE)) {
                    PartnerLedgerBalanceDTO balanceDTO = new PartnerLedgerBalanceDTO(orderDTO.getFinalamount(), orderDTO.getPartnerId().intValue()
                            , "Add balance", CommonUtils.PAYMENT_MODE_ONLINE, purchaseDetailsDTO.getTransid(), LocalDateTime.now().toLocalDate());
                    //add balance in ledgerdetails
                    partnerLedgerDetailsService.addBalance(balanceDTO);
                    //add balance in ledger
                    partnerLedgerService.addBalance(balanceDTO);
                    //add balance in partnerpayment
                    partnerPaymentService.addBalance(balanceDTO);

                    purchaseDetailsDTO.setPurchaseStatus(PGConstants.SUCCESSFUL_STATUS);
                    purchaseDetailsService.saveEntity(purchaseDetailsDTO);
                }
            }
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
    }
}
