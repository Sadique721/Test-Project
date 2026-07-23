package com.savbill.taskmanagement.core.modules.CustomerAddress.Service;


import com.savbill.taskmanagement.core.modules.Area.repository.AreaRepository;
import com.savbill.taskmanagement.core.modules.City.service.CityService;
import com.savbill.taskmanagement.core.modules.ClientServ.service.ClientServiceSrv;
import com.savbill.taskmanagement.core.modules.Country.service.CountryService;
import com.savbill.taskmanagement.core.modules.CustomerAddress.domain.CustomerAddress;
import com.savbill.taskmanagement.core.modules.CustomerAddress.domain.QCustomerAddress;
import com.savbill.taskmanagement.core.modules.CustomerAddress.dto.CustomerAddressPojo;
import com.savbill.taskmanagement.core.modules.CustomerAddress.repository.CustomerAddressRepository;
import com.savbill.taskmanagement.core.modules.Customers.Service.CustomersService;
import com.savbill.taskmanagement.core.modules.Customers.domain.Customers;
import com.savbill.taskmanagement.core.modules.Pincode.repository.PincodeRepository;
import com.savbill.taskmanagement.core.modules.ServiceArea.service.ServiceAreaService;
import com.savbill.taskmanagement.core.modules.State.service.StateService;
import com.savbill.taskmanagement.core.modules.Teams.service.HierarchyService;
import com.savbill.taskmanagement.core.modules.staffuser.repository.StaffUserRepository;
import com.savbill.taskmanagement.core.modules.staffuser.service.StaffUserService;
import com.savbill.taskmanagement.core.modules.workflowaudit.service.WorkflowAuditService;
import com.savbill.taskmanagement.core.service.AbstractService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerAddressService extends AbstractService<CustomerAddress, CustomerAddressPojo, Integer> {



    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    private CustomerAddressRepository entityRepository;

    @Autowired
    private CustomersService custService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private StateService stateService;

    @Autowired
    private CityService cityService;


    @Autowired
    private ServiceAreaService serviceAreaService;


    @Autowired
    private PincodeRepository pincodeRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    HierarchyService hierarchyService;

    @Autowired
    StaffUserService staffUserService;

    @Autowired
    WorkflowAuditService workflowAuditService;



    @Autowired
    CustomerAddressRepository customerAddressRepository;




    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.CustomerAddress', '1')")
    public Page<CustomerAddress> searchByCustomer(Customers cust, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return entityRepository.findByCustomer(cust, pageRequest);
    }

    @Override
    protected JpaRepository<CustomerAddress, Integer> getRepository() {
        return entityRepository;
    }

    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.CustomerAddress', '1')")
    public List<CustomerAddress> findAllByCustomers(Customers customer) {
        return entityRepository.findAllByCustomer(customer);
    }

    public CustomerAddress findByAddressTypeAndCustomer(String addressType, Customers customer, String version) {
        return entityRepository.findByAddressTypeAndCustomerAndVersion(addressType, customer, version);
    }

    public CustomerAddress findByAddressTypeAndCustomer(String addressType, Customers customer) {
        return entityRepository.findByAddressTypeAndCustomerAndVersion(addressType, customer, "NEW");
    }



    public CustomerAddress convertCustomerAddressPojoToCustomerAddressModel(CustomerAddressPojo customerAddressPojo) throws Exception {
        CustomerAddress customerAddress = null;
        if (customerAddressPojo != null) {
            customerAddress = new CustomerAddress();
            if (customerAddressPojo.getId() != null) {
                customerAddress.setId(customerAddressPojo.getId());
            }
            customerAddress.setLandmark(customerAddressPojo.getLandmark());
            customerAddress.setLandmark1(customerAddressPojo.getLandmark1());
            customerAddress.setAddress1(customerAddressPojo.getAddress1());
            customerAddress.setAddress2(customerAddressPojo.getAddress2());
            customerAddress.setAddressType(customerAddressPojo.getAddressType());
            customerAddress.setCityId(customerAddressPojo.getCityId());
            customerAddress.setStateId(customerAddressPojo.getStateId());
            customerAddress.setCountryId(customerAddressPojo.getCountryId());
            customerAddress.setPincodeId(customerAddressPojo.getPincodeId());
            customerAddress.setPincode(pincodeRepository.getOne(customerAddressPojo.getPincodeId().longValue()));
            customerAddress.setAreaId(customerAddressPojo.getAreaId());
            customerAddress.setArea(areaRepository.getOne(customerAddressPojo.getAreaId().longValue()));
            customerAddress.setFullAddress(customerAddressPojo.getFullAddress());
            customerAddress.setCity(cityService.get(customerAddressPojo.getCityId()));
            customerAddress.setState(stateService.get(customerAddressPojo.getStateId()));
            customerAddress.setCountry(countryService.get(customerAddressPojo.getCountryId()));
            customerAddress.setIsDelete(customerAddressPojo.getIsDelete());
            customerAddress.setNextTeamHierarchyMappingId(customerAddressPojo.getNextTeamHierarchyMappingId());
            customerAddress.setNextStaff(customerAddressPojo.getNextStaff());
            customerAddress.setStatus(customerAddressPojo.getStatus());
            customerAddress.setVersion(customerAddressPojo.getVersion());
            customerAddress.setShiftId(customerAddressPojo.getShiftId());
            // customerAddress.setShiftedPartnerId(customerAddressPojo.getShiftedPartnerId());
            // customerAddress.setShitedServiceAreaId(customerAddressPojo.getShiftedServiceAreaId());

            if (null != customerAddressPojo.getCustomerId())
                customerAddress.setCustomer(custService.get(customerAddressPojo.getCustomerId()));
        }
        return customerAddress;
    }

    public CustomerAddressPojo convertCustomerAddressModelToCustomerAddressPojo(CustomerAddress customerAddress) throws Exception {
        CustomerAddressPojo pojo = null;
        if (customerAddress != null) {
            pojo = new CustomerAddressPojo();
            pojo.setId(customerAddress.getId());
            pojo.setLandmark(customerAddress.getLandmark());
            pojo.setLandmark1(customerAddress.getLandmark1());
            pojo.setAddress1(customerAddress.getAddress1());
            pojo.setAddress2(customerAddress.getAddress2());
            pojo.setAddressType(customerAddress.getAddressType());
            pojo.setCityId(customerAddress.getCityId());
            pojo.setStateId(customerAddress.getStateId());
            pojo.setCountryId(customerAddress.getCountryId());
            pojo.setPincodeId(customerAddress.getPincodeId());
            pojo.setAreaId(customerAddress.getAreaId());
            pojo.setFullAddress(customerAddress.getFullAddress());
            pojo.setCustomerId(customerAddress.getCustomer().getId());
            pojo.setIsDelete(customerAddress.getIsDelete());
            pojo.setNextTeamHierarchyMappingId(customerAddress.getNextTeamHierarchyMappingId());
            pojo.setNextStaff(customerAddress.getNextStaff());
            pojo.setStatus(customerAddress.getStatus());
            pojo.setVersion(customerAddress.getVersion());
            pojo.setShiftId(customerAddress.getShiftId());
            pojo.setRequestedDate(customerAddress.getRequestedDate());
            pojo.setRequestedByName(customerAddress.getRequestedByName());
        }
        return pojo;
    }

    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.CustomerAddress', '1')")
    public List<CustomerAddressPojo> convertResponseModelIntoPojo(List<CustomerAddress> customerAddressList) throws Exception {
        List<CustomerAddressPojo> pojoListRes = new ArrayList<CustomerAddressPojo>();
        if (customerAddressList != null && customerAddressList.size() > 0) {
            for (CustomerAddress customerAddress : customerAddressList) {
                if (customerAddress.getVersion().equalsIgnoreCase("NEW")) {
                    pojoListRes.add(convertCustomerAddressModelToCustomerAddressPojo(customerAddress));
                }
            }
        }
        return pojoListRes;
    }

    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.CustomerAddress', '1')")
    public List<CustomerAddress> convertPojoListIntoResponseModelList(List<CustomerAddressPojo> customerAddressList) throws Exception {
        List<CustomerAddress> pojoListRes = new ArrayList<CustomerAddress>();
        if (customerAddressList != null && customerAddressList.size() > 0) {
            for (CustomerAddressPojo customerAddress : customerAddressList) {
                if (customerAddress.getVersion().equalsIgnoreCase("NEW")) {
                    pojoListRes.add(convertCustomerAddressPojoToCustomerAddressModel(customerAddress));
                }
            }
        }
        return pojoListRes;
    }







    public CustomerAddress findByAddressTypeAndCustomerId(String addressType, Integer customerId) {
        QCustomerAddress qCustomerAddress = QCustomerAddress.customerAddress;
        BooleanExpression booleanExpression = qCustomerAddress.isNotNull().and(qCustomerAddress.addressType.eq(addressType)).and(qCustomerAddress.customer.id.eq(customerId));
        booleanExpression = booleanExpression.and(qCustomerAddress.version.equalsIgnoreCase("NEW"));
        return entityRepository.findOne(booleanExpression).orElse(null);
    }



}
