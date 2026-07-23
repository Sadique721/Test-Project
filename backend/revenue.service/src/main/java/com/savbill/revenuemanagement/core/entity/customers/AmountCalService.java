package com.savbill.revenuemanagement.core.entity.customers;

import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.dto.customer.CustomerChangePlanDueAmountDTO;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.repository.customer.*;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.security.spring.SpringContext;
import com.savbill.revenuemanagement.core.service.AbstractService;
import com.savbill.revenuemanagement.core.service.prepaid.CustomerLedgerAllInfoPojo;
import com.savbill.revenuemanagement.core.service.prepaid.CustomerLedgerDtlsService;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostpaidPlan;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostpaidPlanRepo;
import com.savbill.revenuemanagement.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class AmountCalService extends AbstractService<Customers, CustomersPojo, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(AmountCalService.class);

    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    CustPlanMapppingRepository custPlanMapppingRepository;

    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    DebitDocRepository debitDocRepository;

    @Autowired
    private CustomerChargeDBRRepository customerChargeDBRRepository;

    @Override
    protected JpaRepository<Customers, Integer> getRepository() {
        return customersRepository;
    }

    /**
     * Method to calculate amount to be paid for Change Plan or renew
     * @param customerChangePlanDueAmountDTO
     * @return
     */
    public Integer viewAmountForChangePlanOrRenew(CustomerChangePlanDueAmountDTO customerChangePlanDueAmountDTO){
        Double dueAmount = 0.0;
        Double walletAmount = 0.0;
        Double newPlanPrice=0.0;
        Double refundAmount=0.0;


        Optional<CustPlanMappping> custPlanMappping = custPlanMapppingRepository.findById(customerChangePlanDueAmountDTO.getCustPackRelId());
        if(customerChangePlanDueAmountDTO.getCustPackRelId() != null && customerChangePlanDueAmountDTO.getNewPlanId() != null && customerChangePlanDueAmountDTO.getOldPlanId() != null && customerChangePlanDueAmountDTO.getOldPlanGroupId() == null) {
            Optional<PostpaidPlan> postpaidPlan = postpaidPlanRepo.findProjectedById(custPlanMappping.get().getPlanId());
            Optional<PostpaidPlan> newPostpaidPlan = postpaidPlanRepo.findProjectedById(customerChangePlanDueAmountDTO.getNewPlanId());
            if(newPostpaidPlan.isPresent()){
            newPlanPrice = newPostpaidPlan.get().getOfferprice();
            }else{
                logger.error("Error fetching new Plan for Plan ID: "+customerChangePlanDueAmountDTO.getNewPlanId());
            }

            //Logic for calculating Wallet balance: reference /api/v1/Revenue/wallet
            try {
                CustomerLedgerDtlsService customerLedgerDtlsService = SpringContext.getBean(CustomerLedgerDtlsService.class);
                CustomerLedgerDtlsPojo pojo = new CustomerLedgerDtlsPojo();
                pojo.setCustId(customerChangePlanDueAmountDTO.getCustId());
                pojo.setCREATE_DATE(null);
                pojo.setEND_DATE(null);

                CustomerLedgerInfoPojo infoPojo = customerLedgerDtlsService.getByTime(pojo);
                CustomerLedgerAllInfoPojo ledgerAllInfoPojo =
                        customerLedgerDtlsService.custInfoBytime(customerChangePlanDueAmountDTO.getCustId(), infoPojo);
                if (ledgerAllInfoPojo != null && ledgerAllInfoPojo.getCustomerLedgerInfoPojo() != null) {
                    walletAmount = -ledgerAllInfoPojo.getCustomerLedgerInfoPojo().getClosingBalance();
                }
            } catch (Exception ex) {
                logger.error("Error fetching wallet for customer ID: " + customerChangePlanDueAmountDTO.getCustId(), ex);
                walletAmount = 0.0;
            }

            //Logic for calculating dueAmount for Renew
            if(Constants.PURCHASE_TYPE.RENEW.equalsIgnoreCase(customerChangePlanDueAmountDTO.getPurchaseType())){
                dueAmount = newPlanPrice - walletAmount;
            }else{

//                @Autowired
//                CustPlanMapppingRepository custPlanMapppingRepository;
                //Calculating newPlanPrice for prorated invoice
                if(CommonUtils.CHANGEPLANBILLINGCYCLECONSTANT.Existing_Billing_Cycle.equalsIgnoreCase(customerChangePlanDueAmountDTO.getChangePlanBillingCycle())) {
                    Integer totalNewPlanDays = newPostpaidPlan.get().getValidity().intValue();
                    String validityUnit = newPostpaidPlan.get().getUnitsOfValidity();
                    if (validityUnit != null) {
                        switch (validityUnit.trim().toLowerCase()) {
                            case "hours":
                                totalNewPlanDays = (int) Math.ceil(totalNewPlanDays / 24.0);
                                break;
                            case "months":
                                totalNewPlanDays = (int) ChronoUnit.DAYS.between(LocalDate.now(),LocalDate.now().plusMonths(totalNewPlanDays));
                                break;
                            case "years":
                                totalNewPlanDays = (int) ChronoUnit.DAYS.between(LocalDate.now(),LocalDate.now().plusYears(totalNewPlanDays));
                                break;
                            case "days":
                            default:
                                break;
                        }
                    }

                    // Remaining days based on old plan’s expiry
                    long remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now(), custPlanMappping.get().getExpiryDate());
                    if (remainingDays < 0) remainingDays = 0;

                    // Calculate prorated price
                    double newPlanDailyPrice = newPlanPrice / totalNewPlanDays;
                    newPlanPrice = newPlanDailyPrice * remainingDays;
                }

                //Calculate refund amount(credit note)
                refundAmount = Optional.ofNullable(custPlanMappping.get().getDebitdocid())
                        .map(id -> previewCreditNoteAmount(id.intValue()))
                        .orElse(0.0);

                dueAmount=newPlanPrice-(refundAmount+walletAmount);
            }
        }

        if (dueAmount < 0.0) {
            dueAmount = 0.0;
        }

        return dueAmount.intValue();
    }

    public Double previewCreditNoteAmount(Integer debitDocId) {
        Optional<DebitDocument> optionalDebitDocument = debitDocRepository.findById(debitDocId);
        if (optionalDebitDocument.isPresent()) {
            DebitDocument debitDocument = optionalDebitDocument.get();
            try {
                double remainingAmount = debitDocument.getTotalamount();
                DecimalFormat df = new DecimalFormat("#.00");
                List<CustomerChargeDBR> dbrList = customerChargeDBRRepository
                        .findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqual(
                                debitDocument.getId().longValue(),
                                LocalDate.now(),
                                debitDocument.getEndate().toLocalDate());

                double cnAmount = 0d;
                if (!CollectionUtils.isEmpty(dbrList)) {
                    cnAmount = dbrList.stream()
                            .mapToDouble(x -> x.getDbr())
                            .sum();
                }

                if (cnAmount == 0) {
                    return 0.0;
                }

                double invoiceWithoutTax = debitDocument.getTotalamount() - debitDocument.getTax() + debitDocument.getDiscount();
                double newDiscount = 0;
                if (invoiceWithoutTax > 0) {
                    newDiscount = cnAmount * (debitDocument.getDiscount() / invoiceWithoutTax);
                }

                double percentage = (debitDocument.getTax() * 100.0d) / (debitDocument.getTotalamount() - debitDocument.getTax());
                double prorateTaxAmount = ((cnAmount - newDiscount) * percentage) / 100.0d;
                cnAmount = cnAmount - newDiscount + prorateTaxAmount;

                if (remainingAmount - cnAmount < 0.1 && remainingAmount != 0) {
                    cnAmount = remainingAmount;
                }

                cnAmount = Double.parseDouble(df.format(cnAmount));
                return cnAmount;

            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error("Error while previewing CN for invoice: " + debitDocument.getDocnumber() + " exception: " + ex.getMessage());
                return null;
            }
        }
        return null;
    }
}
