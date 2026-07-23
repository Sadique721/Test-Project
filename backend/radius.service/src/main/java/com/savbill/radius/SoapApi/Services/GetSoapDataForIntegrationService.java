package com.savbill.radius.SoapApi.Services;

import com.savbill.radius.SoapApi.Dto.*;
import com.savbill.radius.SoapApi.Dto.*;
import com.savbill.radius.aaa.data.CustomerData;
import com.savbill.radius.aaa.db.DBAuthenticationDriver;
import com.savbill.radius.aaa.packet.AccountingRequest;
import com.savbill.radius.aaa.server.RadiusUtility;
import com.savbill.radius.dto.LiveUserSearchDTO;
import com.savbill.radius.dto.LogoutCustomerDTO;
import com.savbill.radius.entity.*;
import com.savbill.radius.entity.*;
import com.savbill.radius.helper.changeUserData;
import com.savbill.radius.kafka.KafkaMessageData;
import com.savbill.radius.kafka.KafkaMessageSender;
import com.savbill.radius.kafka.message.CustomerQuotaInfo;
import com.savbill.radius.repository.*;
import com.savbill.radius.repository.*;
import com.savbill.radius.services.ClientService;
import com.savbill.radius.services.CustomerService;
import com.savbill.radius.services.DeviceService;
import com.savbill.radius.services.LiveUserService;
import com.savbill.radius.services.impl.DeviceServiceImpl;
import com.savbill.radius.utils.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.NoResultException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GetSoapDataForIntegrationService {

    @Autowired
    public CustomersRepository customersRepository;

    @Autowired
    private PostpaidPlanRepository postpaidPlanRepository;
    @Autowired
    private CustQuotaDetailsRepository custQuotaDetailsRepository;

    @Autowired
    private LiveUserRepository liveUserRepository;
    @Autowired
    private ClientService clientService;

    @Autowired
    LiveUserService liverUserService;

    @Autowired
    DeviceService deviceService;
    @Autowired
    CustPlanMappingRepository custPlanMappingRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    DeviceServiceImpl deviceServiceImple;
    @Autowired
    DeviceRepository deviceRepository;
    @Autowired
    private MacAddressMappingRepository macAddressMappingRepository;

    private static final Logger log = LoggerFactory.getLogger(GetSoapDataForIntegrationService.class);

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private CustomerService customerService;

    public GenericDataDTO GetBalanceBYSubscriberId(String SubscriberID) {
        log.debug("In Get Balance CustId: " + SubscriberID);

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Long custid = customersRepository.findIdByUsernameAndMvnoIdAndStatusNotTerminate(SubscriberID, 2l);
        List<GetBalanceDto> custQuotaDetails1 = custQuotaDetailsRepository.findByCustomerId(custid.intValue());
        List<LiveUser> liveUserList = liveUserRepository.findAllByCustid(custid.toString());

        if (!CollectionUtils.isEmpty(custQuotaDetails1)) {
            GetBalanceDto custQuotaDetails = custQuotaDetails1.get(0);
            if (!CollectionUtils.isEmpty(liveUserList) && liveUserList != null) {
                String totalUpload = String.valueOf(
                        liveUserList.stream()
                                .map(LiveUser::getAcctInputOctets)
                                .mapToDouble(Double::parseDouble)
                                .sum());


                String totalDownload = String.valueOf(
                        liveUserList.stream()
                                .map(LiveUser::getAcctOutputOctets)
                                .mapToDouble(Double::parseDouble)
                                .sum());
                custQuotaDetails.setUploadQuota(totalUpload);
                custQuotaDetails.setDownloadQuota(totalDownload);
            } else {
                custQuotaDetails.setUploadQuota("0.0");
                custQuotaDetails.setDownloadQuota("0.0");
            }
            genericDataDTO.setData(custQuotaDetails);
            genericDataDTO.setResponseMessage("SUCCESS");
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
        }else {
            genericDataDTO.setResponseMessage("Failure");
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        return genericDataDTO;

    }

    public GenericDataDTO GetAccountDetails(String username) {
        log.debug("In Get Account Details: " + username);

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<GetAccountDetailsDto> accountDetails = customersRepository.findAccountDetailsByUsername(username);
        Long custId = customersRepository.findIdByUsernameAndMvnoIdAndStatusNotTerminate(username, 2L);
        List<String> macList = macAddressMappingRepository.findMacByCustomerId(custId);
        String callingStationId = String.join(", ", macList);
        if (!accountDetails.isEmpty() && accountDetails != null) {
            log.info("Get Account Details Fetched SuccessFully For Subscriber:{}", username);
            accountDetails.get(0).setCallingStationId(callingStationId);
        } else {
            log.warn("Get Account Details Failed: Account:{} Not Found In System", username);
        }
        genericDataDTO.setDataList(accountDetails);

        return genericDataDTO;

    }

    public GenericDataDTO getLiveUserLoginStatus(String subscriberID, Long mvnoId) {
        log.debug("In Live User Login Status CustId: " + subscriberID);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String vendor = "";
        try {
            List<LiveUser> liveUser = liveUserRepository.findAllByUserName(subscriberID);

            Optional<LiveUser> loginSession = liveUser.stream()
                    .min(Comparator.comparing(LiveUser::getLastmodifiedDate).reversed()); // Get the latest modified date

            if (loginSession.isPresent()) {
                Boolean isKnownUser = checkUserIsKnownOrNot(loginSession.get().getFramedIpAddress(), mvnoId);
                Client client = clientRepository.findByClientIP(loginSession.get().getNasIpAddress());
                vendor = client.getVendor() != null || client.getVendor().isEmpty() ? client.getVendor() : "";

                if (!isKnownUser) {
                    genericDataDTO.setData("UNKNOWN");
                    genericDataDTO.setResponseMessage("IP is available in session table with PARAM_STR9 is preauth : " + vendor);
                    genericDataDTO.setResponseCode(201);
                    return genericDataDTO;
                } else {
                    genericDataDTO.setData(loginSession);
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    genericDataDTO.setResponseMessage("SUCCESS");
                    return genericDataDTO;
                }

            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("IP is available in session table with PARAM_STR9 is preauth : " + vendor);
            }
        } catch (NoSuchElementException e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage("No login session found");
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failure: " + e.getMessage());
        }
        return genericDataDTO;
    }


    public GenericDataDTO GetUserSessionDetails(String ipAddress, Long mvnoId) {
        log.debug("In get User Session Details IpAddress: " + ipAddress);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        LiveUserSearchDTO paginationDTO = new LiveUserSearchDTO();
//        List<GetUserSessionresponseDto> userSession = liveUserRepository.findUserSessionByIpAddress(ipAddress);
        paginationDTO.setFramedIpAddress(ipAddress);
        Page<LiveUser> liveUsersFromIp = liverUserService.findLiveUsersUsingFilter(paginationDTO, Math.toIntExact(mvnoId));
        List<LiveUser> liveUsersList = new ArrayList<>(liveUsersFromIp.getContent());
        Optional<LiveUser> loginSession = null;
        if (liveUsersFromIp != null && !liveUsersFromIp.isEmpty() && liveUsersFromIp.getContent().size() > 0) {
            loginSession = liveUsersList.stream().min(Comparator.comparing(LiveUser::getLastmodifiedDate).reversed());
            GetUserSessionresponseDto data = new GetUserSessionresponseDto();
            data.setAcctSessionId(loginSession.get().getAcctSessionId());
            data.setCdrID(loginSession.get().getCdrID());
            data.setUserName(loginSession.get().getUserName());
            data.setCreatedDate(loginSession.get().getCreatedDate());
            data.setCallingStationId(loginSession.get().getCallingStationId());
            data.setNasPortType(loginSession.get().getNasPortType());
            data.setNasPortId(loginSession.get().getNasPortId());
            data.setFramedIPv6Prefix(loginSession.get().getFramedipv6address());
            data.setFramedIpAddress(loginSession.get().getFramedIpAddress());
            data.setDelegatedIPv6Prefix(loginSession.get().getDelegatedIPv6Prefix());
            ZonedDateTime zonedDateTime = loginSession.get().getCreatedDate().toInstant()
                    .atZone(ZoneId.of("Asia/Kolkata")); // +05:30 timezone
            // Format the ZonedDateTime to the desired format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
            String formattedDate = zonedDateTime.format(formatter);
            data.setCreatedDateString(formattedDate);
            boolean isUserKnown = checkUserIsKnownOrNot(ipAddress, 2L);
            if (isUserKnown) {
                data.setKnownUser(true);
            } else {
                data.setKnownUser(false);
            }

            if (loginSession.get() == null) {
                genericDataDTO.setData(null);
            } else {
                genericDataDTO.setData(data);
            }
        }
        return genericDataDTO;
    }


    public GenericDataDTO getCustomerDetails(String username, Integer mvnoId) {
        log.debug("In get Customer Details username: " + username);
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        Customers customer = customersRepository.findByUsernameAndMvnoIdAndStatusNot(username, mvnoId, "Terminate").orElse(null);
        if (customer != null) {
            genericDataDTO.setData(customer);
            genericDataDTO.setResponseMessage("SUCCESS");
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
        } else {
            genericDataDTO.setData(null);
            genericDataDTO.setResponseMessage("No Records Found");
            genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
        }

        return genericDataDTO;
    }

    public GenericDataDTO UpdateUerUsage(String username, double usageBytes) {
        log.debug("In update user usage username: " + username + " ,usage in Bytes: " + usageBytes);
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        Long custid = customersRepository.findIdByUsername(username);
        if (custid == null) {
            return genericDataDTO;
        }
        List<CustQuotaDetails> quotaDetails = custQuotaDetailsRepository.findBasePlanQuotaByCustid(custid.intValue());
        CustQuotaDetails quota = quotaDetails.get(0);
        if (quota == null) {
            return genericDataDTO;
        }
        Double convertusage = quotaConverter(quota.getQuotaUnit(), usageBytes);
//        if (quota.getTotalQuota() < convertusage.byteValue()) {
//            genericDataDTO.setResponseCode(422);
//            genericDataDTO.setResponseMessage("Usage exceeds total quota.");
//            genericDataDTO.setData(usageBytes);
//            return genericDataDTO;
//        }
        quota.setUsedQuota(convertusage);

        double unusedQuota = quota.getTotalQuota() - usageBytes;
        if (unusedQuota < 0) {
            unusedQuota = 0;
        }
        CustQuotaDetails savedQuotaDetails = custQuotaDetailsRepository.save(quota);
        CustomerQuotaInfo custQuotaInfo = new CustomerQuotaInfo();
        custQuotaInfo.setQuotaType(savedQuotaDetails.getQuotaType());
        custQuotaInfo.setTimeBasedTotalQuota(savedQuotaDetails.getTimeTotalQuota());
        custQuotaInfo.setTimeBasedUsedQuota(savedQuotaDetails.getTimeQuotaUsed());
        custQuotaInfo.setVolumeBasedTotalQuota(savedQuotaDetails.getTotalQuota());
        custQuotaInfo.setVolumeBasedUsedQuota(savedQuotaDetails.getUsedQuota());
        custQuotaInfo.setVolumeBasedUsedQuota(convertusage);
        custQuotaInfo.setCustpackageid(savedQuotaDetails.getCustPlanMappping().getId().intValue());
        custQuotaInfo.setMvnoId(2L);
        custQuotaInfo.setUserName(username);
        kafkaMessageSender.send(new KafkaMessageData(custQuotaInfo, custQuotaInfo.getClass().getSimpleName()));

        List<LiveUser> liveUserList = liveUserRepository.findAllByCustid(custid.toString());
        if (!liveUserList.isEmpty()) {
            try {
                DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();
                CustomerData custRetrunData = dbAuth.getDBCustomer(null, 2, custid.toString(), null, false);
                String event = CommonConstants.EVENTCONSTANTS.UPDATE_USAGE;
                changeUserData changeUserData = new changeUserData(username,
                        Long.valueOf(2));
                List<changeUserData> userList = new ArrayList<changeUserData>();
                userList.add(changeUserData);
                customerService.CoADMSupport(userList, "COA", custRetrunData, event);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("Getting error during trigger coa for live user : ");
            }
        } else {
            log.info("No live user found for cust id: " + custid);
        }

        genericDataDTO.setData(savedQuotaDetails);
        genericDataDTO.setResponseMessage("Used quota updated successfully.");
        return genericDataDTO;
    }

    private Double quotaConverter(String CustPlan, Double usageQuota) {
        if (CustPlan == null || CustPlan.isEmpty()) {
            throw new IllegalArgumentException("CustPlan or QuotaUnit cannot be null");
        }
        switch (CustPlan.toUpperCase()) {
            case "GB":
                usageQuota = usageQuota / (1024.0 * 1024.0 * 1024.0);
                return usageQuota;
            case "MB":
                usageQuota = usageQuota / (1024.0 * 1024.0);
                return usageQuota;
            default:
                throw new UnsupportedOperationException("Unsupported QuotaUnit");
        }
    }

    public GenericDataDTO SessionLoginStatus(String ipAddress) {
        log.debug("In session Login Status ipaddress: " + ipAddress);
        //ToDo this Api may have changes
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            List<LiveUser> framedIpAddress = liveUserRepository.findByFramedIpAddressOrderByCreatedDateDesc(ipAddress);
            Integer size = framedIpAddress.size();
            genericDataDTO.setData(framedIpAddress.get(0));
            return genericDataDTO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public GenericDataDTO GetAccountName(String ipAddress) {
        log.debug("In Get Account Name ipaddress: " + ipAddress);

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Boolean checkUserIsUnKnownOrNot = checkUserIsKnownOrNot(ipAddress, 2L);
        if (!checkUserIsUnKnownOrNot) {
            genericDataDTO.setData("UNKNOWN");
        } else {
            List<LiveUser> liveUserList = liveUserRepository.findByFramedIpAddress(ipAddress);
            Optional<LiveUser> loginSession = liveUserList.stream().min(Comparator.comparing(LiveUser::getLastmodifiedDate).reversed());
            if (loginSession.isPresent()) {
                String userData = loginSession.get().getUserName();
                genericDataDTO.setData(userData);
            } else {
                genericDataDTO.setResponseCode(401);
                genericDataDTO.setData(null);
            }
        }
        return genericDataDTO;
    }

    //    public GenericDataDTO checkLiveRadiusClient(String ipaddress, Long mvnoId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        List data = new ArrayList();
//        Boolean aBoolean = false;
//        List<LiveUser> liveUser = liveUserRepository.findByFramedIpAddressOrderByCreatedDateDesc(ipaddress);

    /// /        liveUser.get(0).getramedIpAddress();F
//        if (liveUser != null && !liveUser.isEmpty()) {
//            List<Client> client = clientService.findClientByIpAddress(ipaddress, Math.toIntExact(mvnoId));
//            Client client1 = client.get(0);
//            if (client1 != null) {
//                Boolean isVendoreAvailable = isVendoreAvailable(ipaddress, Math.toIntExact(mvnoId));
//                if (isVendoreAvailable) {
//                    if (client1.getVendor().equalsIgnoreCase("HUAVEI")) {
//                        genericDataDTO.setResponseMessage("Session is not expected format for HUAVEI");
//                        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                        aBoolean = true;
//                        genericDataDTO.setData(aBoolean);
//                    } else if (client.get(0).getVendor().equalsIgnoreCase("NOKIA")) {
//                        genericDataDTO.setResponseMessage("Session is not expected format for NOKIA");
//                        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                        aBoolean = true;
//                        genericDataDTO.setData(aBoolean);                    }
//                }
//            }
//        }
//        genericDataDTO.setData(aBoolean);
//        return genericDataDTO;
//    }
    public GenericDataDTO checkLiveRadiusClient(String ipaddress, Integer mvnoId) throws CloneNotSupportedException {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        RadiusUtility radiusUtility = new RadiusUtility();
        Boolean aBoolean = false;
        LiveUserSearchDTO paginationDTO = new LiveUserSearchDTO();
        paginationDTO.setFramedIpAddress(ipaddress);
        Page<LiveUser> liveUsersFromIp = liverUserService.findLiveUsersUsingFilter(paginationDTO, mvnoId);
        List<LiveUser> liveUsersList = new ArrayList<>(liveUsersFromIp.getContent());
        if (!CollectionUtils.isEmpty(liveUsersList)) {
            LiveUser liveuser = liveUsersList.get(0);
            AccountingRequest acctReq = (AccountingRequest) radiusUtility.getRequestFromLiveUser(liveuser);
            Client client1 = radiusUtility.identifyClient(liveuser.getNasIpAddress(), acctReq);
            if (client1 != null) {
                genericDataDTO.setResponseMessage("Session is not expected format for " + client1.getVendor());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setData(aBoolean);
                return genericDataDTO;
            }
        }
        genericDataDTO.setData(aBoolean);
        return genericDataDTO;
    }

    private Boolean isVendoreAvailable(String ipaddress, Integer mvnoId) {
        List<Client> client = clientService.findClientByIpAddress(ipaddress, Math.toIntExact(mvnoId));
        if (client.get(0).getVendor() != null && !client.get(0).getVendor().isEmpty()) {
            return true;
        }
        return false;
    }

    public GenericDataDTO LoggOffSubSession(String ipAddress, Long mvnoId) {
        log.debug("In LogOff Sub Session ip: " + ipAddress);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        LiveUser liveUser = new LiveUser();
        liveUser.setMvnoId(liveUser.getMvnoId());
        List<GetUserSessionresponseDto> userSession = liveUserRepository.findUserSessionByIp(ipAddress);
        if (userSession == null || userSession.isEmpty()) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("No active session found for the given IP address.");
            return genericDataDTO;
        }
        GetUserSessionresponseDto session = userSession.get(0);
        genericDataDTO.setData(session);

        if (session != null) {
            List<Long> cdrIDs = liveUserRepository.findCdrIDByIp(ipAddress);
            if (cdrIDs.isEmpty()) {
                throw new NoResultException("No records found for IP address: " + ipAddress);
            }
            liverUserService.disconnectLiveUsers(Collections.singletonList(cdrIDs.get(0)), Math.toIntExact(mvnoId), false);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Logoff operation completed successfully.");
        } else {
            genericDataDTO.setResponseMessage("Session data is null.");
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }

        return genericDataDTO;
    }

    public GenericDataDTO getMeteredVolumeUsage(String SubscriberID) {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Long custid = customersRepository.findIdByUsername(SubscriberID);
        List<MeteredVolumeUsageDTO> custQuotaDetails1 = custQuotaDetailsRepository.getMeteredVolumeUsage(custid.intValue());
        if (!CollectionUtils.isEmpty(custQuotaDetails1)) {
            MeteredVolumeUsageDTO custQuotaDetails = custQuotaDetails1.get(0);
            genericDataDTO.setData(custQuotaDetails);
            genericDataDTO.setResponseMessage("SUCCESS");
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
        }
        return genericDataDTO;

    }

    public GenericDataDTO LoggOffSubSessions(String username, Long mvnoId) throws SQLException {
        log.debug("In log of sub session username: " + username);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Customers customers = customersRepository.findByUsername(username);
        // Fetch user session details
        List<GetUserSessionresponseDto> userNameSessions = liveUserRepository.findUserSessionByuserName(username);
        if (CollectionUtils.isEmpty(userNameSessions)) {
            genericDataDTO.setResponseMessage("No active session found for the given username.");
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            return genericDataDTO;
        }
        List<Long> cdrIDs = liveUserRepository.findCdrIDByuserName(username);
        if (CollectionUtils.isEmpty(cdrIDs)) {
            throw new NoResultException("No CDR records found for username: " + username);
        }
        genericDataDTO.setData(userNameSessions.get(0));
//        liverUserService.disconnectLiveUsers(Collections.singletonList(cdrIDs.get(0)), Math.toIntExact(mvnoId));
        LogoutCustomerDTO logoutCustomerDTO = new LogoutCustomerDTO();
        logoutCustomerDTO.setMvnoId(mvnoId.intValue());
        logoutCustomerDTO.setUsername(username);
        logoutCustomerDTO.setCustId(customers.getId());
        logoutCustomerDTO.setFramedIP(userNameSessions.get(0).getFramedIpAddress());
        changeUserData changeUserData = new changeUserData(logoutCustomerDTO.getUsername(),
                Long.valueOf(logoutCustomerDTO.getMvnoId()));
        List<changeUserData> userList = new ArrayList<changeUserData>();
        userList.add(changeUserData);
        DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();
        CustomerData custRetrunData = dbAuth.getDBCustomer(null, logoutCustomerDTO.getMvnoId(), logoutCustomerDTO.getCustId().toString(), null, false);
        customerService.CoADMSupport(userList, "COA", custRetrunData, CommonConstants.EVENTCONSTANTS.CUSTOMER_LOGOUT);
        //customerService.logoutCustomer(userList, logoutCustomerDTO, custRetrunData, CommonConstants.EVENTCONSTANTS.CUSTOMER_LOGOUT);
        genericDataDTO.setResponseMessage("Logoff operation completed successfully.");
        genericDataDTO.setResponseCode(HttpStatus.OK.value());

        return genericDataDTO;
    }

    public GenericDataDTO getCustQoutaDetails(String username, Integer mvnoId) throws Exception {
        log.debug("Entering getCustQuotaDetails with username: {} and MVNO ID: {}", username, mvnoId);

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<Object[]> customers = new ArrayList<>();
        log.info("Fetching customer details for username: {}", username);
        customers = customersRepository.findByUsernameAndMvnoIdAndStatus(username, mvnoId, "Terminate");
        if (customers == null) {
            genericDataDTO.setData(null);
            genericDataDTO.setResponseMessage("No Records Found");
            log.warn(CommonConstants.SoapConstant.USER_NOT_AVAILABLE + username);
            genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
            return genericDataDTO;
        }
        log.info("Fetching quota details for customer ID: {}", customers.get(0)[0]);
        //        List<CustQuotaDetails> custQuotaDetails = custQuotaDetailsRepository.findAllByCustidAndCustStutusActive((Integer) customers.get(0)[0]);
        List<Object[]> custQuotaDetails = custQuotaDetailsRepository.findCustQuotaDetailsByCustidAndStatus((Integer) customers.get(0)[0]);

        log.info("Fetching live user data for customer ID: {}", customers.get(0)[0]);
        //        List<LiveUser> liveUserList = liveUserRepository.findAllByCustid((String) customers.get(0)[0]);
        List<Object[]> liveUserData = liveUserRepository.findLiveUserDataByCustid(customers.get(0)[0].toString());

        Optional<Object[]> loginSession = liveUserData.stream()
                .max(Comparator.comparing(data -> (Date) data[1]));
//        Optional<LiveUser> loginSession = liveUserList.stream().min(Comparator.comparing(LiveUser::getLastmodifiedDate).reversed());

        String uploadQuota = "0.0";
        String downloadQuota = "0.0";

        if (liveUserData != null && !liveUserData.isEmpty()) {
            log.debug("Calculating total upload and download quotas.");
            long totalUpload = liveUserData.stream()
                    .mapToLong(data -> Long.parseLong((String) data[2])) // Assuming index 2 is acctInputOctets
                    .sum();

            long totalDownload = liveUserData.stream()
                    .mapToLong(data -> Long.parseLong((String) data[3])) // Assuming index 3 is acctOutputOctets
                    .sum();

            uploadQuota = String.valueOf(totalUpload);
            downloadQuota = String.valueOf(totalDownload);
        }

        List<WsGetBalanceRequestDTO> arrayLists = new ArrayList<>();
        for (Object[] quotaDetails : custQuotaDetails) {
            WsGetBalanceRequestDTO wsGetBalanceRequestDTO = new WsGetBalanceRequestDTO();
            String planName = null;
            String planGroup = null;

            log.debug("Processing quota details for customer ID: {}", customers.get(0)[0]);
            // Extract values from the Object[] (index based on the query)
            Integer planId = ((Long) quotaDetails[0]).intValue();
            Double totalQuota = (Double) quotaDetails[1];
            Double usedQuota = (Double) quotaDetails[2];
            Double currentSessionUsageVolume = (Double) quotaDetails[3];
            String usageQuotaType = (String) quotaDetails[4];
            String quotaUnit = (String) quotaDetails[5];
            CustPlanMappping custPlanMappping = (CustPlanMappping) quotaDetails[6];  // Full CustPlanMappping object

            Double balanceQuota = totalQuota - usedQuota;
//            Integer planId = Math.toIntExact(quotaDetails.getPlanId());
//            PostpaidPlan postpaidPlan = postpaidPlanRepository.findById(planId).get();
            log.info("Fetching plan details for Plan ID: {}", planId);
            Object[] postpaidPlanData = postpaidPlanRepository.findPostpaidPlanById(planId);
            if (postpaidPlanData != null) {
                Object[] planDetails = (Object[]) postpaidPlanData[0];
                planName = (String) planDetails[0];
                planGroup = (String) planDetails[1];
            }
//            CustPlanMappping custPlanMappping = custPlanMappingRepository.findByCprId(quotaDetails.getCustPlanMappping().getId());
//            Double balanceQouta = quotaDetails.getTotalQuota() - quotaDetails.getUsedQuota();
            wsGetBalanceRequestDTO.setBalance(balanceQuota.toString());
            wsGetBalanceRequestDTO.setPackageId(planId.toString());
            wsGetBalanceRequestDTO.setPackageName(planName);
            wsGetBalanceRequestDTO.setServiceId(planId.toString());
            wsGetBalanceRequestDTO.setCurrentUsage(usedQuota.toString());
            wsGetBalanceRequestDTO.setCprId(custPlanMappping.getId());
            String plantype = convertPackageName(planGroup);
            wsGetBalanceRequestDTO.setPackageType(plantype);
            wsGetBalanceRequestDTO.setCurrentSessionUsageVolume(currentSessionUsageVolume);
            wsGetBalanceRequestDTO.setUsedQuota(usedQuota);
            wsGetBalanceRequestDTO.setTotalQuota(totalQuota);
            wsGetBalanceRequestDTO.setQuotaProfileId(planId.toString());
            wsGetBalanceRequestDTO.setSubscriberId(customers.get(0)[1].toString());
            wsGetBalanceRequestDTO.setDownloadOctet("-1");
            wsGetBalanceRequestDTO.setHsqLimit(totalQuota.toString());
            wsGetBalanceRequestDTO.setQuotaProfileName(usageQuotaType + "_QP");
            wsGetBalanceRequestDTO.setTime("-1");
            wsGetBalanceRequestDTO.setTotalOctet("-1");
            wsGetBalanceRequestDTO.setUploadOctet("-1");
            wsGetBalanceRequestDTO.setServiceName("All Service");

            Long endTime = dateConvertInMillis(custPlanMappping.getEndDate());
            wsGetBalanceRequestDTO.setUsageResetTime(endTime.toString());
            wsGetBalanceRequestDTO.setEndTime(endTime.toString());
            wsGetBalanceRequestDTO.setAddOnStatus(custPlanMappping.getCustPlanStatus());
            wsGetBalanceRequestDTO.setAddonSubscriptionId(custPlanMappping.getId().toString());
            Long startTime = dateConvertInMillis(custPlanMappping.getStartDate());
            wsGetBalanceRequestDTO.setStartTime(startTime.toString());
            wsGetBalanceRequestDTO.setQuotaUnit(quotaUnit);

            arrayLists.add(wsGetBalanceRequestDTO);
        }

        String finalUploadQuota = uploadQuota;
        String finalDownloadQuota = downloadQuota;

        loginSession.ifPresent(session -> {
            log.debug("Updating quota details with live session data.");
            arrayLists.stream()
                    .filter(quotaDetails -> Objects.equals(quotaDetails.getCprId(), session[4]))
                    .forEach(quotaDetails -> {
                        quotaDetails.setUploadOctet(finalUploadQuota);
                        quotaDetails.setDownloadOctet(finalDownloadQuota);
                    });
        });

        genericDataDTO.setData(arrayLists);
        return genericDataDTO;
    }

    public GenericDataDTO GetReAuthSession(String username, Long mvnoId) throws SQLException {
        log.debug("Entering GetReAuthSession with username: {}", username);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        log.info("Fetching customer details for username: {}", username);
        Customers user = customersRepository.findByUsername(username);//customersRepository.findUsernameAndPasswordByUsername(username);
        if (user == null && user.getId() != null) {
            genericDataDTO.setResponseMessage("User not available.");
            log.warn(CommonConstants.SoapConstant.USER_NOT_AVAILABLE + username);
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setData(user);
            return genericDataDTO;
        }
        log.info("Fetching live user session for customer ID: {}", user.getId());
        List<LiveUser> liveUser = liveUserRepository.findAllByCustid(String.valueOf(user.getId()));//findFirstByUserNameOrderByCdrIDDesc(user.getUsername());
        if (CollectionUtils.isEmpty(liveUser)) {
            log.warn("No active session found for username: {}", username);
            genericDataDTO.setResponseMessage("User not logged in.");
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setData(liveUser);
            return genericDataDTO;
        }

        log.info("Fetching customer authentication details from DB for username: {}", username);
        DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();
        CustomerData custRetrunData = dbAuth.getDBCustomer(username, mvnoId.intValue(), null, null, false, true);

        if (custRetrunData != null) {
            log.debug("Triggering CoA (Change of Authorization) for username: {}", username);
            changeUserData changeuserData = new changeUserData(custRetrunData.getUsername(), Long.valueOf(custRetrunData.getMvnoId()));
            List<changeUserData> userList = new ArrayList<changeUserData>();
            userList.add(changeuserData);
            customerService.CoADMSupport(userList, "COA", custRetrunData, CommonConstants.EVENTCONSTANTS.RE_AUTH);
        }

        genericDataDTO.setData("Success");
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        return genericDataDTO;
    }

    public GenericDataDTO getSubscriberAccDetails(String username, Integer mvnoId) {
        log.debug("In customer Acct Details: " + username);
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        Customers customer = customersRepository.findByUsernameAndMvnoIdAndStatusNot(username, mvnoId, "Terminate").get();
        if (customer != null && customer.getStatus().equalsIgnoreCase("Terminate")) {
            genericDataDTO.setData(customer);
            genericDataDTO.setResponseMessage("Username is not available in SPR Table");
            genericDataDTO.setResponseCode(503);
            return genericDataDTO;
        }
        if (customer != null) {
            GetSubscriberAccountDetailsDTO dto = new GetSubscriberAccountDetailsDTO();

            ZonedDateTime zonedDateTime = customer.getCreatedate().atZone(ZoneId.of("Asia/Kolkata")); // Convert to ZonedDateTime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSS"); // Desired format
            String formattedDate = zonedDateTime.format(formatter);
            dto.setCreationDate(formattedDate);
            dto.setPassword(customer.getPassword());
            dto.setCStatus(changeStatusValue(customer.getStatus()));
            dto.setCustName(customer.getUsername());

            List<CustPlanMappping> custPlanMappping = custPlanMappingRepository.findAllByCustidAndCustPlanStatusAndPurchaseType(customer.getId(), "Active", "New");
            if (!custPlanMappping.isEmpty()) {
                if (custPlanMappping.get(0).getPlanId() != null) {
                    Integer planId = custPlanMappping.get(0).getPlanId();
                    Optional<PostpaidPlan> planData = postpaidPlanRepository.findById(planId);
                    dto.setPlanId(planData.get().getName());
                }
            }

//            LiveUser liveUser = liveUserRepository.findFirstByUserNameOrderByCdrIDDesc(username);

            if (customer != null && StringUtils.hasText(customer.getNasPortId())) {
                dto.setLocationLock(customer.getNasPortId());
            } else {
                dto.setLocationLock("");
            }
            genericDataDTO.setData(dto);
            genericDataDTO.setResponseMessage("SUCCESS");
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
        }

        return genericDataDTO;
    }

    public Long dateConvertInMillis(LocalDateTime time) {
        LocalDateTime localDateTime = LocalDateTime.now();
        // Convert to milliseconds
        long milliseconds = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return milliseconds;
    }

    public String convertPackageName(String planName) {
        if (planName.equalsIgnoreCase("Bandwidthbooster"))
            return "ADDON";
        else if (planName.equalsIgnoreCase("Volume Booster"))
            return "SPARETOPUP";
        else
            return "BASE";
    }

    public GenericDataDTO GetLogedInUsername(String subscriberId) {
        log.debug("In get Logged In custId: " + subscriberId);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Boolean customerAvailable = liveUserRepository.existsByUserName(subscriberId);
        List<LiveUser> liveUser = liveUserRepository.findByUserName(subscriberId);
        if (!liveUser.isEmpty()) {
            LiveUser liveusers = liveUser.get(0);
            boolean isUserKnown = checkUserIsKnownOrNot(liveusers.getFramedIpAddress(), 2L);
            if (isUserKnown) {
                genericDataDTO.setResponseMessage("true");
            } else {
                genericDataDTO.setResponseMessage("false");
            }
        } else {
            genericDataDTO.setResponseMessage(CommonConstants.SoapConstant.USER_NOT_AVAILABLE);
        }

        return genericDataDTO;
    }

    public String changeStatusValue(String status) {
        if (status != null && status.equalsIgnoreCase("Active")) {
            return "Y";
        } else if (status != null && status.equalsIgnoreCase("InActive")) {
            return "N";
        } else
            return status;
    }

    public Integer getAllLiveLogingUser(String username) {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<LiveUser> liveUser = liveUserRepository.findAllByUserName(username);
        Integer userSize = liveUser.size();
        genericDataDTO.setData(liveUser);
        return userSize;

    }

    public GenericDataDTO getCOAValidation(String ipAddress, Long mvnoId) {
        log.debug("In COA Validation ipAddress: " + ipAddress);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Client client = clientRepository.findByClientIP(ipAddress);
        String vendor = client.getVendor();
        try {
            List<LiveUser> liveUsers = liveUserRepository.findAllByNasIpAddress(ipAddress);
            Device device = deviceRepository.getOne(client.getDeviceId());
            Integer coacheck = deviceServiceImple.generateCoaDMForCustomerLogin(device.getCoaDmProfileId(), client.getMvnoId(), liveUsers.get(0).getUserName(), client.getClientIpAddress(), liveUsers.get(0).getUserPassword());

        } catch (Exception e) {
            if (vendor.equalsIgnoreCase("Huawei")) {
                genericDataDTO.setResponseCode(314);
                genericDataDTO.setResponseMessage("COA Failed " + vendor);
            } else if (vendor.equalsIgnoreCase("Nokia")) {
                genericDataDTO.setResponseCode(325);
                genericDataDTO.setResponseMessage("COA Failed " + vendor);
            } else if (vendor.equalsIgnoreCase("SNMP")) {
                genericDataDTO.setResponseCode(315);
                genericDataDTO.setResponseMessage("COA Failed " + vendor);
            }
            e.printStackTrace();
        }
        return genericDataDTO;
    }

    public GenericDataDTO checkKnownUser(String ipAddress, Long mvnoId) {
        log.debug("In check unknown User ipaddress: " + ipAddress);
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            List<LiveUser> liveUsers = liveUserRepository.findByFramedIpAddressOrderByCreatedDateDesc(ipAddress);
            String vendor = "";
            if (!CollectionUtils.isEmpty(liveUsers) && liveUsers.get(0) != null) {
                LiveUser liveuser = liveUsers.get(0);
                List<Client> client = clientService.findClientByIpAddress(liveuser.getNasIpAddress(), Math.toIntExact(mvnoId));
                if ((client != null && !client.isEmpty())) {
                    if (Objects.nonNull(client.get(0).getVendor()) && !client.get(0).getVendor().isEmpty()) {
                        vendor = client.get(0).getVendor();
                    }
                    RadiusUtility radiusUtility = new RadiusUtility();
                    String liveUserCallingStationId = radiusUtility.normalizeMacAddress(liveuser.getCallingStationId());
                    String liveUserUsername = radiusUtility.normalizeMacAddress(liveuser.getUserName());
                    if (liveUserCallingStationId.equalsIgnoreCase(liveUserUsername)) {
                        Optional<Customers> customer = customersRepository.findByUsernameAndMvnoIdAndStatusNot(liveuser.getUserName(), Math.toIntExact(mvnoId), "Terminate");
                        List<MacAddressMapping> macAddressMapping = macAddressMappingRepository.findByMacAddress(liveuser.getCallingStationId());
                        if (!customer.isPresent() && (macAddressMapping != null || macAddressMapping.isEmpty())) {
                            genericDataDTO.setResponseCode(201);
                            genericDataDTO.setData("IP is available in session table with PARAM_STR9 is UNKNOWN : " + vendor);
                            genericDataDTO.setResponseMessage("Failure");
                        } else {
                            genericDataDTO.setResponseCode(200);
                            genericDataDTO.setData("IP is available in session table and LoggedIN : " + vendor);
                            genericDataDTO.setResponseMessage("SUCCESS");
                        }

                    } else {
                        genericDataDTO.setResponseCode(200);
                        genericDataDTO.setData("IP is available in session table and LoggedIN : " + vendor);
                        genericDataDTO.setResponseMessage("SUCCESS");
                    }
                } else {
                    genericDataDTO.setResponseCode(407);
                    genericDataDTO.setResponseMessage("Vendor is not available");
                    genericDataDTO.setData("IP is available in session table and LoggedIN : "+vendor);

                }
            } else {
                genericDataDTO.setResponseCode(407);
                genericDataDTO.setResponseMessage("IP is not available in session table");
                genericDataDTO.setData("IP is available in session table and LoggedIN : "+ vendor);
            }
            return genericDataDTO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkUserIsKnownOrNot(String ipAddress, Long mvnoId) {
        boolean isUserKnown = true;
        List<LiveUser> liveUsers = liveUserRepository.findByFramedIpAddressOrderByCreatedDateDesc(ipAddress);
        if (!CollectionUtils.isEmpty(liveUsers) && liveUsers.get(0) != null) {
            LiveUser liveuser = liveUsers.get(0);
            Customers customer = customersRepository.findByUsernameAndMvnoIdAndStatusNot(liveuser.getUserName(), mvnoId.intValue(), "Terminate").orElse(null);
            if (liveuser.getCallingStationId().equalsIgnoreCase(liveuser.getUserName()) || customer == null) {
                if (customer == null) {
                    List<MacAddressMapping> mcMapping = macAddressMappingRepository.findByMacAddress(liveuser.getCallingStationId());
                    if (CollectionUtils.isEmpty(mcMapping) || mcMapping == null) {
                        isUserKnown = false;
                    }
                }
            }
        }
        return isUserKnown;
    }

    public GenericDataDTO GetBalanceBYSubscriberIdlist(String SubscriberID, String planName, Long mvnoId, String plan) {
        log.debug("In get balance custId: " + SubscriberID);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<GetBalanceDto> response = new ArrayList<>();
        Long custid = customersRepository.findIdByUsernameAndMvnoIdAndStatusNotTerminate(SubscriberID, 2l);
        List<GetBalanceDto> custQuotaDetails1 = custQuotaDetailsRepository.findByCustomerId(custid.intValue());
        List<LiveUser> liveUserList = liveUserRepository.findAllByCustid(custid.toString());
        Optional<LiveUser> loginSession = liveUserList.stream().min(Comparator.comparing(LiveUser::getLastmodifiedDate).reversed());
        String uploadQuota = "0.0";
        String downloadQuota = "0.0";
        if (!liveUserList.isEmpty() && liveUserList != null) {
            long totalUpload = liveUserList.stream()
                    .mapToLong(user -> Long.parseLong(user.getAcctInputOctets()))
                    .sum();

            long totalDownload = liveUserList.stream()
                    .mapToLong(user -> Long.parseLong(user.getAcctOutputOctets()))
                    .sum();
            uploadQuota = String.valueOf(totalUpload);
            downloadQuota = String.valueOf(totalDownload);
        }
//        for (GetBalanceDto quotaDetails :custQuotaDetails1){
//            if (loginSession.isPresent()) {
//                if (quotaDetails.getCprId() == (loginSession.get().getCprId())) {
//                    quotaDetails.setUploadQuota(uploadQuota);
//                    quotaDetails.setDownloadQuota(downloadQuota);
//                }
//            }
//
//        }

        String finalUploadQuota = uploadQuota;
        String finalDownloadQuota = downloadQuota;
        loginSession.ifPresent(session -> {
            custQuotaDetails1.stream()
                    .filter(quotaDetails -> Objects.equals(quotaDetails.getCprId(), loginSession.get().getCprId()))
                    .forEach(quotaDetails -> {
                        quotaDetails.setUploadQuota(finalUploadQuota);
                        quotaDetails.setDownloadQuota(finalDownloadQuota);
                    });
        });

//        if(!custQuotaDetails1.isEmpty() && custQuotaDetails1 != null){
//            custQuotaDetails1.get(0).setUploadQuota(uploadQuota);
//            custQuotaDetails1.get(0).setDownloadQuota(downloadQuota);
//        }
        List<GetBalanceDto> custQuotaDetailsList = new ArrayList<>();
        GetBalanceDto custQuotaDetailsplan = new GetBalanceDto();
        if (!CollectionUtils.isEmpty(custQuotaDetails1)) {
            if (!planName.isEmpty()) {
                PostpaidPlan postpaidPlan = postpaidPlanRepository.findByNameIgnoreCase(planName);
                if (postpaidPlan != null) {
                    Long planId = Long.valueOf(postpaidPlan.getId());
                    custQuotaDetailsplan = custQuotaDetailsRepository.findByPlanId(planId, custid.intValue());

                    custQuotaDetailsplan.setUploadQuota(uploadQuota);
                    custQuotaDetailsplan.setDownloadQuota(downloadQuota);
                    custQuotaDetailsList.add(custQuotaDetailsplan);
                    genericDataDTO.setDataList(custQuotaDetailsList);
                    genericDataDTO.setResponseMessage("SUCCESS");
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    return genericDataDTO;
                } else {
                    genericDataDTO.setResponseMessage("SUCCESS");
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    custQuotaDetailsplan.setUploadQuota(uploadQuota);
                    custQuotaDetailsplan.setDownloadQuota(downloadQuota);
                    return genericDataDTO;
                }
            } else if (!plan.isEmpty()) {
                Long planId = Long.valueOf(plan);
                custQuotaDetailsplan = custQuotaDetailsRepository.findByPlanId(planId, custid.intValue());
                if (custQuotaDetailsplan != null) {
                    custQuotaDetailsplan.setUploadQuota(uploadQuota);
                    custQuotaDetailsplan.setDownloadQuota(downloadQuota);
                    custQuotaDetailsList.add(custQuotaDetailsplan);
                }
                genericDataDTO.setDataList(custQuotaDetailsList);
                genericDataDTO.setResponseMessage("SUCCESS");
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                return genericDataDTO;
            } else {
                genericDataDTO.setDataList(custQuotaDetails1);
                genericDataDTO.setResponseMessage("SUCCESS");
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
            }
        }
        return genericDataDTO;
    }

    public GenericDataDTO GetUserSummeryBYSubscriberIdlist(String SubscriberID, Long mvnoId) {
        log.debug("In get User summary custId: " + SubscriberID);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Long custid = customersRepository.findIdByUsernameAndMvnoIdAndStatusNotTerminate(SubscriberID, 2l);
        if (custid == null || custid == 0) {
            genericDataDTO.setResponseMessage("Username is not available in SPR Table via Product API[findByUserIdentity]");
            genericDataDTO.setResponseCode(503);
            return genericDataDTO;
        }
        List<GetBalanceDto> custQuotaDetails1 = custQuotaDetailsRepository.findBySubscriberID(custid.intValue());
        List<GetBalanceDto> custQuotaDetailsList = new ArrayList<>();
        GetBalanceDto custQuotaDetailsplan = new GetBalanceDto();
        if (!CollectionUtils.isEmpty(custQuotaDetails1)) {
            if (!custQuotaDetails1.isEmpty()) {
                for (GetBalanceDto dto : custQuotaDetails1) {
                    dto.setDownloadQuota("0");
                    dto.setUploadQuota("0");
                    custQuotaDetailsList.add(dto);
                }
                genericDataDTO.setDataList(custQuotaDetailsList);
                genericDataDTO.setResponseMessage("SUCCESS");
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
            } else {
                custQuotaDetailsplan.setUploadQuota("0");
                custQuotaDetailsplan.setDownloadQuota("0");
                custQuotaDetailsList.add(custQuotaDetailsplan);
                genericDataDTO.setDataList(custQuotaDetails1);
                genericDataDTO.setResponseMessage("SUCCESS");
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
            }
        }
        return genericDataDTO;
    }

    public GenericDataDTO GetLiveUser(String ipAddress, Long mvnoId) {
        log.debug("In get Live user ipaddress: " + ipAddress);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<GetUserSessionresponseDto> userSession = liveUserRepository.findUserSessionByIp(ipAddress);
        if (CollectionUtils.isEmpty(userSession)) {
            genericDataDTO.setResponseMessage(CommonConstants.SoapConstant.IP_NOT_AVAILABLE_IN_SESSION);
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setData(userSession);
            return genericDataDTO;
        } else {
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(userSession);
        }

        return genericDataDTO;
    }

    public static String adjustNasPortId(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty");
        }
        // Split the input string by "."
        String[] parts = input.split("\\.");

        // Ensure there are enough parts to avoid ArrayIndexOutOfBoundsException
        if (parts.length < 4) {
            throw new IllegalArgumentException("Input string must have at least 4 parts separated by dots.");
        }
        // Map the values to Huawei's NAS-Port-ID format
        String slot = parts[0];
        String subslot = parts[1];
        String port = parts[2];
        String vlanId = parts[3];

        // Create the Huawei NAS-Port-ID format
        String nasPortId = String.format(
                "slot=%s;subslot=%s;port=%s;VLAN ID=%s", slot, subslot, port, vlanId
        );
        // Create the desired output format
        String formattedString = String.format(
                "0:92=[*%s:%s*,*lg id %s*]", slot, subslot, vlanId
        );
        // Combine both formats
        return String.format("NAS-Port-ID: %s | Adjusted Format: %s", nasPortId, formattedString);
    }

    public GenericDataDTO GetUserSessionDetailsTimeZoneZ(String ipAddress, Long mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        LiveUserSearchDTO paginationDTO = new LiveUserSearchDTO();
        List<GetUserSessionresponseDto> userSession = liveUserRepository.findUserSessionByIp(ipAddress);
        paginationDTO.setFramedIpAddress(ipAddress);
        Page<LiveUser> liveUsersFromIp = liverUserService.findLiveUsersUsingFilter(paginationDTO, Math.toIntExact(mvnoId));
        List<LiveUser> liveUsersList = new ArrayList<>(liveUsersFromIp.getContent());
        Optional<LiveUser> loginSession = null;
        if (liveUsersFromIp != null && !liveUsersFromIp.isEmpty() && liveUsersFromIp.getContent().size() > 0) {
            loginSession = liveUsersList.stream().min(Comparator.comparing(LiveUser::getLastmodifiedDate).reversed());
            GetUserSessionresponseDto data = new GetUserSessionresponseDto();
            data.setAcctSessionId(loginSession.get().getAcctSessionId());
            data.setCdrID(loginSession.get().getCdrID());
            data.setUserName(loginSession.get().getUserName());
            data.setCreatedDate(loginSession.get().getCreatedDate());
            data.setCallingStationId(loginSession.get().getCallingStationId());
            data.setNasPortType(loginSession.get().getNasPortType());
            data.setNasPortId(loginSession.get().getNasPortId());
            data.setFramedIPv6Prefix(loginSession.get().getFramedIPv6Prefix());
            data.setFramedIpAddress(loginSession.get().getFramedIpAddress());
            data.setDelegatedIPv6Prefix(loginSession.get().getDelegatedIPv6Prefix());
            ZonedDateTime sessionCreatedTime = loginSession.get().getCreatedDate().toInstant().atZone(ZoneId.of("Asia/Kolkata"));
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            String formattedSessionTime = sessionCreatedTime.format(dateFormatter);
            data.setCreatedDateString(formattedSessionTime);

            boolean isUserKnown = checkUserIsKnownOrNot(ipAddress, 2L);
            data.setKnownUser(isUserKnown);

            if (loginSession.get() == null) {
                genericDataDTO.setData(null);
            } else {
                genericDataDTO.setData(data);
            }
        }
        return genericDataDTO;
    }

    public GenericDataDTO getLogOnSubSession(LogOnSubSessionDTO req, Long mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String ipAddress = req.getString1();
        String userName = req.getString2().trim().toLowerCase();
        String password = req.getString3();
        log.debug("In get log on sub session Ip: " + ipAddress + " ,username: " + userName);
        try {
            List<LiveUser> liveUser = liveUserRepository.findByFramedIpAddress(ipAddress);
            Customers customers = customersRepository.findAllByUsernameAndMvnoIdAndStatusNot(userName, Math.toIntExact(mvnoId), "Terminate");
            Optional<LiveUser> loginSession = null;
            Integer maxConcurrentUser = 0;
            loginSession = liveUser.stream().min(Comparator.comparing(LiveUser::getLastmodifiedDate).reversed());
            if (CollectionUtils.isEmpty(liveUser)) {
                genericDataDTO.setResponseMessage("No Session found for Login");
                genericDataDTO.setResponseCode(323);
                return genericDataDTO;
            }
            if (customers == null) {
                genericDataDTO.setResponseMessage("User does not exist in to the system");
                genericDataDTO.setResponseCode(301);
                return genericDataDTO;
            } else {
                maxConcurrentUser = customers.getMaxconcurrentsession() != null ? customers.getMaxconcurrentsession() : 1;
                if (maxConcurrentUser < liveUser.size()) {
                    genericDataDTO.setResponseMessage("Number of allowed concurrent users count has been exceeded");
                    genericDataDTO.setResponseCode(305);
                    return genericDataDTO;
                } else if (loginSession.get().getNasIdentifier() == null || loginSession.get().getNasIdentifier().isEmpty()) {
                    genericDataDTO.setResponseMessage("NAS_IDENTIFIER does not exist for given ipaddress or login action");
                    genericDataDTO.setResponseCode(311);
                    return genericDataDTO;
                } else if (!userName.equalsIgnoreCase(customers.getUsername()) || !password.equals(customers.getPassword())) {
                    genericDataDTO.setResponseMessage("Given username and password does not match in to the system");
                    genericDataDTO.setResponseCode(303);
                    return genericDataDTO;
                } else if ((customers.getStatus() != null && !customers.getStatus().isEmpty()) && !customers.getStatus().equalsIgnoreCase("Active")) {
                    genericDataDTO.setResponseMessage("Given username is not ACTIVE in to the system");
                    genericDataDTO.setResponseCode(304);
                    return genericDataDTO;
                } else {
                    genericDataDTO.setResponseMessage("Success");
                    genericDataDTO.setResponseCode(200);
                    genericDataDTO.setData(customers);

                }
            }

        } catch (Exception e) {
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(404);
            return genericDataDTO;
        }
        return genericDataDTO;
    }

    public List<TopUpSubscriptionListDto> GetListTopUpSubscriptions(String username, Integer mvnoId) throws Exception {
        log.debug("In get List of TopUp Subscriptions: " + username);
        long starttime = System.currentTimeMillis();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Long custid = customersRepository.findIdByUsernameAndMvnoIdAndStatusNotTerminate(username, mvnoId.longValue());

        List<Object[]> custPlanMappping = custPlanMappingRepository.findTopUpSubscriptionsByCustomerId(custid.intValue());
        return custPlanMappping.stream().map(obj -> {
            TopUpSubscriptionListDto dto = new TopUpSubscriptionListDto();
            dto.setEndTime(((Timestamp) obj[0]).toLocalDateTime());  // Convert Timestamp to LocalDateTime
            dto.setStartTime(((Timestamp) obj[1]).toLocalDateTime());  // startTime
            dto.setTopUpSubscriptionId(((BigInteger) obj[2]).longValue()); // custpackid
            dto.setTopUpId(((BigInteger) obj[4]).intValue());// planid
            dto.setTopUpName((String) obj[5]);         // planname
            dto.setTopUpStatus((String) obj[3]);       // planstatus
            dto.setSubscriberIdentity(username);
            dto.setUsageResetTime(((Timestamp) obj[0]).toLocalDateTime());
            return dto;
        }).collect(Collectors.toList());
    }

    public List<AddOnSubscriptionListDto> GetListAddOnSubscriptions(String username, Integer mvnoId) throws Exception {
        log.debug("Entering GetListAddOnSubscriptions with userName:{} and MvnoId:{} ", username, mvnoId);
        Long custid = customersRepository.findIdByUsernameAndMvnoIdAndStatusNotTerminate(username, mvnoId.longValue());
        log.debug("Fetched Customer ID: {}", custid);

        List<Object[]> custPlanMappping = custPlanMappingRepository.findAddOnSubscriptionsByCustomerId(custid.intValue());
        log.debug("Fetched {} AddOn subscriptions for customerId: {}", custPlanMappping.size(), custid);

        return custPlanMappping.stream().map(obj -> {
            AddOnSubscriptionListDto dto = new AddOnSubscriptionListDto();
            dto.setEndTime(((Timestamp) obj[0]).toLocalDateTime());  // Convert Timestamp to LocalDateTime
            dto.setStartTime(((Timestamp) obj[1]).toLocalDateTime());  // startTime
            dto.setAddonSubscriptionId(((BigInteger) obj[2]).longValue()); // custpackid
            dto.setAddOnId(((BigInteger) obj[4]).intValue());// planid
            dto.setAddOnName((String) obj[5]);         // planname
            dto.setAddOnStatus((String) obj[3]);       // planstatus
            dto.setSubscriberIdentity(username);
            dto.setUsageResetTime(((Timestamp) obj[0]).toLocalDateTime());
            return dto;
        }).collect(Collectors.toList());
    }


}
