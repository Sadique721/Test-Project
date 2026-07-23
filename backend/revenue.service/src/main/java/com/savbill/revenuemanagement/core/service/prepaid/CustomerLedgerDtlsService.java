package com.savbill.revenuemanagement.core.service.prepaid;


import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.constants.SubscriberConstants;
import com.savbill.revenuemanagement.core.entity.customers.*;
import com.savbill.revenuemanagement.core.entity.customers.*;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CustomerLedgerDtls;
import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
import com.savbill.revenuemanagement.core.repository.customer.*;
import com.savbill.revenuemanagement.core.repository.customer.*;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CustomerLedgerDtlsRepository;
import com.savbill.revenuemanagement.core.service.AbstractService;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostpaidPlanRepo;
import com.savbill.revenuemanagement.productmanagement.PlanService.domain.Services;
import com.savbill.revenuemanagement.productmanagement.PlanService.repository.ServiceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerLedgerDtlsService extends AbstractService<CustomerLedgerDtls, CustomerLedgerDtlsPojo, Integer> {

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private CustomerLedgerDtlsRepository entityRepository;

    @Autowired
    private CustomerLedgerDtlsMapper customerLedgerDtlsMapper;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;
    
    @Autowired
    private  CustomerLedgerDtlsService customerLedgerDtlsService;

    @Autowired
    CustomerDBRRepository customerDBRRepository;
    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private CustomerAddressRepository customerAddressRepository;

    @Autowired
    private CustPlanMapppingRepository custPlanMapppingRepository;

    //    @Autowired
//    private TrialCustomerLedgerDtlRepo trialCustomerLedgerDtlRepo;
    @Override
    protected JpaRepository<CustomerLedgerDtls, Integer> getRepository() {
        return entityRepository;
    }


    public List<CustomerLedgerDtls> getAllEntities(Integer pageNumber, int pageSize) {
//    	PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return entityRepository.findAll();
    }

    public CustomerLedgerInfoPojo getByTime(CustomerLedgerDtlsPojo pojo) {
        DecimalFormat df = new DecimalFormat("#.##");
        Customers customers = customersRepository.findCustomerById(pojo.getCustId());
        Boolean isCaf = false;
        if (customers != null) {
            isCaf = customers.getStatus().equalsIgnoreCase("NewActivation");
        }
        CustomerLedgerInfoPojo infoPojo = new CustomerLedgerInfoPojo();
        Double OpeningAmount = null;
//        if (isCaf){
//            OpeningAmount=trialCustomerLedgerDtlRepo.findOpeningAmount(pojo.getCREATE_DATE(), pojo.getCustId());
//        }else {
        OpeningAmount = entityRepository.findOpeningAmount(pojo.getCREATE_DATE(), pojo.getCustId());
        //   }
        if (OpeningAmount == null) {
            OpeningAmount = 0.0;
        }
        infoPojo.setOpeningAmount(OpeningAmount);
        Double bal = 0.0;
        List<CustomerLedgerDtls> customerLedgerDtlsList = null;
        //   List<TrialCustomerLedgerDtls> trialCustomerLedgerDtls=null;
        if (pojo.getCREATE_DATE() != null && pojo.getEND_DATE() != null) {
//            if (isCaf){
//                trialCustomerLedgerDtls= trialCustomerLedgerDtlRepo.findAllByCREATE_DATEAndEndDateAndCustomerIdAndIsDelete(pojo.getCREATE_DATE(), pojo.getEND_DATE(), pojo.getCustId(), false);
//                customerLedgerDtlsList=trialToCust(trialCustomerLedgerDtls);
//            }else {
            customerLedgerDtlsList = entityRepository.findAllByCREATE_DATEAndEndDateAndCustomerIdAndIsDelete(pojo.getCREATE_DATE(), pojo.getEND_DATE(), pojo.getCustId(), false);
            //   }
        }
        if (pojo.getCREATE_DATE() == null && pojo.getEND_DATE() == null) {
//            if (isCaf){
//                trialCustomerLedgerDtls = trialCustomerLedgerDtlRepo.findByCustomerIdAndIsDelete(pojo.getCustId(), false);
//                customerLedgerDtlsList=trialToCust(trialCustomerLedgerDtls);
//            }else {
            customerLedgerDtlsList = entityRepository.findByCustomerIdAndIsDelete(pojo.getCustId(), false);
            //   }
        }
        if (!customerLedgerDtlsList.isEmpty()) {
            List<Integer> ids = customerLedgerDtlsList.stream().map(CustomerLedgerDtls::getDebitdocid).filter(Objects::nonNull).collect(Collectors.toList());
            List<DebitDocument> invoiceList = findDebitDocByStatus("VOID", ids);
            List<Integer> ids1 = new ArrayList<>();
            for (DebitDocument debitDocument1 : invoiceList) {
                ids1.add(debitDocument1.getId());
            }
            if (invoiceList.size() > 0) {
                customerLedgerDtlsList.removeIf(l -> !ids1.contains(l.getDebitdocid()) && l.getTranstype().equalsIgnoreCase("DR") && l.getDebitdocid() != null);
            }
        }
        if (customerLedgerDtlsList != null) {
            for (int i = 0; i < customerLedgerDtlsList.size(); i++) {
                if (customerLedgerDtlsList.get(i).getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_CREDIT)) {
                    bal += OpeningAmount - customerLedgerDtlsList.get(i).getAmount();
                }
                if (customerLedgerDtlsList.get(i).getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_DEBIT)) {
                    bal += OpeningAmount + customerLedgerDtlsList.get(i).getAmount();
                }
                customerLedgerDtlsList.get(i).setBalAmount(Double.parseDouble(df.format(bal)));
                customerLedgerDtlsList.get(i).setAmount(Double.parseDouble(df.format(customerLedgerDtlsList.get(i).getAmount())));
            }
        }
//        List<CustomerLedgerDtls> sortedList = customerLedgerDtlsList.stream().sorted(Comparator.comparing(CustomerLedgerDtls::getId).reversed()).collect(Collectors.toList());
        infoPojo.setDebitCreditDetail(convertResponseModelIntoPojo(customerLedgerDtlsList));
        Double ClosingAmount = 0.0;
        if (pojo.getCREATE_DATE() != null && pojo.getEND_DATE() != null) {
//            if (isCaf){
//                ClosingAmount = trialCustomerLedgerDtlRepo.findClsoingAmount(pojo.getCREATE_DATE(), pojo.getEND_DATE(), pojo.getCustId());
//            }else {
            ClosingAmount = entityRepository.findClsoingAmount(pojo.getCREATE_DATE(), pojo.getEND_DATE(), pojo.getCustId());
            //  }
        }
        if (pojo.getCREATE_DATE() == null || pojo.getEND_DATE() == null) {
//            if (isCaf){
//                ClosingAmount = trialCustomerLedgerDtlRepo.findClsoingAmountById(pojo.getCustId());
//            }else {
            ClosingAmount = entityRepository.findClsoingAmountById(pojo.getCustId());
            //}
        }

        if (ClosingAmount == null) {
            ClosingAmount = 0.0;
        }
        Double Balance = OpeningAmount + ClosingAmount;
        infoPojo.setClosingBalance(Double.parseDouble(df.format(Balance)));
        return infoPojo;
    }


    public List<CustomerLedgerDtlsPojo> convertResponseModelIntoPojo(List<CustomerLedgerDtls> customerLedgerDtls) {
        return customerLedgerDtls.stream().map(data -> customerLedgerDtlsMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public CustomerLedgerAllInfoPojo custInfoBytime(Integer custId, CustomerLedgerInfoPojo pojo) {
        CustomerLedgerAllInfoPojo custPojo = new CustomerLedgerAllInfoPojo();
        Customers customers = customersRepository.findCustomerById(custId);
        if (customers != null) {
            custPojo.setCustId(customers.getId());
            custPojo.setCustname(customers.getCustname());
            custPojo.setUsername(customers.getUsername());
            String address = null;
            List<CustomerAddress> customerAddress = customerAddressRepository.findAllByCustomer_Id(custId);
            //   for (int i = 0; i < customerAddress.size(); i++) {
            if (customerAddress != null && !customerAddress.isEmpty() && customerAddress.get(0).getAddressType().equalsIgnoreCase(SubscriberConstants.CUST_ADDRESS_PRESENT)) {
                address = customerAddress.get(0).getFullAddress();

            }
            custPojo.setAddress(address);
            String plan = null;
            List<CustPlanMappping> custPlanMapppingList = custPlanMapppingRepository.findAllByCustomerId(customers.getId());
            for (int i = 0; i < custPlanMapppingList.size(); i++) {
                if (custPlanMapppingList.get(i).getService().equalsIgnoreCase(SubscriberConstants.SERVICE_DATA)) {
                    Integer planId = customers.getPlanMappingList().get(i).getPlanId();
                    plan = postpaidPlanRepo.getOne(planId).getDisplayName();
                }
            }
            custPojo.setPlan(plan);
            custPojo.setStatus(customers.getStatus());

            custPojo.setCustomerLedgerInfoPojo(pojo);
            return custPojo;
        } else {
            return new CustomerLedgerAllInfoPojo();
        }
    }

    public List<DebitDocument> findDebitDocByStatus(String status, List<Integer> ids) {
//        QDebitDocument qDebitDocument = QDebitDocument.debitDocument;
//        BooleanExpression exp = qDebitDocument.isNotNull();
//        exp = exp.and(qDebitDocument.billrunstatus.notEqualsIgnoreCase(status));
//        if (!CollectionUtils.isEmpty(ids)) exp = exp.and(qDebitDocument.id.in(ids));
        return debitDocRepository.findAllByDebiidocidAndBillrunStatusNotEquals(ids, status);
    }


    public List<CustomerDBRPojo> getbycustid(LocalDate startdate, Long custid) {
        List<CustomerDBR> customerDBRS = customerDBRRepository.getbyCustid(startdate, custid);
        List<CustomerDBRPojo> response = new ArrayList<>();
        if (customerDBRS != null && !customerDBRS.isEmpty()) {
            customerDBRS.stream().forEach(x -> {
                CustomerDBRPojo pojo = new CustomerDBRPojo();
                pojo.setDbr(x.getDbr());
                pojo.setMonth(startdate.getDayOfMonth() + "");
                pojo.setDate(startdate);
                pojo.setPendingamt(x.getPendingamt());
                pojo.setCumm_revenue(x.getCumm_revenue());
                pojo.setIsContainsMultipleService(false);
                pojo.setStartdate(startdate);
                pojo.setServiceName(getServiceNameById(x.getServiceId()));
                response.add(pojo);
            });
        }
        return response;
    }

    public String getServiceNameById(Long serviceId) {
        Optional<Services> services = serviceRepository.findById(serviceId);
        if (services.isPresent())
            return services.get().getServiceName();
        else
            return "";
    }

    public CustomerDBRResponse getbycustid(LocalDate startdate, LocalDate endate, Long custid) {
        List<CustomerDBR> customerDBRS = customerDBRRepository.getbyCustid(startdate, endate, custid);
        List<CustomerDBR> outstandingCustomerDBRS = customerDBRRepository.getbyCustid1(startdate.minusDays(1), custid);
        CustomerDBRResponse responseData = new CustomerDBRResponse();
        DecimalFormat df = new DecimalFormat("0.00");

        if (outstandingCustomerDBRS != null && !outstandingCustomerDBRS.isEmpty()) {
            Double outstandingPending = outstandingCustomerDBRS.stream().mapToDouble(x -> x.getPendingamt()).sum();
            Double outstandingDBR = outstandingCustomerDBRS.stream().mapToDouble(x -> x.getDbr()).sum();
            Double outstandingRevenue = outstandingCustomerDBRS.stream().mapToDouble(x -> x.getCumm_revenue()).sum();

            responseData.setOutstandingPending(Double.parseDouble(df.format(outstandingCustomerDBRS.get(outstandingCustomerDBRS.size() - 1).getPendingamt())));
            responseData.setOutstandingDbr(Double.parseDouble(df.format(outstandingDBR)));
            responseData.setOutstandingRevenue(Double.parseDouble(df.format(outstandingDBR)));
        } else {
            responseData.setOutstandingPending(0.0d);
            responseData.setOutstandingDbr(0.0d);
            responseData.setOutstandingRevenue(0.0d);
        }

        Double cummrevenue = 0d;
        List<CustomerDBRPojo> response = new ArrayList<>();
        if (customerDBRS != null && !customerDBRS.isEmpty()) {
            LocalDate dbStartDate = customerDBRS.get(0).getStartdate();
            Double offerPrice = customerDBRS.get(0).getOffer_price();
            LocalDate dbEndDate = customerDBRS.get(customerDBRS.size() - 1).getEnddate().plusMonths(1);
            if (dbEndDate.isAfter(endate))
                endate = dbEndDate;
            LocalDate y = startdate;
            if (dbStartDate.isAfter(startdate))
                y = dbStartDate;
            int pointer = customerDBRS.size();

            while (pointer > 0 && (y.isBefore(endate))) {
                CustomerDBRPojo customerDBRPojo = new CustomerDBRPojo();
                LocalDate finalY = y;
                Double totDBR = customerDBRS.stream().filter(value -> value.getStartdate().equals(finalY)).mapToDouble(CustomerDBR::getDbr).sum();
                Double totPending = customerDBRS.stream().filter(value -> value.getStartdate().equals(finalY)).mapToDouble(CustomerDBR::getPendingamt).sum();
                Double totDBR1 = Double.parseDouble(df.format(totDBR));

                String remarksList = customerDBRS.stream().filter(value -> value.getStartdate().equals(finalY)).map(x -> x.getRemark()).collect(Collectors.joining(" ")).trim();

                Boolean isContainsMultipleService = customerDBRS.stream().filter(value -> value.getStartdate().equals(finalY)).filter(x -> x.getServiceId() != null).mapToInt(x -> x.getServiceId().intValue()).distinct().count() > 1;

                cummrevenue += totDBR;

                customerDBRPojo.setMonth(y.getDayOfMonth() + "-" + y.getMonthValue() + "-" + y.getYear());
                customerDBRPojo.setDate(y);
                customerDBRPojo.setDbr(totDBR1);
                customerDBRPojo.setPendingamt(totPending);
                customerDBRPojo.setRemark(remarksList);
                customerDBRPojo.setIsContainsMultipleService(isContainsMultipleService);

                customerDBRPojo.setCumm_revenue(cummrevenue + responseData.getOutstandingRevenue());
                if (customerDBRPojo.getPendingamt() == 0 && customerDBRPojo.getDbr() == 0) {
                } else
                    response.add(customerDBRPojo);

                y = y.plusDays(Long.parseLong("1"));
            }
            ;
        }

        if (response != null && !response.isEmpty()) {
            Double dbrSum = response.stream().mapToDouble(x -> x.getDbr()).sum();
            Double totalCumm = Double.parseDouble(df.format(response.get(response.size() - 1).getCumm_revenue())) - responseData.getOutstandingRevenue();
            response.get(response.size() - 1).setDbr(response.get(response.size() - 1).getDbr() - (dbrSum - totalCumm));
        }
        responseData.setCustomerDBRPojos(response);
        return responseData;
    }





//    public List<CustomerLedger> getCustomerLeger(Customers customer) {
//        return entityRepository.findByCustomer(customer);
//    }
//
//    public CustomerLedger getCustomerLeger(Integer custId) {
//        return entityRepository.findByCustomerId(custId);
//    }


}
