package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages;


import com.savbill.revenuemanagement.core.entity.customers.*;
import lombok.Data;

@Data
public class UpdateCustomerShareDataMessage {
    private Integer id;
    private String title;
    private String username;
    private String password;
    private String contactperson;
    private String firstname;
    private String lastname;
    private String custname;
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
    private String feasibilityRequired;
    private String valleyType;
    private String customerArea;
    private String custcategory;
    private Integer parentCustId;
    private String blockNo;
    private String pan;

    private String customerVrn;

    private Integer renewPlanLimit;

    private String customerNid;

    private String passportNo;

    private String drivingLicence;

    private Integer billday;

    private boolean billDayUpdated;

    private Integer previousBillday;

   /* public UpdateCustomerShareDataMessage(Customers customers, List<CustPlanMappping> custPlanMapppingList, List<CustomerServiceMapping> customerServiceMappingList) {
        id = customers.getId();
        title = customers.getTitle();
        nextbilldate=customers.getNextBillDate().toString();
        istrialplan = customers.getIstrialplan();
//        customerDocDetails=customers.getCustDocList();
//        customerLedgerDtlsList=customers.getLedgerDtls();
//     //   customerChargeHistories=customers.getCustomerChargeHistories();
//        customerLedgerList=customers.getCustLeger();
        address=customers.getAddressList();
        username = customers.getUsername();
        password = customers.getPassword();
        firstname = customers.getFirstname();
        lastname = customers.getLastname();
        custname = customers.getCustname();
        email = customers.getEmail();
        mobile = customers.getMobile();
        debitDocument=customers.getDebitDocList();
        countryCode = customers.getCountryCode();
      //  serviceAreaId = customers.getServicearea() != null ? Math.toIntExact(customers.getServicearea().getId()) : null;
//        networkdevicesId = customers.getNetworkdevices() != null ? Math.toIntExact(customers.getNetworkdevices().getId()) : null;
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
        //parnterId = customers.getPartner() != null ? customers.getPartner().getId() : null;
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
        for (CustPlanMappping planMappping : custPlanMapppingList) {

            this.custPlanMappping.setId(planMappping.getId());
            this.custPlanMappping.setCustid(planMappping.getCustomer().getId());
            this.custPlanMappping.setPlanId(planMappping.getPlanId());
            this.custPlanMappping.setBillTo(planMappping.getBillTo());
            this.custPlanMappping.setIsInvoiceToOrg(planMappping.getIsInvoiceToOrg());
            this.custPlanMappping.setService(planMappping.getService());
            this.custPlanMappping.setIstrialplan(planMappping.getIstrialplan());
            if (planMappping.getPlanGroup() != null) {
                custPlanMappping.setPlangroupid(planMappping.getPlanGroup().getPlanGroupId());
            }
            this.custPlanMappping.setStatus(planMappping.getStatus());
            this.custPlanMappping.setCustPlanStatus(planMappping.getCustPlanStatus());
            this.custPlanMappping.setCustServiceMappingId(planMappping.getCustServiceMappingId());
            this.custPlanMappping.setIsDelete(planMappping.getIsDelete());
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.custPlanMappping.setStartDate(new DateTimeUtil().convertDateTimeToDifferenFormat(outputFormatter, planMappping.getStartDateString()));
            this.custPlanMappping.setEndDate(new DateTimeUtil().convertDateTimeToDifferenFormat(outputFormatter, planMappping.getEndDateString()));
            if (planMappping.getExpiryDateString() != null)
                this.custPlanMappping.setExpiryDate(new DateTimeUtil().convertDateTimeToDifferenFormat(outputFormatter, planMappping.getExpiryDateString()));

//            this.custPlanMappping.setSerialNumber(planMappping.getSerialNumber());


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
        }*/
//    }

}
