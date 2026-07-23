package com.savbill.revenuemanagement.core.service.common;

import com.savbill.revenuemanagement.core.constants.ClientServiceConstant;
import com.savbill.revenuemanagement.core.entity.customers.*;
import com.savbill.revenuemanagement.core.entity.debitdoc.*;
import com.savbill.revenuemanagement.core.entity.customers.*;
import com.savbill.revenuemanagement.core.entity.debitdoc.*;
import com.savbill.revenuemanagement.core.repository.customer.CustChargeDetailsRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomerServiceMapRepository;
import com.savbill.revenuemanagement.core.service.ClientServ.repository.ClientServiceRepository;
import com.savbill.revenuemanagement.kafka.KafkaMessageData;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import com.savbill.revenuemanagement.productmanagement.Charge.domain.Charge;
import com.savbill.revenuemanagement.productmanagement.Charge.repocitory.ChargeRepository;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostpaidPlan;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostpaidPlanRepo;
import com.savbill.revenuemanagement.productmanagement.PlanService.domain.Services;
import com.savbill.revenuemanagement.productmanagement.PlanService.repository.ServiceRepository;
import com.savbill.revenuemanagement.productmanagement.Tax.service.TaxService;
import com.savbill.revenuemanagement.rabbitmq.messages.CustChargeDetailsMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.CustomerBillingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class ChargeInvoiceUtil {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceUtil.class);

    @Value("${project.currency: Rs.}")
    private String curr;

    @Value("${project.currency.cent: Rs.}")
    private String centCurr;

    @Autowired
    private ChargeRepository chargeRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private InvoiceUtil invoiceUtil;

    @Autowired
    private CustPlanMapppingRepository custPlanMapppingRepository;

    @Autowired
    TaxService taxService;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private CustomerServiceMapRepository customerServiceMappingRepository;

    @Autowired
    private CustChargeDetailsRepository custChargeDetailsRepository;

    @Autowired
    private ClientServiceRepository clientServiceRepository;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    /**
    Create DebitDoc related data
     * @Author yogesh
     * @param customers
     * @param custChargeDetailsList
     * @return InvoiceDetails
     */
    public InvoiceDetails prepareInvoiceChargeDetail(Customers customers, List<CustChargeDetails> custChargeDetailsList, CustomerBillingMessage message) {
        try {
            DebitDocument debitDocument = new DebitDocument();
            List<DebitDocDetails> debitDocDetailsList = new ArrayList<>();
            Integer cprId = 0;
            for(CustChargeDetails custChargeDetails: custChargeDetailsList) {
                CustPlanMappping custPlanMappping = null;
                if(custChargeDetails.getCustPlanMapppingId() != null) {
                    custPlanMappping = custPlanMapppingRepository.findById(custChargeDetails.getCustPlanMapppingId()).get();
                    cprId = custPlanMappping.getId();
                }
                CustChargeDetails newCustChargeDetails = custChargeDetails;
                DebitDocDetails debitDocDetails = setDebitDocDetailsForCharge(newCustChargeDetails, custPlanMappping);
                debitDocDetailsList.add(debitDocDetails);
            }
            debitDocument = invoiceUtil.setDebitDocBasicDetails(debitDocument, debitDocDetailsList, customers, cprId);
            return new InvoiceDetails(debitDocument, debitDocDetailsList, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception during create customer charge invoice: "+ex.getMessage());
        }
        return null;
    }

    /**
     Set Basic details for DebitDocDetails
     * @Author Yogesh
     * @param custChargeDetails
     * @param planMappping
     * @return DebitDocDetails
     */
    public DebitDocDetails setDebitDocDetailsForCharge(CustChargeDetails custChargeDetails, CustPlanMappping planMappping) {

        //Charge charge = chargeRepository.findById(custChargeDetails.getChargeid()).get();
        List<Integer> chargeIds = Collections.singletonList(custChargeDetails.getChargeid());
        Charge charge = chargeRepository.findByChargeIds(chargeIds).get(0);
        DebitDocDetails debitDocDetails = new DebitDocDetails();
        debitDocDetails.setChargecycle(String.valueOf(custChargeDetails.getBillingCycle()));
        debitDocDetails.setChargename(charge.getName());
        debitDocDetails.setChargeid(charge.getId());
        debitDocDetails.setChargetype(custChargeDetails.getChargetype());
        if(planMappping != null && planMappping.getCustServiceMappingId() != null) {
            debitDocDetails.setCustServiceId(Long.valueOf(planMappping.getCustServiceMappingId()));
            debitDocDetails.setIcCode(String.valueOf(planMappping.getCustServiceMappingId()));
        }
        debitDocDetails.setNoofcycle(-1);//TODO: Need to confirm
        debitDocDetails.setDescription(charge.getDesc());
        debitDocDetails.setEnddate(custChargeDetails.getEnddate());
        debitDocDetails.setProrationtype("F");
        debitDocDetails.setStartdate(custChargeDetails.getStartdate());
        if(planMappping != null && planMappping.getPlanId() != null) {
            Optional<PostpaidPlan> postpaidPlan = postpaidPlanRepo.findById(planMappping.getPlanId());
            if(postpaidPlan.isPresent()) {
                Optional<Services> services = serviceRepository.findById(Long.valueOf(postpaidPlan.get().getServiceId()));//findServicesByServiceName(planMappping.getService());
                services.ifPresent(value -> debitDocDetails.setServiceId(value.getId()));
            }
            debitDocDetails.setPlanId(String.valueOf(planMappping.getPlanId()));
        }
        if(planMappping !=null && planMappping.getDiscount() != null)
            debitDocDetails.setDiscountPercentage(planMappping.getDiscount());
        else
            debitDocDetails.setDiscountPercentage(0d);

        Optional<CustomerServiceMapping> customerServiceMapping = null;
        if(planMappping!=null) {
             customerServiceMapping = customerServiceMappingRepository.findById(planMappping.getCustServiceMappingId());
        }
        if(customerServiceMapping !=null && customerServiceMapping.get().getDiscount()!=null)
            custChargeDetails.setDiscount(customerServiceMapping.get().getDiscount());
        else
            custChargeDetails.setDiscount(0d);


        //amount calculation for installment based customer direct charge
        if(custChargeDetails.getInstallmentEnabled() != null && custChargeDetails.getInstallmentEnabled().equals(Boolean.TRUE)){
            Double installmentAmountPrice = custChargeDetails.getAmountPerInstallment().doubleValue();
            logger.info("Installment is enabled for custId: " + custChargeDetails.getCustomer().getId() +
                    ", amount per installment: " + installmentAmountPrice +
                    ", total installments: " + custChargeDetails.getTotalInstallments());

            custChargeDetails.setPrice(installmentAmountPrice);

            Integer currentInstallmentNo = custChargeDetails.getInstallmentNo();
            Integer totalInstallments = custChargeDetails.getTotalInstallments();

            if (currentInstallmentNo != null && currentInstallmentNo < totalInstallments) {

                LocalDate currentNextInstallmentDate = null;
                if(custChargeDetails.getInstallmentNo() != null && custChargeDetails.getInstallmentNo() == 0){
                    currentNextInstallmentDate = custChargeDetails.getInstallmentStartDate();
                } else {
                    currentNextInstallmentDate = custChargeDetails.getNextInstallmentDate();
                }
                String frequency = custChargeDetails.getInstallmentFrequency();

                LocalDate newNextDate = calculateNextInstallmentDate(currentNextInstallmentDate, frequency);

                Integer nextInstallmentNo = currentInstallmentNo + 1;
                custChargeDetails.setInstallmentNo(nextInstallmentNo);
                custChargeDetails.setLastInstallmentDate(currentNextInstallmentDate);
                custChargeDetails.setNextInstallmentDate(newNextDate);
                if(nextInstallmentNo == custChargeDetails.getTotalInstallments()){
                    custChargeDetails.setNextInstallmentDate(null);
                }
                custChargeDetailsRepository.updateInstallmentDatesAndNo(
                        newNextDate,
                        currentNextInstallmentDate,
                        nextInstallmentNo,
                        custChargeDetails.getId()
                );

                CustChargeDetailsMessage custChargeDetailsMessage = new CustChargeDetailsMessage(custChargeDetails.getId(), custChargeDetails.getNextInstallmentDate(), currentNextInstallmentDate, nextInstallmentNo);
                kafkaMessageSender.send(new KafkaMessageData(custChargeDetailsMessage,CustChargeDetailsMessage.class.getSimpleName()));
                logger.info("******************** kafka call send to CMS : " + custChargeDetailsMessage + " custid : " +custChargeDetails.getCustomer().getId());

                String installmentInterest = clientServiceRepository.findValueByNameAndMvnoId(ClientServiceConstant.INSTALLMENT_INTEREST , custChargeDetails.getCustomer().getMvnoId());

                if (installmentInterest != null) {
                    try {
                        BigDecimal interestRate = new BigDecimal(installmentInterest.replace("%", ""))
                                .divide(BigDecimal.valueOf(100));
                        double installmentAmount = installmentAmountPrice * interestRate.doubleValue();
                        debitDocDetails.setInstallmentCharge(interestRate.doubleValue());
                        debitDocDetails.setInstallmentInterest(installmentAmount);
                        logger.info("installmentCharge : " + installmentInterest + " custid : " +custChargeDetails.getCustomer().getId());
                    } catch (NumberFormatException e) {
                        logger.info("Invalid interest format: " + installmentInterest);
                    }
                } else {
                    logger.info("Installment interest is null");
                }
                debitDocDetails.setCurrentInstallmentNo(nextInstallmentNo);

            } else {
                logger.info("All installments completed for custId: " + custChargeDetails.getCustomer().getId() + " with cstchargeid :  " + custChargeDetails.getId());
                custChargeDetails.setInstallmentEnabled(false);
            }
        }

        //amount calculations
        debitDocDetails.setSubtotal(custChargeDetails.getPrice());
//        custChargeDetails.setTaxamount(0.0);
        taxService.calculateTierTax(custChargeDetails, custChargeDetails.getTaxId());
        debitDocDetails.setTax(custChargeDetails.getTaxamount());
        debitDocDetails.setDiscount(custChargeDetails.getDiscount());
        //double discountedAmount = debitDocDetails.getSubtotal() + debitDocDetails.getTax();
        //debitDocDetails.setDiscount(invoiceUtil.calculateDiscount(discountedAmount, debitDocDetails.getDiscountPercentage()));
        debitDocDetails.setTotalamount(debitDocDetails.getSubtotal() - debitDocDetails.getDiscount() + debitDocDetails.getTax() + (debitDocDetails.getInstallmentInterest() != null ? debitDocDetails.getInstallmentInterest() : 0));
        return debitDocDetails;
    }

    /**
     invoice details for direct charge in caf
     * @Author Vikas
     *
     */
    public TrialInvoiceDetails prepareInvoiceChargeDetailCaf(Customers customers, List<CustChargeDetails> custChargeDetailsList) {
        try {
            TrialDebitDocument debitDocument = new TrialDebitDocument();
            List<TrialDebitDocumentDetail> debitDocDetailsList = new ArrayList<>();
            Integer cprId = 0;
            for(CustChargeDetails custChargeDetails: custChargeDetailsList) {
                CustPlanMappping custPlanMappping = custPlanMapppingRepository.findById(custChargeDetails.getCustPlanMapppingId()).get();
                cprId = custPlanMappping.getId();
                TrialDebitDocumentDetail debitDocDetails = setDebitDocDetailsForChargeCaf(custChargeDetails, custPlanMappping);
                debitDocDetailsList.add(debitDocDetails);
            }
            debitDocument = invoiceUtil.setDebitDocBasicDetails(debitDocument, debitDocDetailsList, customers, cprId);
            return new TrialInvoiceDetails(debitDocument, debitDocDetailsList);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception during create customer charge invoice: "+ex.getMessage());
        }
        return null;
    }


    /**
     set DebitDocDetailsForChargeCaf got direct charge
     * @Author Vikas
     *
     */
    private TrialDebitDocumentDetail setDebitDocDetailsForChargeCaf(CustChargeDetails custChargeDetails, CustPlanMappping planMappping) {
        TrialDebitDocumentDetail debitDocDetails = new TrialDebitDocumentDetail();
        Charge charge = chargeRepository.findById(custChargeDetails.getChargeid()).get();
        debitDocDetails.setChargecycle(String.valueOf(custChargeDetails.getBillingCycle()));
        debitDocDetails.setChargename(charge.getName());
        debitDocDetails.setChargeid(charge.getId());
        debitDocDetails.setChargetype(custChargeDetails.getChargetype());
        debitDocDetails.setNoofcycle(-1);//TODO: Need to confirm
        debitDocDetails.setDescription(custChargeDetails.getRemarks());
        debitDocDetails.setEnddate(custChargeDetails.getEnddate());
        debitDocDetails.setProrationtype("F");
        debitDocDetails.setStartdate(custChargeDetails.getStartdate());

        if(custChargeDetails.getDiscount() != null)
            debitDocDetails.setDiscount(custChargeDetails.getDiscount());
        else
            debitDocDetails.setDiscount(0d);

        //amount calculations
        debitDocDetails.setSubtotal(custChargeDetails.getPrice());
        taxService.calculateTierTax(custChargeDetails, custChargeDetails.getTaxId());
        debitDocDetails.setTax(custChargeDetails.getTaxamount());
        debitDocDetails.setDiscount(custChargeDetails.getDiscount());
        debitDocDetails.setTotalamount(debitDocDetails.getSubtotal() - debitDocDetails.getDiscount() + debitDocDetails.getTax());

        return debitDocDetails;
    }

    private LocalDate calculateNextInstallmentDate(LocalDate current, String frequency) {
        if (current == null || frequency == null) return null;

        switch (frequency.toUpperCase()) {
            case "MONTHLY":
                return current.plusMonths(1);
            case "QUARTERLY":
                return current.plusMonths(3);
            case "ANNUALLY":
                return current.plusYears(1);
            default:
                throw new IllegalArgumentException("Unknown installment frequency: " + frequency);
        }
    }

}
