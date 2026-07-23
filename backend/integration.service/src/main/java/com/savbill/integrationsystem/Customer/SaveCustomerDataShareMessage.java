package com.savbill.integrationsystem.Customer;


import lombok.Data;

@Data
public class SaveCustomerDataShareMessage {
    private Integer id;
    private String title;
    private String username;
    private String password;
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
    private Integer parentCustId;
    private String feasibilityRequired;
    private String valleyType;
    private String customerArea;
    private String custcategory;
    private Integer createdById;
    private Integer lastModifiedById;
    private String serialNumber;
    private Integer serviceId;
    private Integer refMvno;
    private String blockNo;
//    private List<CustPlanMapppingPojo> custPlanMapppingList = new ArrayList<>();
//    private List<CustomerServiceMapping> customerServiceMappingList = new ArrayList<>();
//    CustPlanMapppingPojo custPlanMappping = new CustPlanMapppingPojo();
//    CustomerServiceMapping customerServiceMapping = new CustomerServiceMapping();


//    public SaveCustomerDataShareMessage(Customers customers, List<CustPlanMappping> custPlanMapppingList, List<CustomerServiceMapping> customerServiceMappingList) {
//        id = customers.getId();
//        title = customers.getTitle();
//        username = customers.getUsername();
//        password = customers.getPassword();
//        firstname = customers.getFirstname();
//        lastname = customers.getLastname();
//        custname = customers.getCustname();
//        email = customers.getEmail();
//        mobile = customers.getMobile();
//        countryCode = customers.getCountryCode();
//        serviceAreaId = customers.getServicearea() != null ? Math.toIntExact(customers.getServicearea().getId()) : null;
//        networkdevicesId = customers.getNetworkdevices() != null ? Math.toIntExact(customers.getNetworkdevices().getId()) : null;
//        status = customers.getStatus();
//        custtype = customers.getCusttype();
//        phone = customers.getPhone();
//        mvnoId = customers.getMvnoId();
//        buId = customers.getBuId();
//        lcoId = customers.getLcoId();
//        is_from_pwc = customers.getIs_from_pwc();
//        isDeleted = customers.getIsDeleted();
//        oltportid = customers.getOltportid();
//        oltslotid = customers.getOltslotid();
//        fullName = customers.getFullName();
//        parnterId = customers.getPartner() != null ? customers.getPartner().getId() : null;
//        calendarType = customers.getCalendarType();
//        dunningCategory = customers.getDunningCategory();
//        parentCustUsername = customers.getParentCustomers() != null ? customers.getParentCustomers().getUsername() : null;
//        parentCustId = customers.getParentCustomers() != null ? customers.getParentCustomers().getId() : null;
//        feasibilityRequired = customers.getFeasibilityRequired();
//        valleyType = customers.getValleyType();
//        customerArea = customers.getCustomerArea();
//        custcategory = customers.getCustcategory();
//        createdById = customers.getCreatedById();
//        lastModifiedById = customers.getLastModifiedById();
//
//        for (CustPlanMappping planMappping : custPlanMapppingList) {
//
//            this.custPlanMappping.setId(planMappping.getId());
//            this.custPlanMappping.setCustid(planMappping.getCustomer().getId());
//            this.custPlanMappping.setPlanId(planMappping.getPlanId());
//            this.custPlanMappping.setBillTo(planMappping.getBillTo());
//            this.custPlanMappping.setIsInvoiceToOrg(planMappping.getIsInvoiceToOrg());
//            this.custPlanMappping.setService(planMappping.getService());
//            if (planMappping.getPlanGroup() != null) {
//                custPlanMappping.setPlangroupid(planMappping.getPlanGroup().getPlanGroupId());
//            }
//            this.custPlanMappping.setStatus(planMappping.getStatus());
//            this.custPlanMappping.setCustPlanStatus(planMappping.getCustPlanStatus());
//            this.custPlanMappping.setCustServiceMappingId(planMappping.getCustServiceMappingId());
//            this.custPlanMappping.setIsDelete(planMappping.getIsDelete());
//            this.custPlanMappping.setSerialNumber(planMappping.getSerialNumber());
//            this.custPlanMappping.setSerialNumber(planMappping.getSerialNumber());
//
//            this.custPlanMapppingList.add(this.custPlanMappping);
//        }
//
//        for (CustomerServiceMapping serviceMapping : customerServiceMappingList) {
//            this.customerServiceMapping.setId(serviceMapping.getId());
//            this.customerServiceMapping.setServiceId(serviceMapping.getServiceId());
//            this.customerServiceMapping.setCustId(serviceMapping.getCustId());
//            this.customerServiceMapping.setConnectionNo(serviceMapping.getConnectionNo());
//            this.customerServiceMapping.setPartner(serviceMapping.getPartner());
//            this.customerServiceMapping.setPop(serviceMapping.getPop());
//            this.customerServiceMapping.setIsDelete(serviceMapping.getIsDelete());
//            this.customerServiceMapping.setCreatedById(serviceMapping.getCreatedById());
//            this.customerServiceMapping.setLastModifiedById(serviceMapping.getLastModifiedById());
//            this.customerServiceMapping.setStatus(serviceMapping.getStatus());
//            this.customerServiceMapping.setMvnoId(serviceMapping.getMvnoId());
//            this.customerServiceMapping.setBuId(serviceMapping.getBuId());
//            this.customerServiceMappingList.add(this.customerServiceMapping);
//        }
//    }

}
