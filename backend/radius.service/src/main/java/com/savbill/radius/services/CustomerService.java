package com.savbill.radius.services;

import com.savbill.radius.aaa.data.CustomerCreateData;
import com.savbill.radius.aaa.data.CustomerData;
import com.savbill.radius.dto.LogoutCustomerDTO;
import com.savbill.radius.dto.PageableResponse;
import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.*;
import com.savbill.radius.helper.*;
import com.savbill.radius.entity.*;
import com.savbill.radius.helper.*;
import com.savbill.radius.kafka.CustomMessage;
import com.savbill.radius.kafka.message.*;
import com.savbill.radius.kafka.message.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CustomerService {
    Customer findCustomerById(Long id, Integer mvnoId);

    Customers findCustomersByid(Integer id, Integer mvnoId);

    Customer findCustomerByName(String name, Integer mvnoId);

    //List<Customers> findCustomersByName(String name , Integer mvnoId);
    List<Customer> searchCustomerByName(String name, Integer mvnoId);

    Page<Customers> searchCustomersByName(String name, Integer mvnoId, PaginationDTO paginationDTO);

    List<Customer> findAllCustomer(Integer mvnoId);

    Page<Customers> findAllCustomers(Integer mvnoId, Integer staffId, PaginationDTO paginationDTO);

    Customer addCustomer(CustomerDto customerDto, Integer mvnoId);

    Customer updateCustomer(UpdateCustomerDto customer, Integer mvnoId);

    Customers updateCustomers(CustomerCreateData customer, Integer mvnoId, Boolean netconf);

    void deleteCustomer(String userName, Integer mvnoId);

    void changePassword(CustomerPasswordDto passwordDto, Integer mvnoId);

    String updateCustomerStatus(String userName, String status, Integer mvnoId);

    String updateCustomerStatus(Integer custId, String status, String remarks, Boolean netconf, String username);

    //void rechargeQuota(Long custId,Customer customer,Long mvnoId);
    void rechargeQuota(Long custId, Customer customer, Boolean allowCrossRecharge, Long mvnoId);

    Customer addWifiCustomer(CustomMessage message);

    Customer updateWifiCustomer(CustomMessage message);

    void deleteWifiCustomer(CustomMessage message);

    void updateWifiCustomerPassword(CustomMessage message);

    void updateBSSCustomerPassword(CustomMessage message);

    void updateWifiCustomerStatus(CustomMessage message);

    Customer validateLoginUser(LoginDto loginDto, Integer mvnoId);

    void validateLogoutUser(String userName);

    void updateWifiCustomerQuota(CustomMessage message);

    Customers updateCustomerEndDate(String endDate, String name, Long mvnoId);

    void updateCustomerMacFromApiGTW(CustMacMappingMessage message);

    void terminateUserSession(List<TerminateUser> userList, Integer mvnoId);


    void CoADMSupport(List<changeUserData> userList, String type, CustomerData custRetrunData, String operation);

    Map<String, Object> logoutCustomer(List<changeUserData> userList, LogoutCustomerDTO logoutCustomerDTO, CustomerData custRetrunData, String operation);

    void deleteCustomerMACFromApigateway(CustMacMappingMessage message);

    List<CustPlanMappping> findActiveByCustid(Integer id, LocalDateTime now);

    List<CustPlanMappping> findExpiredByCustid(Integer id, LocalDateTime now);

    List<CustPlanMappping> findFutureByCustid(Integer id, LocalDateTime now);

    void sendCustQuotaDetailToApigw(Integer cprid, Double percentagequotaConsumed, Double totalQuota, Double usedQuota);

    void sendReservedQuotaUpdateToAPIGateway(Integer cprid, boolean isChunkAvailable, double totalResrvedQuota);

    void sendCustQuotaIntrimDetailToApigw(Integer cprid, Double currentsessionusagetime, Double currentsessionusagevolume);

    PageableResponse<Customers> findAllCustomerBySearch(Integer mvnoId, PaginationDTO paginationDTO, CustomerSearch customerSearch);

    void updateCustomerdata(UpdateCustomerShareDataMessage message);

    void updateCustomerConcurrency(UpdateCustomerDto customer);

    String checkConcurrencyByCompare(String userName, String macAddress, Integer mvnoId);

    CustomerCreateData addNewCustomers(CustomerCreateData customerDto, Integer mvnoId, Boolean netconf);

    boolean customerUserNameExists(String userName, Integer mvnoId);

    void deleteCustomers(Integer custid, Integer mvnoId, Boolean netconf);

    void deleteCustomers(DeleteCustomerMessage message, Integer mvnoId, Boolean netconf);

    CustomerCreateData defaultLeaseIPv4provision(CustomerCreateData customerCreateData, Boolean netconf);

    Customers defaultLeaseIPv4Update(CustomerCreateData customer, String oldUsername, Boolean netconf);

    void defoultDeprovision(String username, String gatewayIP, Boolean netconf);

    void customerDeactivationWhenMvnoIsInActive(MvnoStatusMessage mvnoStatusMessage);


    void customerIpMappingUpdate(CustIPMessage custIPMessage);

    void customerIpMappingSave(CustIPMessage custIPMessage);

    void customerIpMappingDelete(CustIPMessage dataMessage);

    List<CustQuotaDetails> findAllByCustomersId(Integer id);

    Boolean updateCustQuotaDetails(Integer custId, Long cprId);

    PostpaidPlan findByPlanId(Integer planId);

    void triggerCOADMForSingleLiveSession(LiveUser liveuser, String event, String type, CustomerData custRetrunData);

    void terminatSessionAfterCustomerStatusChange(Customers customer, String original_username);

    String updateCustomerStatusSoap(Integer custId, String status, String nasportId, String password, String remarks, Boolean netconf, String username);

}
