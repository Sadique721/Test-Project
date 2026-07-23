package com.savbill.radius.services.impl;

import com.savbill.radius.aaa.data.CustomerData;
import com.savbill.radius.aaa.data.CustomerPlanData;
import com.savbill.radius.aaa.db.DBAccountingDriver;
import com.savbill.radius.aaa.db.DBAuthenticationDriver;
import com.savbill.radius.aaa.server.AuthAcctUtilityServiceImpl;
import com.savbill.radius.aaa.server.RadiusAsyncUtility;
import com.savbill.radius.aaa.server.RadiusUtility;
import com.savbill.radius.dto.LiveUserSearchDTO;
import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.LiveUser;
import com.savbill.radius.entity.QLiveUser;
import com.savbill.radius.helper.UsersDto;
import com.savbill.radius.helper.changeUserData;
import com.savbill.radius.kafka.message.CustomerQuotaInfo;
import com.savbill.radius.repository.CustPlanMappingRepository;
import com.savbill.radius.repository.CustomersRepository;
import com.savbill.radius.repository.LiveUserRepository;
import com.savbill.radius.services.CustomerService;
import com.savbill.radius.services.LiveUserService;
import com.savbill.radius.utils.CommonConstants;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.savbill.radius.utils.RadiusUtils.notNullNotEmpty;

@Service
public class LiveUserServiceImpl implements LiveUserService {

    private static Log log = LogFactory.getLog(LiveUserServiceImpl.class);

    @Autowired
    private LiveUserRepository liveUserRepository;

    @Autowired
    StaffServiceImpl staffService;

    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;

    @Override
    public Page<LiveUser> getAll(Integer mvnoId, PaginationDTO paginationDTO, HttpServletRequest request) {
        QLiveUser qLiveUser = QLiveUser.liveUser;
        BooleanExpression boolExp = qLiveUser.isNotNull();
        if (mvnoId != null && mvnoId != 1) {
            boolExp = boolExp.and(qLiveUser.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
        } else if (mvnoId == 1 && paginationDTO == null) {
            Page<LiveUser> page = new PageImpl<LiveUser>(liveUserRepository.findAll());
            return page;
        }
        if (paginationDTO.getPage() > 0) {
            paginationDTO.setPage(paginationDTO.getPage() - 1);
        }
        Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "lastmodifiedDate"));
        if (!(StringUtils.isBlank(paginationDTO.getFromDate()) || paginationDTO.getFromDate().equalsIgnoreCase("null"))) {
            boolExp = boolExp.and(qLiveUser.lastmodifiedDate.eq(Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00"))
                    .or(qLiveUser.lastmodifiedDate.after(Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00"))));
        }
        if (!(StringUtils.isBlank(paginationDTO.getToDate()) || paginationDTO.getToDate().equalsIgnoreCase("null"))) {
            boolExp = boolExp.and(qLiveUser.lastmodifiedDate.eq(Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59"))
                    .or(qLiveUser.lastmodifiedDate.before(Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59"))));
        }
//        Integer staffIdFromApigw=null;
//        if(request.getHeader("staffIdFromApigw") != null) {
//            staffIdFromApigw= request.getIntHeader("staffIdFromApigw");
//         }
//        List<Long>serviceAreaIds= staffService.ListOfIds(staffIdFromApigw);
//        QCustomers qCustomers=QCustomers.customers;
//        BooleanExpression booleanExpression=qCustomers.isNotNull();
//        List<LiveUser> liveUsers=liveUserRepository.findAll();
//        List<Customers> customers=customersRepository.findAll();
//         Set<String> userName=customers.stream().filter(customers1 -> serviceAreaIds.contains(customers1.getServicearea())).map(Customers:: getUsername).collect(Collectors.toSet());
//        if(!CollectionUtils.isEmpty(userName)){
//            boolExp=boolExp.and(qLiveUser.userName.in(userName));
//        }


        Page<LiveUser> page = liveUserRepository.findAll(boolExp, pageable);

        return page;
    }

    @Override
    public void delete(Long id, Integer mvnoId) {
        Optional.ofNullable(id).map(longId -> {
            LiveUser liveUser = findLiveUsers(id, ValidateCrudTransactionData.validateMvnoId(mvnoId));
            liveUserRepository.deleteById(id);
            MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
            log.info("Live user deleted succefully: " + liveUser.getUserName());
            return true;
        }).orElseThrow(() -> new IllegalArgumentException("Profile not found for Id " + id));
    }

    @Override
    public LiveUser findLiveUserById(Long id, Integer mvnoId) {
        try {
//			log.info(String.format("getting liveuser for %d", id));
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id))
                throw new IllegalArgumentException("Please enter valid live user id.");
            QLiveUser qLiveUser = QLiveUser.liveUser;
            BooleanExpression boolExp = qLiveUser.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qLiveUser.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            boolExp = boolExp.and(qLiveUser.cdrID.eq(id));

            Optional<LiveUser> liveUser = liveUserRepository.findOne(boolExp);
            if (!liveUser.isPresent()) {
                throw new IllegalArgumentException(
                        "No record found with live user id " + id + " . Please enter valid live user id.");
            }
            return liveUser.get();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public LiveUser findLiveUsers(Long id, Integer mvnoId) {
        try {
//			log.info(String.format("getting liveuser for %d", id));
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id))
                throw new IllegalArgumentException("Please enter valid live user id.");
            QLiveUser qLiveUser = QLiveUser.liveUser;
            BooleanExpression boolExp = qLiveUser.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qLiveUser.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
            boolExp = boolExp.and(qLiveUser.cdrID.eq(id));

            Optional<LiveUser> liveUser = liveUserRepository.findOne(boolExp);
            if (!liveUser.isPresent()) {
                throw new RuntimeException("You do not have access to update or delete this record.");
            }
            return liveUser.get();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Page<LiveUser> findLiveUsersUsingFilter(LiveUserSearchDTO liveUserSearchDTO, Integer mvnoId) {
//		log.info(String.format("getting liveuser for %s", userName));

        QLiveUser qLiveUser = QLiveUser.liveUser;
        BooleanExpression boolExp = qLiveUser.isNotNull();
        Pageable pageable = null;

        if (liveUserSearchDTO != null) {
            if (notNullNotEmpty(liveUserSearchDTO.getUserName()))
                boolExp = boolExp.and(qLiveUser.userName.eq(liveUserSearchDTO.getUserName()));

            if (notNullNotEmpty(liveUserSearchDTO.getFramedIpAddress()))
                boolExp = boolExp.and(qLiveUser.framedIpAddress.eq(liveUserSearchDTO.getFramedIpAddress()));

            if (notNullNotEmpty(liveUserSearchDTO.getNasIpAddress()))
                boolExp = boolExp.and(qLiveUser.nasIpAddress.eq(liveUserSearchDTO.getNasIpAddress()));

            if (notNullNotEmpty(liveUserSearchDTO.getClassAttribute()))
                boolExp = boolExp.and(qLiveUser.lClass.eq(liveUserSearchDTO.getClassAttribute()));

            if (notNullNotEmpty(liveUserSearchDTO.getAcctStatusType()))
                boolExp = boolExp.and(qLiveUser.acctStatusType.eq(liveUserSearchDTO.getAcctStatusType()));

            if (notNullNotEmpty(liveUserSearchDTO.getNasIdentifier()))
                boolExp = boolExp.and(qLiveUser.nasIdentifier.eq(liveUserSearchDTO.getNasIdentifier()));

            if (notNullNotEmpty(liveUserSearchDTO.getNasPortId()))
                boolExp = boolExp.and(qLiveUser.nasPortId.eq(liveUserSearchDTO.getNasPortId()));

            if (notNullNotEmpty(liveUserSearchDTO.getNasPortType()))
                boolExp = boolExp.and(qLiveUser.nasPortType.eq(liveUserSearchDTO.getNasPortType()));

            if (notNullNotEmpty(liveUserSearchDTO.getFramedIpv6Address()))
                boolExp = boolExp.and(qLiveUser.framedipv6address.eq(liveUserSearchDTO.getFramedIpv6Address()));

            if (notNullNotEmpty(liveUserSearchDTO.getFramedRoute()))
                boolExp = boolExp.and(qLiveUser.framedRoute.eq(liveUserSearchDTO.getFramedRoute()));

            if (notNullNotEmpty(liveUserSearchDTO.getSourceIpAddress()))
                boolExp = boolExp.and(qLiveUser.sourceipaddress.eq(liveUserSearchDTO.getSourceIpAddress()));

            if (notNullNotEmpty(liveUserSearchDTO.getAcctSessionId()))
                boolExp = boolExp.and(qLiveUser.acctSessionId.eq(liveUserSearchDTO.getAcctSessionId()));

            if (notNullNotEmpty(liveUserSearchDTO.getAcctMultiSessionId()))
                boolExp = boolExp.and(qLiveUser.acctMultiSessionId.eq(liveUserSearchDTO.getAcctMultiSessionId()));

            if (notNullNotEmpty(liveUserSearchDTO.getCustId()))
                boolExp = boolExp.and(qLiveUser.custid.eq(liveUserSearchDTO.getCustId()));


            if (notNullNotEmpty(liveUserSearchDTO.getCallingStationId())) {
//                boolExp = boolExp.and(qLiveUser.callingStationId.likeIgnoreCase("%" + liveUserSearchDTO.getCallingStationId().replaceAll("[:.\\-]", "%") + "%"));
                String normalizedCallingStationId = liveUserSearchDTO.getCallingStationId().replaceAll("[:.\\-]", "");

                boolExp = boolExp.and(
                        Expressions.stringTemplate("REPLACE(REPLACE(REPLACE({0}, ':', ''), '-', ''), '.', '')", qLiveUser.callingStationId)
                                .eq(normalizedCallingStationId)
                );
            }

            if (liveUserSearchDTO.getPage() > 0) {
                liveUserSearchDTO.setPage(liveUserSearchDTO.getPage() - 1);
            } else {
                liveUserSearchDTO.setPage(0);
                liveUserSearchDTO.setSize(Integer.MAX_VALUE);
            }

            if (liveUserSearchDTO.getPage() > 0 && liveUserSearchDTO.getSize() > 0) {
                pageable = PageRequest.of(liveUserSearchDTO.getPage(), liveUserSearchDTO.getSize(), Sort.by(Sort.Direction.DESC, "lastmodifiedDate"));
            }

            if (!(StringUtils.isBlank(liveUserSearchDTO.getFromDate()) || liveUserSearchDTO.getFromDate().equalsIgnoreCase("null"))) {
                boolExp = boolExp.and(qLiveUser.lastmodifiedDate.eq(Timestamp.valueOf(liveUserSearchDTO.getFromDate() + " 00:00:00"))
                        .or(qLiveUser.lastmodifiedDate.after(Timestamp.valueOf(liveUserSearchDTO.getFromDate() + " 00:00:00"))));
            }
            if (!(StringUtils.isBlank(liveUserSearchDTO.getToDate()) || liveUserSearchDTO.getToDate().equalsIgnoreCase("null"))) {
                boolExp = boolExp.and(qLiveUser.lastmodifiedDate.eq(Timestamp.valueOf(liveUserSearchDTO.getToDate() + " 23:59:59"))
                        .or(qLiveUser.lastmodifiedDate.before(Timestamp.valueOf(liveUserSearchDTO.getToDate() + " 23:59:59"))));
            }
        }

        if (ValidateCrudTransactionData.validateMvnoId(mvnoId) != 1) {
            boolExp = boolExp.and(qLiveUser.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));

        }
        Page<LiveUser> liveUsers = null;
        if (liveUserSearchDTO != null && liveUserSearchDTO.getPage() > 0 && liveUserSearchDTO.getSize() > 0) {
            liveUsers = liveUserRepository.findAll(boolExp, pageable);
        } else {
            liveUsers = new PageImpl<LiveUser>((List<LiveUser>) liveUserRepository.findAll(boolExp));
        }
        for (LiveUser liveUser : liveUsers.getContent()) {
            if (liveUser.getCallingStationId() != null) {
                liveUser.setMacAddress(liveUser.getCallingStationId().replaceAll("[-:.]", ":"));
            }
            if (liveUser.getCprId() != null && liveUser.getCprId() != 0) {
                String planName = custPlanMappingRepository.getPlanNameFromCPRID(liveUser.getCprId());
                liveUser.setPlanName(planName);
            }
        }
        return liveUsers;
    }

    @Override
    public List<String> findUserStatusOnlineOrOffline(UsersDto usersDto) {
        List<String> onlineUsers = new ArrayList<>();
        if (usersDto != null && usersDto.getUsers() != null && usersDto.getUsers().size() > 0)
            onlineUsers = liveUserRepository.findUsernameByStatus(usersDto.getUsers());
        return onlineUsers;
    }

    @Override
    public void dummyEntries(String userName, String pwd) {
//        LiveUser liveUser = new LiveUser();
//        liveUser.setNasIpAddress("192.168.1.1");
//        liveUser.setFramedIpAddress("192.168.1.1");
//        liveUser.setlClass("no");
//        liveUser.setCallingStationId("ACBHDHDHDHDDH");
//        liveUser.setAcctStatusType("Start");
//        liveUser.setAcctInputOctets("1024");
//        liveUser.setAcctOutputOctets("1024");
//        liveUser.setAcctSessionId("130408");
//        liveUser.setAcctSessionTime("3000");
//        liveUser.setMvnoId(1);
        for (int i = 0; i < 1000; i++) {
            LiveUser liveUser = new LiveUser();
            liveUser.setNasIpAddress("192.168.1.1");
            liveUser.setFramedIpAddress("192.168.1.1");
            liveUser.setlClass("no");
            liveUser.setCallingStationId("ACBHDHDHDHDDH");
            liveUser.setAcctStatusType("Start");
            liveUser.setAcctInputOctets("1024");
            liveUser.setAcctOutputOctets("1024");
            liveUser.setAcctSessionId("130408");
            liveUser.setAcctSessionTime("3000");
            liveUser.setMvnoId(1);
            liveUser.setUserPassword(pwd);
            liveUser.setUserName(userName + i);
            liveUser.setUserPassword(pwd);
            liveUserRepository.save(liveUser);
        }
    }

    @Override
    public List<LiveUser> findLiveUsersByMacAddress(String mac) {
        return liveUserRepository.findAllByCallingStationId(mac);
    }

    @Override
    public List<LiveUser> findLiveUsersByLClass(String username) {
        return liveUserRepository.findLiveUsersByLClass(username);
    }

    @Override
    public boolean existsWithUsername(String username) {
        return false;
    }

    @Override
    @Transactional
    public void delete(List<Long> id, Integer mvnoId) {
        findLiveUsers(id, ValidateCrudTransactionData.validateMvnoId(mvnoId));
        int count = liveUserRepository.deleteByCdrIDIn(id);
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        log.info(String.format("Live user deleted successfully: %s", count));
    }

    @Override
    public void disconnectLiveUsersOfStaleSession(List<LiveUser> sessionIdToPurgeSessions, Integer mvnoId) {
        log.info("Disconnect LiveUsers For Session size: " + sessionIdToPurgeSessions.size());
        DBAccountingDriver accountingDriver = new DBAccountingDriver();
        DBAuthenticationDriver dbAuthenticationDriver = new DBAuthenticationDriver();
        for (LiveUser liveUser : sessionIdToPurgeSessions) {
            try {
                //TODO: Below methods needs to be handle by multiple thread
                CustomerData customerData = dbAuthenticationDriver.getDBCustomer(liveUser.getUserName(), liveUser.getMvnoId(), liveUser.getCustid(), null, false);
                if (customerData != null && customerData.getUsername() != null) {
                    log.debug(String.format("Processing user from live user:  name and id: %s  %s", customerData.getUsername(), customerData.getCustid()));
//                    updateUsageQuota(customerData, accountingDriver);
                    updateUsageQuotaBySession(liveUser, customerData, accountingDriver);
                    log.debug(String.format("Successfully user has been removed from live user:  name and id: %s  %s", customerData.getUsername(), customerData.getCustid()));
                } else {
                    log.debug("Customer not found for live User: " + liveUser.getUserName() + " So skip stale live user session ");
                }
                //update live session and add in cdr
                accountingDriver.insertCDR(liveUser, null);
                accountingDriver.deleteDBSession(liveUser.getAcctSessionId(), liveUser.getNasIpAddress(), liveUser.getAcctMultiSessionId());
            } catch (SQLException e) {
                log.error("Error In disconnectLiveUsersOfStaleSession : " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException(e);
            }

        }


    }

    @Override
    public Long countByCustId(String custId) {
        try {
            return liveUserRepository.countLiveUserByCustid(custId);
        } catch (Exception ex) {
            log.error("Error to find Live Session count by custId: " + custId);
        }
        return 0L;
    }

    @Override
    public List<LiveUser> findLiveUserByCdrId(List<Long> cdrIds) {
        return liveUserRepository.getLiveUsersByCdrIDIn(cdrIds);
    }

    private void updateUsageQuotaBySession(LiveUser liveUser, CustomerData customerData, DBAccountingDriver dbAcct) {
        try {
            log.info("In updateUsageQuotaBySession sessionId: " + liveUser.getAcctSessionId() + " user: " + customerData.getUsername() + " Date Time: " + LocalDateTime.now());
            RadiusAsyncUtility radiusAsyncUtility = new RadiusAsyncUtility();
            RadiusUtility radiusUtility = new RadiusUtility();
            AuthAcctUtilityServiceImpl authAcctUtilityImpl = new AuthAcctUtilityServiceImpl();
//            CustQuotaDetailsPerSession existingDetailsPerSession = dbAcct.getQuotaSession(liveUser.getAcctSessionId());
            //update session actual quota
            String quotaUnit = "Byte";
            CustomerPlanData customerPlanData = null;
            if (customerData.getCustomerBasePlan() != null) {
                quotaUnit = customerData.getCustomerBasePlan().get(0).getQuotaunit();
                customerPlanData = customerData.getCustomerBasePlan().get(0);
            } else if (customerData.getCustomerAllPlan() != null) {
                quotaUnit = customerData.getCustomerAllPlan().get(0).getQuotaunit();
                customerPlanData = customerData.getCustomerAllPlan().get(0);
            }

            double usedQuota = 0d;
            double totalSessionUsed = 0d;
            if (liveUser != null && !liveUser.getAcctStatusType().equalsIgnoreCase("Start")) {
                double totalSessionUsage = 0;
                if (liveUser.getAcctInputOctets() != null)
                    totalSessionUsage = totalSessionUsage + Double.parseDouble(liveUser.getAcctInputOctets());
                if (liveUser.getAcctOutputOctets() != null)
                    totalSessionUsage = totalSessionUsage + Double.parseDouble(liveUser.getAcctOutputOctets());

                usedQuota = radiusUtility.convertUsageToGivenUnit(totalSessionUsage, quotaUnit);
                totalSessionUsed = usedQuota;
                if (customerPlanData != null)
                    usedQuota = usedQuota + customerPlanData.getVolumebasedusedquota();
            } else if(liveUser.getAcctStatusType().equalsIgnoreCase("Start")) {
                usedQuota = customerPlanData.getVolumebasedusedquota();
            }

            //update actual quota
            CustomerQuotaInfo custQuotaInfo = new CustomerQuotaInfo();
            custQuotaInfo.setCustId(customerData.getCustid());
            custQuotaInfo.setUserName(customerData.getUsername());
            custQuotaInfo.setMvnoId(Long.valueOf(customerData.getMvnoId()));
            custQuotaInfo.setVolumeBasedUsedQuota(usedQuota);
            Double sessionUsageTime = 0d;
            if (liveUser.getAcctSessionTime() != null)
                sessionUsageTime = Double.valueOf(liveUser.getAcctSessionTime());

            custQuotaInfo.setTimeBasedUsedQuota(sessionUsageTime);

            if(customerPlanData != null && !liveUser.getAcctStatusType().equalsIgnoreCase("Start")) {
                double currentSessionData = customerPlanData.getCurrentsessionusagevolume() - totalSessionUsed;
                custQuotaInfo.setVolumeBasedSessionUsedQuota(currentSessionData);
            } else {
                custQuotaInfo.setVolumeBasedSessionUsedQuota(0d);
            }
            custQuotaInfo.setTimeBasedSessionUsedQuota(0d);
            custQuotaInfo.setVolumeBasedTotalQuota(customerPlanData.getVolumebasedtotalquota());

            custQuotaInfo.setTimeBasedTotalQuota(0d);
            custQuotaInfo.setSkipQuotaReset(false);
            radiusAsyncUtility.updateQuotaInfoProcess(custQuotaInfo, customerData, 0d, String.valueOf(usedQuota), sessionUsageTime, customerPlanData, dbAcct, radiusUtility);
            authAcctUtilityImpl.updateAcountingQuotaUse(custQuotaInfo, customerData.getUsername(), String.valueOf(usedQuota), String.valueOf(sessionUsageTime), customerPlanData.getCustpackageid(), dbAcct, customerData.isFreeQuota(), radiusUtility);
            log.debug("Total Quota Updated");
            if (customerPlanData.getReservedQuotaInPer() != null && customerPlanData.isChunkAvailable() && customerPlanData.getTotalReservedQuota() > 0) {
                double totalReservedQuota = customerPlanData.getTotalReservedQuota();
                if (totalReservedQuota >= 0) {
                    // Delete quota from tblreservedquotadtls
                    dbAcct.updateReservedQuotaForChild(customerData.getUsername(), totalReservedQuota);
//					log.debug("Reserved quota updated for customer: " + username + " available reserved quota: " + totalReservedQuota);
                    CustomerServiceImpl customerServiceImpl = new CustomerServiceImpl();
                    customerServiceImpl.sendReservedQuotaUpdateToAPIGateway(customerPlanData.getCustpackageid(), true, totalReservedQuota);
                    dbAcct.deleteReservedQuotaDtls(customerData.getCustid());
                }
            }
        } catch (Exception ex) {
            log.error("Error to update current session data for sessionId:  " + liveUser.getAcctSessionId() + ", error: " + ex.getMessage());
        }


    }

    @Override
    public void disconnectLiveUsers(List<Long> ids, Integer mvnoId, boolean isDisconnect) {
        DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();
        Iterable<LiveUser> liveUsers = findLiveUsers(ids, mvnoId);

        Iterator<LiveUser> liveUserIterator = liveUsers.iterator();

        if (liveUserIterator.hasNext()) {
            LiveUser liveUser = liveUserIterator.next();

            List<changeUserData> userList = new ArrayList<>();
            CustomerData custRetrunData = null;
            try {
                custRetrunData = dbAuth.getDBCustomer(liveUser.getUserName(), mvnoId, null, null, false);

                if (custRetrunData != null) {
                    changeUserData changeuserData = new changeUserData(custRetrunData.getUsername(), Long.valueOf(custRetrunData.getMvnoId()));
                    userList.add(changeuserData);
                    log.debug("changeuserData:" + changeuserData + " isDisconnect: " + isDisconnect);
                } else if (liveUsers != null) {
                    changeUserData changeuserData = new changeUserData(liveUser.getUserName(), Long.valueOf(liveUser.getMvnoId()));
                    userList.add(changeuserData);
                    log.debug("changeuserData:" + changeuserData + " isDisconnect: " + isDisconnect);
                }

                if (isDisconnect)
                    customerService.CoADMSupport(userList, "Remove", custRetrunData, "");
                else
                    customerService.triggerCOADMForSingleLiveSession(liveUser, CommonConstants.EVENTCONSTANTS.CUSTOMER_LOGOUT, "COA", custRetrunData);

            } catch (SQLException e) {
                log.error("Exception In disconnect Live User: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void disconnectLiveUsersByUsername(String username, Integer mvnoId, boolean isDisconnect) {
        List<LiveUser> liveUsers = liveUserRepository.findByUserName(username);
        if (!CollectionUtils.isEmpty(liveUsers)) {
            disconnectLiveUsers(liveUsers.stream().map(LiveUser::getCdrID).collect(Collectors.toList()), mvnoId, isDisconnect);
        } else {
            log.error("No Session Available with username: " + username);
            throw new IllegalArgumentException("No Session Available with username: " + username);
        }
    }

    @Override
    public LiveUser findByUserNameLimit(String userName) {
        LiveUser liveUser = liveUserRepository.findFirstByUserNameOrderByCdrIDDesc(userName);
        return liveUser;
    }

    private Iterable<LiveUser> findLiveUsers(List<Long> ids, Integer mvnoId) {
        List<LiveUser> liveUsers = null;
        try {
            if (ids == null || ids.isEmpty())
                throw new IllegalArgumentException("Please enter valid live user ids.");
            QLiveUser qLiveUser = QLiveUser.liveUser;
            BooleanExpression boolExp = qLiveUser.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qLiveUser.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
            boolExp = boolExp.and(qLiveUser.cdrID.in(ids));

            Iterable<LiveUser> liveUser = liveUserRepository.findAll(boolExp);
            if (!liveUser.iterator().hasNext()) {
                throw new RuntimeException("No record is found with given ids");
            }
            return liveUser;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<LiveUser> findAllLiveUserByCustId(String custid) {
        return liveUserRepository.findAllByCustid(custid);
    }

    @Override
    public String getframedIpAddress(String custId) {
        try {
            String framedIpAddress = liveUserRepository.findFramedIpAddressByCustid(custId).orElse(null);
            return framedIpAddress;
        } catch (Exception e) {
            log.error("Error to Find Live FramedIpAddress for custId: "+custId);
            throw new RuntimeException(e.getMessage());
        }
    }


}

