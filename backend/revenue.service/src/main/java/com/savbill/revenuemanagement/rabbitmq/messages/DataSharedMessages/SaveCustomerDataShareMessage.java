package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages;
import com.savbill.revenuemanagement.core.dto.invoice.RecordPaymentPojo;
import com.savbill.revenuemanagement.core.entity.customers.*;

import com.savbill.revenuemanagement.core.entity.customers.*;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CustomerLedger;
import com.savbill.revenuemanagement.core.entity.ladger.CustomerLedgerDtls;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.CustPlanMapppingPojo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaveCustomerDataShareMessage {
    private Integer id;
    private String title;

    private String accountNumber;
    private String contactperson;

    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String custname;
    private String createdByName;
    private String email;
    private String mobile;
    private String countryCode;
    private Integer serviceAreaId;
    private Integer networkdevicesId;
    private String status;
    private String custtype;
    private String phone;
    private Integer mvnoId;
    private Long buId;
    private Integer lcoId;
    private Boolean is_from_pwc;
    private Boolean isDeleted;
    private Long oltslotid;
    private Long oltportid;
    private String fullName;
    private Integer parnterId;
    private String planPurchaseType;
    private String serviceAreaName;
    private String partnerName;
    private String calendarType;
    private String dunningCategory;
    private String parentCustUsername;
    private Integer parentCustId;
    private String feasibilityRequired;
    private Long popId;
    private Long oltId;
    private Long masterdbid;
    private Long splitterid;
    private String framedIp;
    private String framedIpBind;
    private String ipPoolNameBind;
    private String nasPort;
    private String valleyType;
    private String customerArea;
    private String custcategory;
    private Integer createdById;
    private Integer lastModifiedById;
    private String lastModifiedByName;
    private String serialNumber;
    private Integer serviceId;
    private List<CustPlanMapppingPojo> custPlanMapppingList = new ArrayList<>();
    private List<CustomerServiceMapping> customerServiceMappingList = new ArrayList<>();
    CustPlanMapppingPojo custPlanMappping = new CustPlanMapppingPojo();
    CustomerServiceMapping customerServiceMapping = new CustomerServiceMapping();
    List<CustomerLedgerDtls> customerLedgerDtlsList=new ArrayList<>();

    private  String nextbilldate;
    private List<CustomerAddress> address;
    private  List<CustChargeDetails> indicustChargeDetails;
    private  List<CustChargeDetails> overChargeList;
    List<CustPlanMappping>custPlanMapppingdomainList=new ArrayList<>();
    private List<DebitDocument> debitDocument;
    private CustomerLedger customerLedgerList;
    private  List<CustomerChargeHistory> customerChargeHistories;
    private  List<CustomerDocDetails> customerDocDetails;

    private String nextBillDateString;

    private RecordPaymentPojo recordPaymentPojo;

    private String lastBillDateString;
    @JsonIgnore
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private Boolean istrialplan;

    private Integer billDay;
    private Integer refMvno;

    private Boolean isCaptiveportal;

    private String referenceNo;
    private Integer earlybilldays;// days for substracting from nextBIlldate
    private Integer earlybillday;// this day to set similar to bill day

    private LocalDate earlybilldate;
    private String pan;
    private String blockNo;

    private String drivingLicence;

    private String customerVrn;

    private String passportNo;

    private String customerNid;

    private Integer renewPlanLimit;
    private Integer graceDay;

    private Integer departmentId;
    private String currency;
    private boolean billDayUpdated;
    private Integer previousBillday;


    //DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public SaveCustomerDataShareMessage(Customers customers, List<CustPlanMappping> custPlanMapppingList, List<CustomerServiceMapping> customerServiceMappingList) {
        id = customers.getId();
        title = customers.getTitle();
        nextbilldate=customers.getNextBillDate().toString();
        //   customerChargeHistories=customers.getCustomerChargeHistories();
//        customerDocDetails=customers.getCustDocList();

//        customerLedgerList=customers.getCustLeger();

        //address=customers.getAddressList();
        username = customers.getUsername();
        password = customers.getPassword();
        firstname = customers.getFirstname();
        lastname = customers.getLastname();
        custname = customers.getCustname();
        email = customers.getEmail();
        mobile = customers.getMobile();
        debitDocument=customers.getDebitDocList();
        countryCode = customers.getCountryCode();
  //      serviceAreaId = customers.getServicearea() != null ? Math.toIntExact(customers.getServicearea().getId()) : null;
  //      networkdevicesId = customers.getNetworkdevices() != null ? Math.toIntExact(customers.getNetworkdevices().getId()) : null;
        status = customers.getStatus();
        custtype = customers.getCusttype();
        phone = customers.getPhone();
        mvnoId = customers.getMvnoId();
        buId = customers.getBuId();
        lcoId = customers.getLcoId();
        is_from_pwc = customers.getIs_from_pwc();
        isDeleted = customers.getIsDeleted();
        oltportid = customers.getOltportid();
        oltslotid = customers.getOltslotid();
        fullName = customers.getFullName();
        indicustChargeDetails=customers.getIndiChargeList();
        overChargeList= customers.getOverChargeList();
 //       parnterId = customers.getPartner() != null ? customers.getPartner().getId() : null;
        calendarType = customers.getCalendarType();
        dunningCategory = customers.getDunningCategory();
        parentCustUsername = customers.getParentCustomers() != null ? customers.getParentCustomers().getUsername() : null;
        parentCustId = customers.getParentCustomers() != null ? customers.getParentCustomers().getId() : null;
        feasibilityRequired = customers.getFeasibilityRequired();
        valleyType = customers.getValleyType();
        customerArea = customers.getCustomerArea();
        custcategory = customers.getCustcategory();
        createdById = customers.getCreatedById();
        lastModifiedById = customers.getLastModifiedById();
        popId = customers.getPopid();
        masterdbid = customers.getMasterdbid();
        splitterid = customers.getSplitterid();
        oltId = customers.getOltid();
        framedIp = customers.getFramedIp();
        ipPoolNameBind = customers.getIpPoolNameBind();
        nasPort = customers.getNasPort();
        framedIpBind = customers.getFramedIpBind();
        istrialplan = customers.getIstrialplan();
        //  custPlanMapppingdomainList=custPlanMapppingList;
        for (CustPlanMappping planMappping : custPlanMapppingList) {

            this.custPlanMappping.setId(planMappping.getId());
            this.custPlanMappping.setCustid(planMappping.getCustomer().getId());
            this.custPlanMappping.setPlanId(planMappping.getPlanId());
            this.custPlanMappping.setBillTo(planMappping.getBillTo());
            this.custPlanMappping.setIsInvoiceToOrg(planMappping.getIsInvoiceToOrg());
            this.custPlanMappping.setStartDate(LocalDateTime.parse(planMappping.getStartDateString(), formatter));
            this.custPlanMappping.setEndDate(LocalDateTime.parse(planMappping.getEndDateString(), formatter));
            this.custPlanMappping.setExpiryDate(LocalDateTime.parse(planMappping.getExpiryDateString(), formatter));
            this.custPlanMappping.setService(planMappping.getService());
            this.custPlanMappping.setBillableCustomerId(planMappping.getBillableCustomerId());
            if (planMappping.getPlanGroup() != null) {
                custPlanMappping.setPlangroupid(planMappping.getPlanGroup().getPlanGroupId());
            }
            this.custPlanMappping.setStatus(planMappping.getStatus());
            this.custPlanMappping.setCustPlanStatus(planMappping.getCustPlanStatus());
            this.custPlanMappping.setCustServiceMappingId(planMappping.getCustServiceMappingId());
            this.custPlanMappping.setIsDelete(planMappping.getIsDelete());
      //      this.custPlanMappping.setSerialNumber(planMappping.getSerialNumber());

            this.custPlanMapppingList.add(this.custPlanMappping);
        }

        for (CustomerServiceMapping serviceMapping : customerServiceMappingList) {
            this.customerServiceMapping.setId(serviceMapping.getId());
            this.customerServiceMapping.setServiceId(serviceMapping.getServiceId());
            this.customerServiceMapping.setCustId(serviceMapping.getCustId());
            this.customerServiceMapping.setConnectionNo(serviceMapping.getConnectionNo());
            this.customerServiceMapping.setPartner(serviceMapping.getPartner());
            this.customerServiceMapping.setPop(serviceMapping.getPop());
            this.customerServiceMapping.setStaticOrPooledIP(serviceMapping.getStaticOrPooledIP());
            this.customerServiceMapping.setIsDelete(serviceMapping.getIsDelete());
            this.customerServiceMapping.setCreatedById(serviceMapping.getCreatedById());
            this.customerServiceMapping.setLastModifiedById(serviceMapping.getLastModifiedById());
            this.customerServiceMapping.setStatus(serviceMapping.getStatus());
            this.customerServiceMapping.setMvnoId(serviceMapping.getMvnoId());
            this.customerServiceMapping.setBuId(serviceMapping.getBuId());
            this.customerServiceMappingList.add(this.customerServiceMapping);
        }
//        for(CustomerLedgerDtls customerLedgerDtls:customers.getLedgerDtls()){
//            customerLedgerDtls.setCustId(customerLedgerDtls.getCustomer().getId());
//            customerLedgerDtls.setCustomer(null);
//            customerLedgerDtlsList.add(customerLedgerDtls);
//
//        }
    }

}
