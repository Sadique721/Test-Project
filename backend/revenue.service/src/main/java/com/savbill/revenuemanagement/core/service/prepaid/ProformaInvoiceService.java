package com.savbill.revenuemanagement.core.service.prepaid;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.revenuemanagement.core.constants.APIConstants;
import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.constants.LogConstants;
import com.savbill.revenuemanagement.core.dto.customer.*;
import com.savbill.revenuemanagement.core.dto.customer.ChargeDetailDto;
import com.savbill.revenuemanagement.core.dto.customer.PlanAndChargeRequest;
import com.savbill.revenuemanagement.core.dto.customer.PlanMappingDto;
import com.savbill.revenuemanagement.core.dto.customer.Subscriber;
import com.savbill.revenuemanagement.core.dto.invoice.xml.PlanInformation;
import com.savbill.revenuemanagement.core.entity.customers.*;
import com.savbill.revenuemanagement.core.entity.debitdoc.*;
import com.savbill.revenuemanagement.core.entity.customers.CustomerAddress;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.debitdoc.ProfomInvoiceDetails;
import com.savbill.revenuemanagement.core.entity.debitdoc.ProfomaDebitDocumentDetail;
import com.savbill.revenuemanagement.core.entity.debitdoc.ProformaDebitDocument;
import com.savbill.revenuemanagement.core.entity.invoice.ChargeDetails;
import com.savbill.revenuemanagement.core.entity.invoice.Invoice;
import com.savbill.revenuemanagement.core.entity.invoice.InvoiceDetail;
import com.savbill.revenuemanagement.core.entity.invoice.SubscriberAddress;
import com.savbill.revenuemanagement.core.repository.customer.CustomerAddressRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.debit.ProfomaDebitDocDetailsRepository;
import com.savbill.revenuemanagement.core.repository.debit.ProfomaDebitDocRepository;
import com.savbill.revenuemanagement.core.repository.partner.PostpaidPlanChargeRepo;
import com.savbill.revenuemanagement.core.security.dto.LoggedInUser;
import com.savbill.revenuemanagement.core.service.ClientServ.domain.ClientService;
import com.savbill.revenuemanagement.core.service.ClientServ.repository.ClientServiceRepository;
import com.savbill.revenuemanagement.core.service.common.NumberSequenceUtil;
import com.savbill.revenuemanagement.core.util.CurrencyUtil;
import com.savbill.revenuemanagement.productmanagement.Charge.domain.Charge;
import com.savbill.revenuemanagement.productmanagement.Charge.repocitory.ChargeRepository;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostpaidPlan;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostpaidPlanRepo;
import com.savbill.revenuemanagement.productmanagement.Tax.domain.Tax;
import com.savbill.revenuemanagement.productmanagement.Tax.domain.TaxTypeTier;
import com.savbill.revenuemanagement.productmanagement.Tax.repository.TaxRepository;
import com.savbill.revenuemanagement.rabbitmq.messages.CustomerBillingMessage;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProformaInvoiceService {


    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private NumberSequenceUtil numberSequenceUtil;
    @Value("${project.currency: Rs.}")
    private String curr;

    @Value("${project.currency.cent: Rs.}")
    private String centCurr;

    @Autowired
    Tracer tracer;

    @Autowired
    ChargeRepository chargeRepository;

    @Autowired
    TaxRepository taxRepository;

    @Autowired
    ClientServiceRepository clientServiceRepository;
    @Autowired
    ProfomaDebitDocRepository profomaDebitDocRepository;
    @Autowired
    ProfomaDebitDocDetailsRepository profomaDebitDocDetailsRepository;
    @Autowired
    PostpaidPlanRepo planRepo;
    @Autowired
    CustomerAddressRepository customerAddressRepository;

    private static final Logger logger = Logger.getLogger(PrepaidInvoiceService.class);
    @Autowired
    private PostpaidPlanChargeRepo postpaidPlanChargeRepo;

    public ProformaDebitDocument createPrepaidInvoiceCaf(CustomerBillingMessage customerBillingMessage, PlanAndChargeRequest request) {
        logger.info("Initiating createPrepaidInvoiceCaf Method Process of CAF creation for Trial invoice");
        Map<String, Object> data = customerBillingMessage.getData();
        Integer RESP_CODE = APIConstants.FAIL;
        String nextBillDate = null;
        LocalDate billDate = null;
        try {
            TraceContext traceContext =customerBillingMessage.getTraceContext();
            MDC.put("type", "Create");
            MDC.put("traceId",traceContext.traceIdString());
            MDC.put("spanId",traceContext.spanIdString());
            if (CollectionUtils.isEmpty(data)) {
                logger.error("customer billing message data is empty");
            }
            if (!data.containsKey(CustomerBillingMessage.CUST_ID)) {
                logger.error("customer billing message custId is empty");
            }
            if(customerBillingMessage.getBilldate()!=null){
                billDate=customerBillingMessage.getBilldate();
            }
            LocalDate strBillDate =  LocalDate.now();
            Integer custId = (Integer) data.get(CustomerBillingMessage.CUST_ID);
            Optional<Customers> customersOptional = Optional.ofNullable(customersRepository.getByCustomerId(custId));
            String createdByName = (String) data.get(CustomerBillingMessage.CREATED_BY_NAME);
            if (!customersOptional.isPresent()) {
                logger.error("Given customer not available for id: " + custId);
            }
            Customers customers = customersOptional.get();
            logger.info("Initiated Proforma Invoice Method Process  for  invoice of Customer : " + customers.getUsername());
            String postpaidAdvance = null;
            if (customers.getCusttype().equalsIgnoreCase("Postpaid") ){
                postpaidAdvance = (String) data.get(CustomerBillingMessage.POSTPAIDADVANCE);
            }
            logger.info("Initiating Proforma Invoice   of Customer : " + customers.getUsername());
            ProfomInvoiceDetails invoiceDetails = prepareInvoiceDetailCaf(customers,  postpaidAdvance, request);
            ProformaDebitDocument debitDocument = invoiceDetails.getProfomaDebitDocument();
            debitDocument.setProfomaDebitDocumentDetails(null);

            if(debitDocument.getTotalamount() <= 0) {
                if(customerBillingMessage.getData().containsKey("mvnoId")){
                    Integer mvnoId  = (Integer) customerBillingMessage.getData().get("mvnoId");
                    ClientService allowZeroInvoice = clientServiceRepository.getByNameAndMvnoId(Constants.ALLOWZEROCHARGEINVOICE,mvnoId);
                    if (allowZeroInvoice.getValue().equalsIgnoreCase("No")){
                        logger.error("Invoice can not be generated due to 0 ammount");
                        return null;
                    }
                }
            }
            List<ProfomaDebitDocumentDetail> debitDocDetailsList = invoiceDetails.getProfomaDebitDocDetails();
            debitDocument = profomaDebitDocRepository.save(debitDocument);
            ProformaDebitDocument finalDebitDocument = debitDocument;
            debitDocDetailsList = debitDocDetailsList.stream().peek(debitDocDetails -> debitDocDetails.setDebitdocumentid(finalDebitDocument.getId())).collect(Collectors.toList());
            profomaDebitDocDetailsRepository.saveAll(debitDocDetailsList);
            debitDocument.setProfomaDebitDocumentDetails(debitDocDetailsList);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + " Customer management Service, "+"Successfully Invoice Created for Customer id :" +  custId + LogConstants.REQUEST_BY + createdByName+  LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return debitDocument;
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM+ " Customer management Service, "+"Error During Invoice Generation for Customer id : " +   (Integer) data.get(CustomerBillingMessage.CUST_ID) +   LogConstants.REQUEST_BY + (String) data.get(CustomerBillingMessage.CREATED_BY_NAME) +  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return null;

    }
    public ProfomInvoiceDetails   prepareInvoiceDetailCaf(Customers customers,
                                                        String postpaidAdvance,PlanAndChargeRequest request) {
        try {
            logger.debug("Initiated Proforma Invoice  invoice of Customer : " + customers.getUsername());
            if(!CollectionUtils.isEmpty(request.getPlanMapping())) {
//                DebitDocument debitDocument = new DebitDocument();
                ProformaDebitDocument debitDocument = new ProformaDebitDocument();
                List<ProfomaDebitDocumentDetail> debitDocDetailsList = new ArrayList<>();
                Customers parentCustomers = customers;
                ProfomaDebitDocumentDetail debitDocDetails = new ProfomaDebitDocumentDetail();
                for (PlanMappingDto planMappping : request.getPlanMapping()) {
                    if (planMappping.getDiscount() != null) {
                        debitDocDetails.setDiscount(planMappping.getDiscount());
                        logger.debug("Setting Discount of " + planMappping.getDiscount() + " for customer: " + customers.getUsername());
                    } else {
                        debitDocDetails.setDiscount(0d);
                        logger.debug("Setting Discount of " + planMappping.getDiscount() + " for customer: " + customers.getUsername());
                    }
                    // For parent-child
                    if (planMappping.getInvoiceType() != null && planMappping.getInvoiceType().equalsIgnoreCase(Constants.CUSTOMER_INVOICE_TYPE.GROUP) && customers.getParentCustomers() != null) {
                        parentCustomers = customers.getParentCustomers();
                    } else {
                        parentCustomers = customers;
                    }
                    logger.info("Initiating setDebitDocDetails process for  Plan Id: " + planMappping.getPlanId() + " for customer: " + customers.getUsername());
                    debitDocDetails = setDebitDocDetails(planMappping, request, parentCustomers, debitDocDetails, postpaidAdvance);
                    debitDocDetailsList.add(debitDocDetails);
                    debitDocument.setStartdate(debitDocDetails.getStartdate());
                    if (customers.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_POSTPAID)) {
                        debitDocument.setEndate(debitDocDetails.getEnddate());
                    }
                }
                if (!request.getCustChargeDetailsPojoList().isEmpty())
                {
                    for (ChargeDetailDto custChargeDetailsPojo : request.getCustChargeDetailsPojoList()) {
                        debitDocDetails = setDebitDocDetailsForChargeCaf(custChargeDetailsPojo);
                        debitDocDetailsList.add(debitDocDetails);
                    }
            }
                debitDocument = setDebitDocBasicDetails(debitDocument, debitDocDetailsList, parentCustomers);
                ProfomInvoiceDetails invoiceDetails = new ProfomInvoiceDetails(debitDocument, debitDocDetailsList);
                return invoiceDetails;
            } else {
                logger.error("No plan available with customer!");
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error while generate invoice: "+ex.getMessage());
        }
        return null;
    }
    public ProfomaDebitDocumentDetail setDebitDocDetails(PlanMappingDto planMappping, PlanAndChargeRequest request, Customers customers, ProfomaDebitDocumentDetail debitDocDetails,String postpaidAdvance) {

        Optional<Charge> charge = chargeRepository.findChargeByPlanId(Math.toIntExact(planMappping.getPlanId()));
        debitDocDetails.setChargename(charge.get().getName());
        debitDocDetails.setChargeid(charge.get().getId());
        debitDocDetails.setChargetype(charge.get().getChargetype());
        debitDocDetails.setNoofcycle(-1);//TODO: Need to confirm
        debitDocDetails.setDescription(charge.get().getDesc());
        debitDocDetails.setPlanId(planMappping.getPlanId().intValue());
        debitDocDetails.setProrationtype("F");
        debitDocDetails.setStartdate(LocalDateTime.now());
        double discount=0.0;
        if(planMappping.getDiscount() != null)
            discount=planMappping.getDiscount();
        debitDocDetails.setDiscount(discount);
        long planValidityDays = planMappping.getValidity();
        if(customers.getCusttype().equalsIgnoreCase("Postpaid") && customers.getId()!=2 && ! customers.getUsername().equalsIgnoreCase("ORGANIZATIONPOS"))
        {
            logger.debug("Calculating UsedValidity days for customer: " + customers.getUsername());
            Double chargePrice = charge.get().getActualprice();
        }

        logger.debug("Setting Subtotal in debitDocDetails for customer :  "+ customers.getUsername() + " as: " + charge.get().getActualprice()  );
        debitDocDetails.setSubtotal(charge.get().getPrice());
        debitDocDetails = calculateTierTax(planMappping.getDiscount(), charge.get().getTax(), charge.get(), debitDocDetails);
        debitDocDetails.setDiscount(debitDocDetails.getDiscount());
        if(request.getIsTaxCalculate()) {
            debitDocDetails.setTotalamount(debitDocDetails.getSubtotal() - debitDocDetails.getDiscount() + debitDocDetails.getTax());
            debitDocDetails.setTax(debitDocDetails.getTax());
        }else
        {
            debitDocDetails.setTotalamount(debitDocDetails.getSubtotal() - debitDocDetails.getDiscount());
            debitDocDetails.setTax(0.0);
        }
        logger.debug("Setting Totalamount in debitDocDetails for customer :  "+ customers.getUsername() + " as: " + debitDocDetails.getTotalamount());
        debitDocDetails.setDiscount(discount);
        return debitDocDetails;
    }

    public ProformaDebitDocument setDebitDocBasicDetails(ProformaDebitDocument debitDocument, List<ProfomaDebitDocumentDetail> debitDocDetailsList, Customers customers) {
        double totalCharge = debitDocDetailsList.stream().mapToDouble(ProfomaDebitDocumentDetail::getSubtotal).sum();
        double totalTax = debitDocDetailsList.stream().mapToDouble(ProfomaDebitDocumentDetail::getTax).sum();
        double totalDiscount = debitDocDetailsList.stream().filter(detail -> detail.getDiscount() != null).mapToDouble(ProfomaDebitDocumentDetail::getDiscount).sum();
        double total = debitDocDetailsList.stream().mapToDouble(ProfomaDebitDocumentDetail::getTotalamount).sum();
        debitDocument.setProfomaDebitDocumentDetails(debitDocDetailsList);
        debitDocument.setSubtotal(totalCharge);
        debitDocument.setTax(totalTax);
        debitDocument.setTotalamount(total);
        debitDocument.setDiscount(totalDiscount);
        debitDocument.setBilldate(LocalDateTime.now());
        debitDocument.setCustomer(customers);
        debitDocument.setBillrunstatus(Constants.INVOICE_STATUS.GENERATED.status());
//            debitDocument.setBillrunid(); TODO: need to understand
        debitDocument.setBillableToName(customers.getFullName());//TODO: Need to check
//        debitDocument.setCustpackrelid(cprId);
//            debitDocument.setDebitDocumentTAXRels(); TODO: need to add
        debitDocument.setIsDelete(false);
        Boolean isLco = false;
        if(customers.getLcoId() != null)
            isLco = true;
        debitDocument.setDocnumber(numberSequenceUtil.getInvoiceNumberForTrial(isLco, customers.getPartner(), customers.getMvnoId()));
        debitDocument.setTotaldue(debitDocument.getTotalamount());
        debitDocument.setCreatedate(LocalDateTime.now());
        debitDocument.setLastModifiedByName(customers.getLastModifiedByName());
        debitDocument.setCreatedByName(customers.getCreatedByName());
        if(debitDocument.getStartdate() == null)
            debitDocument.setStartdate(LocalDateTime.now());
        if(debitDocument.getEndate() == null)
            debitDocument.setEndate(LocalDateTime.now());
        if(customers.getCurrency() != null){
            String centCurrDynamic = getSubunitName(customers.getCurrency());
            debitDocument.setAmountinwords(convertToAmount(debitDocument.getTotalamount(), customers.getCurrency(), centCurrDynamic) + " Only");
            debitDocument.setDueinwords(convertToAmount(debitDocument.getTotaldue(), customers.getCurrency(), centCurrDynamic) + " Only");
        } else {
            debitDocument.setAmountinwords(convertToAmount(debitDocument.getTotalamount(), curr, centCurr) + " Only");
            debitDocument.setDueinwords(convertToAmount(debitDocument.getTotaldue(), curr, centCurr) + " Only");
        }
        debitDocument.setPreviousbalance(0.0);
        debitDocument.setLatepaymentfee(0.0);
        debitDocument.setCurrentpayment(0.0);
        debitDocument.setCurrentdebit(0.0);
        return debitDocument;
    }
    public static String convertToAmount(Double value, String curr, String centCurr) {


        long roundedValue = Math.round(value * 100); // Convert to paisa/cent fully  (3540.28=354028)
        long amount = roundedValue / 100;            // Rs part    ( 3540)
        long centsAsInt = roundedValue % 100;             // Paisa part  ( 28)

        String amountStr = CurrencyUtil.convert(amount);
        String centStr = CurrencyUtil.convert(centsAsInt);
        return amountStr + " " + curr + " AND " + centStr + " " + centCurr;
    }
    public static String getSubunitName(String currCode) {
        Currency c = Currency.getInstance(currCode);
        int fractionDigits = c.getDefaultFractionDigits();
        switch(currCode) {
            case "MMK":
                return "Pya";
            case "INR":
                return "Paise";
            case "USD":
                return "Cent";
            default:
                return (fractionDigits == 2 ? "Cent" : "");
        }
    }
    public int getLoggedInUserId() {
        int loggedInUserId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
            }
        } catch (Exception e) {
            loggedInUserId = -1;
        }
        return loggedInUserId;
    }
    public ProfomaDebitDocumentDetail calculateTierTax(Double discount, Tax tax , Charge charge , ProfomaDebitDocumentDetail profomaDebitDocumentDetail)
    {
        logger.info("Initiating calculateTierTax process for  CustomerChargeHistory ID :  "+ charge.getId());
        Double calTax = 0.0;
        Double tier1 = 0.0;
        Double tier2 = 0.0;
        Double tier3 = 0.0;

        Boolean isBefore1 = false;
        Boolean isBefore2 = false;
        Boolean isBefore3 = false;

//        Tax tax=taxRepository.findById(taxId).get();
        Integer taxId = tax.getId();
        if(discount == null){
            discount = 0.0;
        }
        logger.info("Tax Id applied to CustomerChargeHistory is TAX ID :  "+ tax.getId());

        if(tax.getTaxtype().equalsIgnoreCase("Compound")) {
            Double price = charge.getPrice();
            profomaDebitDocumentDetail.setTax(0d);
            Boolean isDiscountCalculated = false;
            Double totalTax=0.0;

            for (TaxTypeTier taxData : tax.getTieredList()) {
                if (taxData.getBeforeDiscount() != null)
                    taxData.setBeforeDiscount(taxData.getBeforeDiscount());

                if (Boolean.FALSE.equals(taxData.getBeforeDiscount()) && isDiscountCalculated) {
                    tier1 = ((price + totalTax - discount) * (taxData.getRate() / 100.0f));
                    totalTax=totalTax+tier1;
                }

                if (Boolean.FALSE.equals(taxData.getBeforeDiscount()) && !isDiscountCalculated) {
                    Double discountAmount = charge.getPrice() * (discount / 100);
                    profomaDebitDocumentDetail.setDiscount(discountAmount);
                    isDiscountCalculated=true;
                    tier1 = ((price + totalTax - discount) * (taxData.getRate() / 100.0f));
                    totalTax=totalTax+tier1;
                }
                if (Boolean.TRUE.equals(taxData.getBeforeDiscount())) {
                    tier1 = ((price + totalTax) * (taxData.getRate() / 100.0f));
                    totalTax=totalTax+tier1;
                    if(!isDiscountCalculated) {
                        Double discountAmount = (charge.getPrice() +totalTax) * (discount / 100);
                        profomaDebitDocumentDetail.setDiscount(discountAmount);
                        isDiscountCalculated=true;
                    }
                }
            }
           profomaDebitDocumentDetail.setTax(totalTax);
        }
        else if(tax.getTaxtype().equalsIgnoreCase("TIER")){
            List<TaxTypeTier> levelOneList=tax.getTieredList().stream().filter(x->x.getTaxGroup().equalsIgnoreCase("TIER1")).collect(Collectors.toList());
            Long level1Count=levelOneList.stream().count();
            if(level1Count>0 && levelOneList.get(0).getBeforeDiscount() != null)
                isBefore1=levelOneList.get(0).getBeforeDiscount();

            if(level1Count>1 && levelOneList.get(1).getBeforeDiscount() != null)
                isBefore2=levelOneList.get(1).getBeforeDiscount();

            if(level1Count>2 && levelOneList.get(2).getBeforeDiscount() != null)
                isBefore3=levelOneList.get(2).getBeforeDiscount();
            int count = 0;
            Double price = charge.getPrice();
//            Double taxAmount = tax.get;
            profomaDebitDocumentDetail.setTax(0d);

            for (TaxTypeTier taxData:tax.getTieredList())
            {
                count++;
                if (taxData.getTaxGroup().equalsIgnoreCase("TIER1")) {
                    logger.info("TAX ID :  "+ taxId + " is TIER 1 tax");
                    if(taxData.getBeforeDiscount() != null)
                        taxData.setBeforeDiscount(taxData.getBeforeDiscount());

                    if (Boolean.FALSE.equals(taxData.getBeforeDiscount()))
                    {
                        logger.debug("TAX ID :  "+ taxId + " has TaxBeforeDiscount flag as False");
                        if(level1Count==1)
                        {
                            Double discountAmount = charge.getPrice() * (discount / 100);
                            profomaDebitDocumentDetail.setDiscount(discountAmount);
                            tier1 = ((price + tier1 - discount) * (taxData.getRate() / 100.0f));
                            profomaDebitDocumentDetail.setTax(tier1);
                            logger.debug("TAX ID :  "+ taxId + " has tier1 tax amount  as : " + tier1);
                        }

                        if(level1Count==2 && (!isBefore1 && !isBefore2))
                        {
                            if(count==1 && !isBefore1 && !isBefore2)
                            {
                                Double discountAmount = charge.getPrice() * (discount / 100);
                                profomaDebitDocumentDetail.setDiscount(discountAmount);
                            }
                            tier1 = ((price - discount) * (taxData.getRate() / 100.0f));
                            profomaDebitDocumentDetail.setTax(profomaDebitDocumentDetail.getTax() + tier1);
                            logger.debug("TAX ID :  "+ taxId + " has tier1 tax amount  as : " + tier1);
                        }

                        if(level1Count==2 && count==2 && (isBefore1 && !isBefore2))
                        {
                            tier1 = ((price + tier1- discount) * (taxData.getRate() / 100.0f));
                            profomaDebitDocumentDetail.setTax(profomaDebitDocumentDetail.getTax() + tier1);
                            logger.debug("TAX ID :  "+ taxId + " has tier1 tax amount  as : " + tier1);
                        }

                        if(level1Count==2 && (!isBefore1 && isBefore2))
                        {
                            if(count==1 && !isBefore1 && isBefore2)
                            {
                                Double discountAmount = charge.getPrice() * (discount / 100);
                                profomaDebitDocumentDetail.setDiscount(discountAmount);
                            }

                            tier1 = ((price + tier1 - discount) * (taxData.getRate() / 100.0f));
                            logger.debug("TAX ID :  "+ taxId + " has tier1 tax amount  as : " + tier1);
                            profomaDebitDocumentDetail.setTax(profomaDebitDocumentDetail.getTax() + tier1);
                            price = price-discount;
                        }
                    } else {

                        if(level1Count==1)
                        {
                            tier1 = ((price + tier1) * (taxData.getRate() / 100.0f));
                            profomaDebitDocumentDetail.setTax(tier1);
                            Double discountAmount = (charge.getPrice()+tier1) * (discount / 100);
                            profomaDebitDocumentDetail.setDiscount(discountAmount);
                        }

                        if(level1Count==2 && (isBefore1 && isBefore2))
                        {
                            Double tmp=tier1;
                            tier1 = ((price + tier1) * (taxData.getRate() / 100.0f));
                            profomaDebitDocumentDetail.setTax(profomaDebitDocumentDetail.getTax()+ tier1);
                            tmp=tier1+tmp;
                            if(count==2 && isBefore1 && isBefore2)
                            {
                                Double discountAmount = (charge.getPrice()+tmp) * (discount / 100);
                                profomaDebitDocumentDetail.setDiscount(discountAmount);
                            }
                        }

                        if(level1Count==2 && (isBefore1 && !isBefore2))
                        {
                            tier1 = ((price + tier1) * (taxData.getRate() / 100.0f));
                            profomaDebitDocumentDetail.setTax(profomaDebitDocumentDetail.getTax() + tier1);
                            if(count==1 && isBefore1 && !isBefore2)
                            {
                                Double discountAmount = (charge.getPrice()+tier1) * (discount / 100);
                                profomaDebitDocumentDetail.setDiscount(discountAmount);
                            }
                        }

                        if(level1Count==2 && count==2 && (!isBefore1 && isBefore2))
                        {
                            tier1 = ((price + tier1) * (taxData.getRate() / 100.0f));
                            profomaDebitDocumentDetail.setTax(profomaDebitDocumentDetail.getTax() + tier1);
                        }
                    }
                }

                if (taxData.getTaxGroup().equalsIgnoreCase("TIER2")) {
                    logger.info("TAX ID :  "+ taxId + " is TIER 2 tax");
                    if (taxData.getBeforeDiscount()!=null && !taxData.getBeforeDiscount())
                        tier2 = tier2  + ((tier1) * (taxData.getRate() / 100.0f));
                    else
                        tier2 = tier2 + ((tier1) * (taxData.getRate() / 100.0f));
                    profomaDebitDocumentDetail.setTax(profomaDebitDocumentDetail.getTax() + tier2);
                }

                if (taxData.getTaxGroup().equalsIgnoreCase("TIER3")) {
                    logger.info("TAX ID :  "+ taxId + " is TIER 3 tax");
                    if (taxData.getBeforeDiscount()!=null && !taxData.getBeforeDiscount())
                        tier3 = tier3  + ((tier2) * (taxData.getRate() / 100.0f));
                    else
                        tier3 = tier3 + ((tier2) * (taxData.getRate() / 100.0f));
                    profomaDebitDocumentDetail.setTax(profomaDebitDocumentDetail.getTax() + tier3);
                }
            }
        }
        return profomaDebitDocumentDetail;
    }

    public String setInvoiceXml(ProformaDebitDocument debitDocument,List<ProfomaDebitDocumentDetail> details) {
        String xml = "";
        try {
            Object[] result = profomaDebitDocRepository.findPlanTypeByProformaAddrId(details.get(0).getDebitdocdetailid());
            Object[] resultRow = (Object[]) result[0];
            String planType = (String) resultRow[0];
            Integer validity = ((BigDecimal) resultRow[1]).intValue();
            Map<String, Object> invoiceData = new HashMap<>();
            invoiceData.put("dueAmountFromLastInvoice", null);
            invoiceData.put("planType", planType);
            if(!planType.equalsIgnoreCase(CommonConstants.PLAN_TYPE_POSTPAID)){
                invoiceData.put("validity", validity);
            }
            invoiceData.put("customerActivationDate", null);
            Invoice invoice = convertDebitDocToInvoice(debitDocument, details, invoiceData);
            xml = createXML(invoice);
            debitDocument.setDocument(xml);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error while save xml: "+ex.getMessage());
        }
        return xml;
    }

    public Invoice convertDebitDocToInvoice(ProformaDebitDocument debitDocument,List<ProfomaDebitDocumentDetail> details, Map<String, Object> invoiceData) {
        Customers customers = debitDocument.getCustomer();
        Invoice invoice = new Invoice();
        invoice.setCustomerId(String.valueOf(customers.getId()));
        invoice.setPhone(customers.getPhone());
        invoice.setBUID(customers.getBuId());
        invoice.setTotal(debitDocument.getTotalamount());
        invoice.setTotalDue(debitDocument.getTotaldue());
        invoice.setTotalDueInWords(debitDocument.getDueinwords());
        invoice.setTotalAmountInWords(debitDocument.getAmountinwords());
        invoice.setEmail(customers.getEmail());
        invoice.setMobile(customers.getMobile());
        invoice.setNumber(debitDocument.getDocnumber());

        invoice.setStartDate(getDatefromLocalDateTime(debitDocument.getStartdate()));
        invoice.setEndDate(getDatefromLocalDateTime(debitDocument.getEndate()));
        invoice.setDueDate(getDatefromLocalDateTime(debitDocument.getEndate()));

        invoice.setCustomerInformation(convertCustomerToSubscriber(customers));
        invoice.setBillDate(getDatefromLocalDateTime(debitDocument.getBilldate()));
        invoice.setCharge(debitDocument.getSubtotal());
        invoice.setBillrunStatus(debitDocument.getBillrunstatus());
        invoice.setCreatebyname(debitDocument.getCreatedByName());
        invoice.setCreateDate(getDatefromLocalDateTime(debitDocument.getCreatedate()));
        invoice.setDiscount(debitDocument.getDiscount());
        invoice.setEmail(customers.getEmail());
        invoice.setTotalDueInWords(debitDocument.getDueinwords());
        invoice.setTax(debitDocument.getTax());
        invoice.setPlanInformation(getPlanInformationFromTrialDebitDoc(debitDocument));
        invoice.setChargeDetails(getchargeDetailFromTrialDebitDoc(debitDocument));
//        invoice.setTaxList(getTaxListFromDebitDoc(debitDocument));
        invoice.setInvoiceList(getInvoiceDetailFromDebitDoc(debitDocument,details));
        invoice.setAddressDetail(getCustomerAddressDetail(customers));
        invoice.setPreviousWalletBalance(debitDocument.getPreviousbalance());
        if(invoiceData.get("billingCycle") != null){
            invoice.setBillingCycleInMonths((Integer) invoiceData.get("billingCycle"));
        }
        if(invoiceData.get("customerActivationDate") != null){
            invoice.setCustomerActivationDate(Date.from(((LocalDate) invoiceData.get("customerActivationDate")).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        if(debitDocument.getCustomer().getGraceDay() != null && debitDocument.getDuedate() != null){
            LocalDateTime dueDate = debitDocument.getDuedate();
            Integer graceDays = debitDocument.getCustomer().getGraceDay();
            LocalDateTime gracePeriodDateTime = dueDate.plusDays(graceDays);
            Date gracePeriodEndDate = Date.from(gracePeriodDateTime.atZone(ZoneId.systemDefault()).toInstant());
            invoice.setGracePeriodEndDate(gracePeriodEndDate);
        }
        if(invoiceData.get("dueAmountFromLastInvoice") != null){
            invoice.setDueAmountFromLastInvoice((double) invoiceData.get("dueAmountFromLastInvoice"));
        }
        if(invoiceData.get("validity") != null) {
            invoice.setPlanValidityInDays((Integer) invoiceData.get("validity"));
        }
        if(invoiceData.get("planType") != null) {
            invoice.setSubscriptionPlanType((String) invoiceData.get("planType"));
        }
        if(customers.getCurrency() != null){
            invoice.setCustomerCurrency(customers.getCurrency());
        } else {
            String currency = clientServiceRepository.findValueByNameAndMvnoId("CURRENCY_FOR_PAYMENT", customers.getMvnoId());
            if(currency != null){
                invoice.setCustomerCurrency(currency);
            }
        }
        return invoice;
    }

    public Date getDatefromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime != null) {
            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        }
        return new Date();
    }
    public Subscriber convertCustomerToSubscriber(Customers customers) {
        Subscriber subscriber = new Subscriber();
        subscriber.setAccountnumber(customers.getAcctno());
        subscriber.setFirstname(customers.getFirstname());
        subscriber.setUserName(customers.getUsername());
        subscriber.setEmail(customers.getEmail());
        subscriber.setPan(customers.getPan());
        subscriber.setMobile(customers.getMobile());
        subscriber.setPhone(customers.getPhone());
        subscriber.setLastname(customers.getLastname());
        subscriber.setCountry(customers.getCountryCode());
        subscriber.setName(customers.getFullName());
        return subscriber;
    }
    public ArrayList<PlanInformation> getPlanInformationFromTrialDebitDoc(ProformaDebitDocument debitDocument) {
        ArrayList<PlanInformation> planInformations = new ArrayList<>();
        Set<Integer> planIds = debitDocument.getProfomaDebitDocumentDetails().stream().filter(docDetails -> docDetails.getPlanId() != null).map(ProfomaDebitDocumentDetail::getPlanId).collect(Collectors.toSet());
        for (Integer planId : planIds) {
            Optional<PostpaidPlan> postpaidPlan = planRepo.findById(planId);
            PlanInformation planInformation = new PlanInformation();
            planInformation.setCreatedate(getDatefromLocalDateTime(debitDocument.getCreatedate()));
            planInformation.setEnddate(getDatefromLocalDateTime(debitDocument.getEndate()));
            planInformation.setDescription(postpaidPlan.get().getDesc());
            planInformation.setDisplayname(postpaidPlan.get().getDisplayName());
            planInformation.setName(postpaidPlan.get().getName());
            planInformation.setPlanGroupName(postpaidPlan.get().getPlanGroup());
            planInformation.setStatus(postpaidPlan.get().getStatus());
            if (postpaidPlan.get().getSaccode()!=null){
                planInformation.setSac(postpaidPlan.get().getSaccode());
            }
            if (postpaidPlan.get().getValidity()!=null){
                planInformation.setValidity(postpaidPlan.get().getValidity());
            }
            planInformations.add(planInformation);
        }
        return planInformations;
    }
    private List<ChargeDetails> getchargeDetailFromTrialDebitDoc(ProformaDebitDocument debitDocument) {
        ArrayList<ChargeDetails> chargeDetails = new ArrayList<>();
        try {
            List<ProfomaDebitDocumentDetail> debitDocDetails = debitDocument.getProfomaDebitDocumentDetails();

            if (!CollectionUtils.isEmpty(debitDocDetails)) {
                Map<String, ChargeDetails> totalAmountsAndTaxesByChargeType = debitDocDetails.stream()
                        .collect(Collectors.groupingBy(
                                ProfomaDebitDocumentDetail::getChargetype,
                                Collectors.collectingAndThen(
                                        Collectors.reducing(new ChargeDetails(0.0, 0.0, 0.0),
                                                d -> new ChargeDetails(d.getTax(), d.getTotalamount(), d.getSubtotal()),
                                                (a, b) -> new ChargeDetails(a.getTax() + b.getTax(), a.getTotal() + b.getTotal(), a.getPrice() + b.getPrice())),
                                        sum -> sum
                                )
                        ));

                for (Map.Entry<String, ChargeDetails> entry : totalAmountsAndTaxesByChargeType.entrySet()) {
                    ChargeDetails chargeDetail = entry.getValue();
                    chargeDetail.setChargeType(entry.getKey());
                    chargeDetails.add(chargeDetail);
                }
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return  chargeDetails;
    }

    public ArrayList<InvoiceDetail> getInvoiceDetailFromDebitDoc(ProformaDebitDocument debitDocument,List<ProfomaDebitDocumentDetail> debitDocumentDetails) {
        List<ProfomaDebitDocumentDetail> debitDocDetails = debitDocumentDetails;
        Customers customers = debitDocument.getCustomer();
        ArrayList<InvoiceDetail> invoiceDetails = new ArrayList<>();
        for (ProfomaDebitDocumentDetail docDetails : debitDocDetails) {
            InvoiceDetail invoiceDetail = new InvoiceDetail();
            invoiceDetail.setCycle(docDetails.getNoofcycle() + "");
            invoiceDetail.setDescription(docDetails.getDescription());
            if(docDetails.getDiscount()!=null)
                invoiceDetail.setDiscount(docDetails.getDiscount());
            else
                invoiceDetail.setDiscount(0l);
            invoiceDetail.setEndDate(new Date(getDatefromLocalDateTime(customers.getNextBillDate().atStartOfDay()).getTime() - 1 * 24 * 3600 * 1000));
            invoiceDetail.setInvoiceId(String.valueOf(debitDocument.getId()));
            invoiceDetail.setItemChargeId(String.valueOf(docDetails.getChargeid()));
            invoiceDetail.setName(docDetails.getChargename());
            invoiceDetail.setNoOfCycle(-1);
            invoiceDetail.setPrice(docDetails.getSubtotal());
            invoiceDetail.setProrationType("F");
            invoiceDetail.setCreatedByname(debitDocument.getCreatedByName());
            invoiceDetail.setUpdateByName(debitDocument.getCreatedByName());

            //if (docDetails.getCustServiceId() != null)
            //invoiceDetail.setCustServiceId(docDetails.getCustServiceId());
            //if (docDetails.getServiceId() != null)
            //invoiceDetail.setServiceId(docDetails.getServiceId());

            if (customers.getLastBillDate() != null)
                invoiceDetail.setStartDate(getDatefromLocalDateTime(customers.getLastBillDate().atStartOfDay()));
            else
                invoiceDetail.setStartDate(getDatefromLocalDateTime(debitDocument.getStartdate()));

            invoiceDetail.setTax(docDetails.getTax());
            invoiceDetail.setType(docDetails.getChargetype());
            //invoiceDetail.setPlanId(docDetails.getPlanId());
            if (docDetails.getDiscount() != null) {
                invoiceDetail.setCustomerDiscount(docDetails.getDiscount());
                invoiceDetail.setDiscount(docDetails.getDiscount());
            } else {
                invoiceDetail.setCustomerDiscount(0d);
                invoiceDetail.setDiscount(0d);
            }
            invoiceDetail.setTotal(round(docDetails.getSubtotal() + docDetails.getTax() - invoiceDetail.getDiscount(), 2));
            invoiceDetails.add(invoiceDetail);
        }

        return invoiceDetails;
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private ArrayList<SubscriberAddress> getCustomerAddressDetail(Customers customers) {
        CustomerAddress customerAddress= customerAddressRepository.findByAddressTypeAndCustomerAndVersion("Present",customers,"NEW");
        ArrayList<SubscriberAddress> list=new ArrayList<>();
        if(customerAddress!=null)
        {
            SubscriberAddress address=new SubscriberAddress();
            if(customerAddress.getPincode()!=null)
                address.setPincode(customerAddress.getPincode().getPincode());
            if(customerAddress.getCity()!=null)
                address.setCity(customerAddress.getCity().getName());
            if(customerAddress.getState()!=null)
                address.setState(customerAddress.getState().getName());
            if(customerAddress.getCountry()!=null)
                address.setCountry(customerAddress.getCountry().getName());
            if(customerAddress.getArea()!=null)
                address.setArea(customerAddress.getArea().getName());
            address.setLandmark(customerAddress.getLandmark());
            address.setAddress1(customerAddress.getAddress1());
            address.setAddress2(customerAddress.getAddress2());
            list.add(address);
        }
        return list;
    }
    public String createXML(Invoice invoice) {
        JAXBContext jaxbContext;
        StringWriter sw = new StringWriter();
        try {
            //logger.debug("[InvoiceUtil]:Invoice Data is " + invoice);
            jaxbContext = JAXBContext.newInstance(Invoice.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(invoice, sw);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }
    private ProfomaDebitDocumentDetail setDebitDocDetailsForChargeCaf(ChargeDetailDto custChargeDetails) {
        ProfomaDebitDocumentDetail debitDocDetails = new ProfomaDebitDocumentDetail();
        Charge charge = chargeRepository.findById(custChargeDetails.getChargeid()).get();
        debitDocDetails.setChargecycle(String.valueOf(custChargeDetails.getBillingCycle()));
        debitDocDetails.setChargename(charge.getName());
        debitDocDetails.setChargeid(charge.getId());
        debitDocDetails.setChargetype(charge.getChargetype());
        debitDocDetails.setNoofcycle(-1);//TODO: Need to confirm
        debitDocDetails.setDescription(charge.getDesc());
        debitDocDetails.setProrationtype("F");

        if(custChargeDetails.getDiscount() != null)
            debitDocDetails.setDiscount(custChargeDetails.getDiscount());
        else
            debitDocDetails.setDiscount(0d);

        //amount calculations
        debitDocDetails.setSubtotal(custChargeDetails.getPrice());
        debitDocDetails = calculateTierTax(custChargeDetails.getDiscount(), charge.getTax(), charge, debitDocDetails);
        debitDocDetails.setTax(debitDocDetails.getTax());
        debitDocDetails.setDiscount(debitDocDetails.getDiscount());
        debitDocDetails.setTotalamount(debitDocDetails.getSubtotal() - debitDocDetails.getDiscount() + debitDocDetails.getTax());

        return debitDocDetails;
    }

}
