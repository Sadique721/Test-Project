package com.savbill.cpm.modules.CronJobs;

import com.savbill.cpm.schedulerAudit.SchedulerAudit;
import com.savbill.cpm.schedulerAudit.SchedulerAuditService;
import com.savbill.cpm.service.SchedulerLockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.savbill.cpm.constants.ClientServiceConstant;
import com.savbill.cpm.core.repository.CustomRepository;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.model.postpaid.CustomerLedger;
import com.savbill.cpm.model.postpaid.CustomerLedgerDtls;
import com.savbill.cpm.modules.placeOrder.model.OrderDTO;
import com.savbill.cpm.modules.placeOrder.service.OrderService;
import com.savbill.cpm.modules.purchaseDetails.model.CustomPurchase;
import com.savbill.cpm.modules.purchaseDetails.repository.PurchaseDetailsRepo;
import com.savbill.cpm.modules.subscriber.queryScript.PlanQueryScript;
import com.savbill.cpm.service.common.ClientServiceSrv;
import com.savbill.cpm.service.common.CustomersService;
import com.savbill.cpm.service.postpaid.CustomerLedgerDtlsService;
import com.savbill.cpm.service.postpaid.CustomerLedgerService;
import com.savbill.cpm.utils.CommonConstants;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class RefundSettlementJob {

    @Autowired
    private PurchaseDetailsRepo purchaseDetailsRepo;

    @Autowired
    private CustomRepository<CustomPurchase> customRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerLedgerDtlsService customerLedgerDtlsService;

    @Autowired
    private CustomerLedgerService customerLedgerService;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private SchedulerLockService schedulerLockService;

    @Autowired
    private SchedulerAuditService schedulerAuditService;

    private String cronRefundPermission = "No";


    @Scheduled(cron = "${cronjobtimeforrefundrelease}}")
    public void cronJob() {
        log.info("XXXXXXXXXXXX----------REFUND RELEASE Scheduler START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.REFUND_RELEASE_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.REFUND_RELEASE)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.REFUND_RELEASE);
            try {
                cronRefundPermission = clientServiceSrv.getClientSrvByName(ClientServiceConstant.CRON_PERMISSION_FOR_REFUND).get(0).getValue();
                if (cronRefundPermission.equalsIgnoreCase("yes")) {
                    ApplicationLogger.logger.info("CRON PERMISSION FOR REFUND SETTLE IS STARTED");

                    List<CustomPurchase> purchaseList = customRepository.getResultOfQuery(PlanQueryScript.getRefundableOrders(), CustomPurchase.class);
                    if (purchaseList.size() > 0) {
                        purchaseList.forEach(data -> {
                            try {
                                OrderDTO orderDTO = orderService.getEntityById(data.getOrderid());
                                Customers customers = customersService.get(data.getCustid());
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
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        });
                        schedulerAudit.setEndTime(LocalDateTime.now());
                        schedulerAudit.setDescription("CRON PERMISSION FOR REFUND SETTLE IS TRUE");
                        schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                        schedulerAudit.setTotalCount(purchaseList.size());
                    }
                } else {
                    ApplicationLogger.logger.info("CRON PERMISSION FOR REFUND SETTLE IS FALSE");
                    schedulerAudit.setEndTime(LocalDateTime.now());
                    schedulerAudit.setDescription("CRON PERMISSION FOR REFUND SETTLE IS FALSE");
                    schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                    schedulerAudit.setTotalCount(null);
                }
                ApplicationLogger.logger.info("CRON JOB FOR Refund settle is ENDED : " + LocalDateTime.now());
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ex.getMessage());
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                log.error(ex.toString(), ex);
                log.error("**********Scheduler Showing ERROR***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.REFUND_RELEASE);
                log.info("XXXXXXXXXXXX---------- REFUND RELEASE Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("REFUND RELEASE Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------REFUND RELEASE Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }
}
