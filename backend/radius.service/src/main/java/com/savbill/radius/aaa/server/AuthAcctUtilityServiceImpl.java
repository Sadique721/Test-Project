package com.savbill.radius.aaa.server;

import com.savbill.radius.aaa.attribute.RadiusAttribute;
import com.savbill.radius.aaa.constant.AAAConstant;
import com.savbill.radius.aaa.data.CustomerData;
import com.savbill.radius.aaa.data.CustomerDetails;
import com.savbill.radius.aaa.data.RadiusProfileData;
import com.savbill.radius.aaa.data.redis.CacheServiceWithRedis;
import com.savbill.radius.aaa.db.DBAccountingDriver;
import com.savbill.radius.aaa.db.DBAuthenticationDriver;
import com.savbill.radius.aaa.expressions.ExpressionEvaluator;
import com.savbill.radius.aaa.packet.AccessRequest;
import com.savbill.radius.aaa.packet.AccountingRequest;
import com.savbill.radius.aaa.packet.RadiusPacket;
import com.savbill.radius.aaa.util.ValidateExpression;
import com.savbill.radius.config.CacheRetrival;
import com.savbill.radius.entity.*;
import com.savbill.radius.entity.*;
import com.savbill.radius.helper.changeUserData;
import com.savbill.radius.kafka.message.CustomerQuotaInfo;
import com.savbill.radius.services.impl.CustomerServiceImpl;
import com.savbill.radius.services.impl.DeviceDriverServiceImpl;
import com.savbill.radius.utils.CommonConstants;
import com.savbill.radius.utils.RadiusUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import static com.savbill.radius.utils.CommonConstants.*;


public class AuthAcctUtilityServiceImpl {

    private static final String REPLY_MESSAGE = "Reply-Message";
    private static final Logger log = LoggerFactory.getLogger(AuthAcctUtilityServiceImpl.class);

    /**
     * Used to validate customer for authentication
     *
     * @param custReturnData
     * @param accessResponse
     * @param request
     * @param cltData
     * @param strUsername
     * @param strPassword
     * @param reason
     * @param radiusProfile
     * @return
     * @throws SQLException
     */
    public CustomerData localDBValidation(CustomerData custReturnData, RadiusPacket accessResponse, AccessRequest request, Client cltData, String strUsername, String strPassword, String reason, RadiusProfile radiusProfile, VLANManagement vlanManagement) throws SQLException {
        long startTime = System.currentTimeMillis();
        String checkItem = radiusProfile.getCheckItem();

        //TODO This is hardcoded for ACT need to provide solution in fuutre.
        if (checkItem != null && checkItem.contains("User-Name=")) {
            checkItem = checkItem.substring(checkItem.indexOf("=") + 1);
        }

        String authenticationType = RadiusUtils.readValueFromProperties("radius.authentication.type");
        authenticationType = (authenticationType != null && !authenticationType.isEmpty()) ? authenticationType : CommonConstants.AUTHENTICATION_TYPE_DEPENDENT;
        String deviceDriverName = radiusProfile.getDeviceDriverName();
        String authenticationMode = radiusProfile.getAuthenticationMode();
        boolean isPasswordCheck = radiusProfile.isPasswordCheckRequired();
        deviceDriverName = (deviceDriverName != null && !deviceDriverName.isEmpty()) ? deviceDriverName : CommonConstants.DEVICE_DRIVER_SAVBILL;

        RadiusUtility radUtil = new RadiusUtility();
        DBAuthenticationDriver dbAuthDrive = new DBAuthenticationDriver();
        CustomerData parentCust = null;
        boolean isParentQuotaUsed = false;
        Double totalReservedQuota = 0.0;
        double reservedQuota = 0;
        String parentUserName = null;
        String mac_attr = cltData.getClientGroupData().getCustomerMacAttribute();
        if (mac_attr == null) {
            mac_attr = "Calling-Station-Id";
        }
        log.debug("Mac address attribute: " + mac_attr);
        String strPacketUsername;
        String user_name_attr = radiusProfile.getCustomerUserNameAttribute();//cltData.getClientGroupData().getCustomerUserNameAttribute();
        log.debug("User-Name attribute: " + user_name_attr);
        if (user_name_attr == null || user_name_attr.trim().isEmpty() || request.getAttribute(user_name_attr.trim()) == null) {
            user_name_attr = "User-Name";
        }
        strPacketUsername = request.getAttribute(user_name_attr).getAttributeValue();

        String userNameRegex = radiusProfile.getUsernameIdentityRegex();//cltData.getClientGroupData().getUsernameIdentityRegex();
        if (userNameRegex != null && !userNameRegex.isEmpty()) {
            String regExUsername = null;
            regExUsername = radUtil.extractValueFromRegex(strPacketUsername, userNameRegex);
            if (regExUsername != null) {
                strUsername = regExUsername;
            }
            log.info("After apply regex: " + userNameRegex + " strUsername: " + strUsername + ":ProcessUsername:" + strPacketUsername);
        } else {
            strUsername = strPacketUsername;
        }
        String acct_session_attr = cltData.getClientGroupData().getDynamicAcctSessionAttribute();
        if (acct_session_attr == null) {
            acct_session_attr = "Acct-Session-Id";
        }
        String strAcctSessionId = null;
        if (request.getAttribute(acct_session_attr) != null) {
            strAcctSessionId = request.getAttribute(acct_session_attr).getAttributeValue();
            log.info("Acct-Session-Id: " + strAcctSessionId);
        }
        String strCalling = getRequestAttribute(request, mac_attr);
        String strCalled = getRequestAttribute(request, "Called-Station-Id");
        String strIPAddress = getRequestAttribute(request, "Framed-IP-Address");
        String eventName = "";

        //TODO This is hardcoded for ACT need to solve.
        if (checkItem != null && checkItem.contains("@") && strUsername.contains("@")) {
            strUsername = strUsername.substring(0, strUsername.lastIndexOf("@"));
        }
        if (deviceDriverName.equalsIgnoreCase(CommonConstants.DEVICE_DRIVER_SAVBILL)) {
            custReturnData = radUtil.getCustomerDetailsForAccessRequest(custReturnData, authenticationMode, strIPAddress, request, cltData, strUsername, strPassword, strCalling, isPasswordCheck, "Auth");
            custReturnData.setSavbillBSSDb(true);
        } else {
            custReturnData = new DeviceDriverServiceImpl().isUserExist(deviceDriverName, strUsername, strPassword, cltData.getMvnoId());
            if (custReturnData.getUsername() != null) {
                log.debug(String.format("LDAP User found: %s", custReturnData.getUsername()));
                custReturnData.setMacflow(true);
                authenticationType = CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT;
            }
            custReturnData.setSavbillBSSDb(false);
        }

        logCustomerData(custReturnData);

        // If customer is not found with request username and mac then check in Cache is mac key available then
        CacheServiceWithRedis cacheService = CacheServiceWithRedis.getInstance();
        if (custReturnData == null || custReturnData.getUsername() == null) {
            CustomerDetails customerDetails = (CustomerDetails) cacheService.get(radUtil.normalizeMacAddress(strCalling));//cacheService.get(strCalling);
            if (customerDetails != null) {
                log.debug("User-name found In Mac-Address Cache: " + customerDetails.toString());
                if (customerDetails.getUserName().equals(strUsername) && customerDetails.getClass() != null) {
                    strUsername = customerDetails.getUser_class();
                    log.debug("User-name get from cache class: " + strUsername);
                } else {
                    strUsername = customerDetails.getUserName();
                    log.debug("User-name get from cache user-name: " + strUsername);
                }
                custReturnData = radUtil.getCustomerDetailsForAccessRequest(custReturnData, authenticationMode, strIPAddress, request, cltData, strUsername, strPassword, strCalling, isPasswordCheck, "Auth");
//				cacheService.remove(strCalling);
                log.debug(String.format("Cache Customer Data is  : %s", custReturnData.toString()));
            }
        }

        // set vlan management if not null
        if (vlanManagement != null && custReturnData != null) {
            custReturnData.setVlanManagement(vlanManagement);
        }
        if (custReturnData.getUsername() == null || AAAConstant.REPLYMSG_PASSWORDFAIL.equalsIgnoreCase(custReturnData.getStrReplyMessage())) {
            log.error("customer Username not found go for unknow profile..!");
            handleUnknownUser(custReturnData, accessResponse, cltData, reason, strUsername, request);
            return custReturnData;
        } else if (CommonConstants.CUST_SUSPEND.equalsIgnoreCase(custReturnData.getStatus())) {
            log.error("customer Status is Suspendgo for Suspend profile..!");
            handleSuspendedUser(custReturnData, accessResponse, cltData, reason, strUsername, request);
            return custReturnData;
        }
        boolean isSameLocation = true;
        if (custReturnData.isMacflow() && strCalled != null) {
            CustomerData childData = dbAuthDrive.getParentCustByMac(strCalled, strCalling, cltData.getMvnoId());
            if (childData != null) {
                childData.setSavbillBSSDb(custReturnData.isSavbillBSSDb());
                custReturnData = childData;
                log.debug(String.format("Parent quota available for location %s", strCalled));
                log.debug(String.format("Mac authentication success for independent customer and Free quota available on location: %s", strCalled));
                log.debug(String.format("Child customer found for location %s", strCalled));
            } else {
                log.debug(String.format("Child customer not found for location %s", strCalled));
                isSameLocation = false;
            }
        }

        if (custReturnData.getParentCustId() != 0 && isSameLocation) {
            parentCust = dbAuthDrive.getDBCustomer(null, custReturnData.getMvnoId(), String.valueOf(custReturnData.getParentCustId()), null, true);
        }
        long endTimeAfterverifyParent = System.currentTimeMillis();
        log.debug("Time Taken: for localDBValidation: after endTimeAfterverifyCustomer   " + (endTimeAfterverifyParent - startTime));

        if (parentCust != null && custReturnData.getStatus() != null) {
            parentUserName = parentCust.getUsername();
            if (!CommonConstants.PLAN_INACTIVE.equalsIgnoreCase(parentCust.getStatus()) && parentCust.isAuthStatus()) {
                accessResponse.setPacketType(AAAConstant.ACCESS_ACCEPT);
                log.debug("Parent has active plan, child will use parent shareable data");
            } else if (!CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT.equalsIgnoreCase(authenticationType)) {
                handleInactiveUser(custReturnData, accessResponse, cltData, reason, request);
                return custReturnData;
            }
        } else if (CommonConstants.PLAN_INACTIVE.equalsIgnoreCase(custReturnData.getStatus()) && !CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT.equalsIgnoreCase(authenticationType)) {
            handleInactiveUser(custReturnData, accessResponse, cltData, reason, request);
            return custReturnData;
        }

        boolean isAuthSuccess = custReturnData.isAuthStatus();

        if (parentCust != null) {
            isAuthSuccess = parentCust.isAuthStatus();
            if (!AAAConstant.REPLYMSG_PASSWORDFAIL.equalsIgnoreCase(custReturnData.getStrReplyMessage())) {
                custReturnData.setAuthStatus(true);
            } else {
                parentCust.setAuthStatus(false);
                isAuthSuccess = false;
                parentCust.setStrReplyMessage(custReturnData.getStrReplyMessage());
            }
        }

        if (isAuthSuccess) {
            /* Authorization */
            log.info("Authorization Started");
            List<DynamicAttributeMapping> dynamicAttributeMappingList = cltData.getClientGroupData().getDynamicAttributeMappings();
            if (dynamicAttributeMappingList != null) {
                // Check Dynamic Validation
                radUtil.validateDynamicAttribute(cltData, dynamicAttributeMappingList, custReturnData, request, accessResponse, eventName);
            } else {
                log.debug("No Authorization Attribute Configured");
            }
            if (custReturnData.getStrReplyMessage().contains(" Validation Fail")) {
                eventName = CommonConstants.AuthResponseEvent.DYNAMIC_VALIDATION_FAIL;
                isAuthSuccess = false;
            }
            long dynEndTime = System.currentTimeMillis();
            log.info("Time Taken: for dynamicAttributeMapping:" + (dynEndTime - startTime));

            log.info("Authorization Completed");

            boolean quotaConsumed = false;
            double volumeQuota = (double) 0;
            boolean noBooster = true;
            long timeQuota = 0;
            long seconds = 0;
            double sessionUsedQuota = 0d;
            //Need to check customer parent quota, From this need to check parent shared quota
            if (custReturnData.getParentCustId() != 0) {
                if (parentCust.getCustomerBasePlan() == null && isAuthSuccess && !authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT)) {
                    if (custReturnData.getCustomerBasePlan() == null && isAuthSuccess) {
                        accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                        custReturnData.setAuthStatus(false);
                        custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_BASE_PLANEXPIRED);
                    }
                } else if (parentCust.getCustomerBasePlan() != null && isAuthSuccess && !authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT)) {
                    // Check parent basePlan
                    if (parentCust.getCustomerBasePlan().get(0).getEnddate() != null && parentCust.isAuthStatus()) {
                        Timestamp timestampTomorrow = new Timestamp(parentCust.getCustomerBasePlan().get(0).getEnddate().getTime());
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        long diffInMS = parentCust.getCustomerBasePlan().get(0).getEnddate().getTime() - timestamp.getTime();
                        seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMS);

                        log.debug("EndDate:" + parentCust.getCustomerBasePlan().get(0).getEnddate() + ":currentDate:" + timestamp + ":User:" + parentCust.getUsername() + ":Expiry Difference in milliseconds is:" + diffInMS + ":and seconds is:" + seconds);

                        if (seconds <= 0) {
                            accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                            custReturnData.setAuthStatus(false);
                            custReturnData.setStrReplyMessage("Parent Customer is Expired");
                        }
                        if (seconds > 1296000) {
                            seconds = 1296000;
                        }
                        timeQuota = (int) seconds;
                        isParentQuotaUsed = true;
                    }
                    // Check child basePlan
                    else if (custReturnData.getCustomerBasePlan().get(0).getEnddate() != null && isAuthSuccess) {
                        log.debug("End Date is Null for:" + parentCust.getUsername() + ":Continue with child:");
                        Timestamp timestampTomorrow = new Timestamp(custReturnData.getCustomerBasePlan().get(0).getEnddate().getTime());
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        long diffInMS = custReturnData.getCustomerBasePlan().get(0).getEnddate().getTime() - timestamp.getTime();
                        seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMS);

                        log.debug("EndDate:" + custReturnData.getCustomerBasePlan().get(0).getEnddate() + ":currentDate:" + timestamp + ":User:" + custReturnData.getUsername() + ":Expiry Difference in milliseconds is:" + diffInMS + ":and seconds is:" + seconds);

                        if (seconds <= 0) {
                            accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                            custReturnData.setAuthStatus(false);
                            custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_CUSTOMER_EXPIRED);
                        }
                        if (seconds > 1296000) {
                            seconds = 1296000;
                        }
                        timeQuota = (int) seconds;
                    } else {
                        log.debug("End Date is Null for:" + custReturnData.getUsername() + ":Continue:");
                    }
                } else if (custReturnData.getCustomerBasePlan() == null && isAuthSuccess && !authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT)) {
                    // child customer check
                    accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                    custReturnData.setAuthStatus(false);
                    custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_BASE_PLANEXPIRED);
                }
                long endTimeAftertimeCalculte = System.currentTimeMillis();
                log.info("Time Taken: for localDBValidation: after endTimeAftertimeCalculte parent   " + (endTimeAftertimeCalculte - startTime));
            } else if (!authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT)) {
                // check child customer quota and plans
                if (custReturnData.getCustomerBasePlan() == null && isAuthSuccess) {
                    accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                    custReturnData.setAuthStatus(false);
                    custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_BASE_PLANEXPIRED);
                    isAuthSuccess = false;
                }
                // check if base plan is active then process else customer is expired
                else if (custReturnData.getCustomerBasePlan().get(0).getEnddate() != null && isAuthSuccess) {
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    long diffInMS = custReturnData.getCustomerBasePlan().get(0).getEnddate().getTime() - timestamp.getTime();
                    seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMS);

                    log.debug("EndDate:" + custReturnData.getCustomerBasePlan().get(0).getEnddate() + ":currentDate:" + timestamp + ":User:" + custReturnData.getUsername() + ":Expiry Difference in milliseconds is:" + diffInMS + ":and seconds is:" + seconds);

                    if (seconds <= 0) {
                        accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                        custReturnData.setAuthStatus(false);
                        custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_CUSTOMER_EXPIRED);
                    }
                    if (seconds > 1296000) {
                        seconds = 1296000;
                    }
                    timeQuota = (int) seconds;
                } else {
                    log.debug("End Date is Null for:" + custReturnData.getUsername() + ":Continue:");
                }
                long endTimeAftertimeCalculte = System.currentTimeMillis();
                log.info("Time Taken: for localDBValidation: after endTimeAftertimeCalculte   " + (endTimeAftertimeCalculte - startTime));
            }

            //will fetch parent data from getDBCustomer with custId

            //Checking valume booster plan quota
            if (custReturnData.getCustomerVolueBooster() != null && isAuthSuccess) {
                if (custReturnData.getCustomerVolueBooster().get(0) != null) {
                    if (isAuthSuccess && (custReturnData.getCustomerVolueBooster().get(0).getQuotatype().equalsIgnoreCase("Data") || custReturnData.getCustomerVolueBooster().get(0).getQuotatype().equalsIgnoreCase("Both"))) {
                        sessionUsedQuota = custReturnData.getCustomerBasePlan().get(0).getCurrentsessionusagevolume();
                        volumeQuota = custReturnData.getCustomerVolueBooster().get(0).getVolumebasedunusedquota() - sessionUsedQuota;
                        volumeQuota = radUtil.convertUsageToBytes(volumeQuota, custReturnData.getCustomerVolueBooster().get(0).getQuotaunit());
                        log.debug("Booster Volume Quota ==" + volumeQuota);
                        if (volumeQuota > 0 || custReturnData.getCustomerVolueBooster().get(0).isAllowoverusage()) {
                            noBooster = false;
                            custReturnData.getCustomerBasePlan().get(0).setVolumequota(volumeQuota);
                        }
                    }

                    if (isAuthSuccess && (custReturnData.getCustomerVolueBooster().get(0).getQuotatype().equalsIgnoreCase("Time") || custReturnData.getCustomerVolueBooster().get(0).getQuotatype().equalsIgnoreCase("Both"))) {
                        timeQuota = radUtil.convertUsageToSec((long) custReturnData.getCustomerVolueBooster().get(0).getTimebasedunusedquota(), custReturnData.getCustomerVolueBooster().get(0).getTimequotaunit());
                        log.debug("Booster Time Quota==" + timeQuota);
                        if ((seconds > 0 && timeQuota > 0) || custReturnData.getCustomerVolueBooster().get(0).isAllowoverusage()) {
                            noBooster = false;
                            custReturnData.getCustomerBasePlan().get(0).setTimequota(timeQuota);
                        }
                    }
                }
                long endDataAftertimeCalculte = System.currentTimeMillis();
                log.info("Time Taken: for localDBValidation: after endDataAftertimeCalculte   " + (endDataAftertimeCalculte - startTime));
            }

            if (parentCust != null && !authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT)) {
                if (parentCust.getCustomerBasePlan() == null && isAuthSuccess) {
                    if (custReturnData.getCustomerBasePlan() == null && isAuthSuccess) {
                        accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                        custReturnData.setAuthStatus(false);
                        custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_BASE_PLANEXPIRED);
                    }
                }
            }

            if (!authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT)) {
                if (noBooster && parentCust != null && !authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT)) {
                    if (noBooster && isAuthSuccess && !parentCust.getCustomerBasePlan().get(0).isUsagereached() &&
                            (parentCust.getCustomerBasePlan().get(0).getQuotatype().equalsIgnoreCase("Data") || parentCust.getCustomerBasePlan().get(0).getQuotatype().equalsIgnoreCase("Both"))) {

                        sessionUsedQuota = parentCust.getCustomerBasePlan().get(0).getCurrentsessionusagevolume();
                        volumeQuota = parentCust.getCustomerBasePlan().get(0).getVolumebasedunusedquota() - sessionUsedQuota;

                        if (parentCust.getCustomerBasePlan().get(0).isChunkAvailable()) {
                            if (parentCust.getCustomerBasePlan().get(0).getTotalReservedQuota() != null) {
                                double existingTotalReservedQuota = parentCust.getCustomerBasePlan().get(0).getTotalReservedQuota();
                                log.debug("Existing Reserved Quota ==" + existingTotalReservedQuota);
                                volumeQuota -= existingTotalReservedQuota;
                            }
                        }
                        volumeQuota = radUtil.convertUsageToBytes(volumeQuota, parentCust.getCustomerBasePlan().get(0).getQuotaunit());
                        log.debug("Volume Quota ==" + volumeQuota);
                        if (volumeQuota <= 0d && !parentCust.getCustomerBasePlan().get(0).isAllowoverusage()) {
                            accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                            parentCust.setAuthStatus(false);
                            parentCust.setStrReplyMessage(AAAConstant.REPLYMSG_CUSTOMER_QUOTA);
                            quotaConsumed = true;
                        } else {
                            parentCust.getCustomerBasePlan().get(0).setVolumequota(volumeQuota);
                        }

                        if (parentCust.getCustomerBasePlan().get(0).isChunkAvailable()) {
                            if (parentCust.getCustomerBasePlan().get(0).getReservedQuotaInPer() != null) {
                                reservedQuota = (Double.valueOf(parentCust.getCustomerBasePlan().get(0).getReservedQuotaInPer()) * parentCust.getCustomerBasePlan().get(0).getVolumebasedtotalquota()) / 100;
                                if (parentCust.getCustomerBasePlan().get(0).getTotalReservedQuota() != null) {
                                    totalReservedQuota = totalReservedQuota + parentCust.getCustomerBasePlan().get(0).getTotalReservedQuota() + reservedQuota;
                                } else {
                                    totalReservedQuota = reservedQuota;
                                }
                            }
                        }
                    }
                    long endTimeDataParent = System.currentTimeMillis();
                    log.info("Time Taken: for localDBValidation: after endTimeDataParent   " + (endTimeDataParent - startTime));
                } else if (noBooster && isAuthSuccess && !custReturnData.getCustomerBasePlan().get(0).isUsagereached() &&
                        (custReturnData.getCustomerBasePlan().get(0).getQuotatype().equalsIgnoreCase("Data") || custReturnData.getCustomerBasePlan().get(0).getQuotatype().equalsIgnoreCase("Both"))) {

                    sessionUsedQuota = custReturnData.getCustomerBasePlan().get(0).getCurrentsessionusagevolume();
                    volumeQuota = custReturnData.getCustomerBasePlan().get(0).getVolumebasedunusedquota() - sessionUsedQuota;

                    if (custReturnData.getCustomerBasePlan().get(0).isChunkAvailable()) {
                        if (custReturnData.getCustomerBasePlan().get(0).getTotalReservedQuota() != null) {
                            double existingTotalReservedQuota = custReturnData.getCustomerBasePlan().get(0).getTotalReservedQuota();
                            log.debug("Existing Reserved Quota ==" + existingTotalReservedQuota);
                            volumeQuota -= existingTotalReservedQuota;
                        }
                    }
                    volumeQuota = radUtil.convertUsageToBytes(volumeQuota, custReturnData.getCustomerBasePlan().get(0).getQuotaunit());
                    log.debug("Volume Quota ==" + volumeQuota);

                    if (volumeQuota <= 0d && !custReturnData.getCustomerBasePlan().get(0).isAllowoverusage()) {
                        accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                        custReturnData.setAuthStatus(false);
                        custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_CUSTOMER_QUOTA);
                        quotaConsumed = true;
                    } else {
                        custReturnData.getCustomerBasePlan().get(0).setVolumequota(volumeQuota);
                    }

                    if (custReturnData.getCustomerBasePlan().get(0).isChunkAvailable()) {
                        if (custReturnData.getCustomerBasePlan().get(0).getReservedQuotaInPer() != null) {
                            reservedQuota = (Double.valueOf(custReturnData.getCustomerBasePlan().get(0).getReservedQuotaInPer()) * custReturnData.getCustomerBasePlan().get(0).getVolumebasedtotalquota()) / 100;
                            if (custReturnData.getCustomerBasePlan().get(0).getTotalReservedQuota() != null) {
                                totalReservedQuota = totalReservedQuota + custReturnData.getCustomerBasePlan().get(0).getTotalReservedQuota() + reservedQuota;
                            } else {
                                totalReservedQuota = reservedQuota;
                            }
                        }
                    }
                }
                long endTimeData = System.currentTimeMillis();
                log.info("Time Taken: for localDBValidation: after endTimeData   " + (endTimeData - startTime));
            }

            if (!authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT)) {
                if (noBooster && isParentQuotaUsed && parentCust != null) {
                    if (noBooster && !parentCust.getCustomerBasePlan().get(0).isUsagereached() && isAuthSuccess &&
                            (parentCust.getCustomerBasePlan().get(0).getQuotatype().equalsIgnoreCase("Time") || parentCust.getCustomerBasePlan().get(0).getQuotatype().equalsIgnoreCase("Both"))) {

                        timeQuota = (int) (parentCust.getCustomerBasePlan().get(0).getTimebasedunusedquota());

                        if (parentCust.getCustomerBasePlan().get(0).isChunkAvailable()) {
                            if (parentCust.getCustomerBasePlan().get(0).getTotalReservedQuota() != null) {
                                double existingTotalReservedQuota = parentCust.getCustomerBasePlan().get(0).getTotalReservedQuota();
                                log.debug("Existing Reserved Quota ==" + existingTotalReservedQuota);
                                timeQuota -= (int) existingTotalReservedQuota;
                            }
                        }
                        timeQuota = radUtil.convertUsageToSec(timeQuota, parentCust.getCustomerVolueBooster().get(0).getTimequotaunit());
                        log.debug("Time Quota==" + timeQuota);

                        if ((seconds <= 0 || timeQuota <= 0) && !parentCust.getCustomerBasePlan().get(0).isAllowoverusage() && isAuthSuccess) {
                            accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                            custReturnData.setAuthStatus(false);
                            custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_QUOTA);
                            quotaConsumed = true;
                        } else if (isAuthSuccess) {
                            parentCust.getCustomerBasePlan().get(0).setTimequota(timeQuota);
                        }

                        if (parentCust.getCustomerBasePlan().get(0).isChunkAvailable()) {
                            if (parentCust.getCustomerBasePlan().get(0).getReservedQuotaInPer() != null) {
                                reservedQuota = (Double.valueOf(parentCust.getCustomerBasePlan().get(0).getReservedQuotaInPer()) * parentCust.getCustomerBasePlan().get(0).getTimebasedtotalquota()) / 100;
                                totalReservedQuota = parentCust.getCustomerBasePlan().get(0).getTotalReservedQuota() != null ?
                                        totalReservedQuota + parentCust.getCustomerBasePlan().get(0).getTotalReservedQuota() + reservedQuota :
                                        reservedQuota;
                            }
                        }
                    }
                    long endTimetime = System.currentTimeMillis();
                    log.info("Time Taken: for localDBValidation: after endTimetime parent   " + (endTimetime - startTime));
                } else if (noBooster && isAuthSuccess && !custReturnData.getCustomerBasePlan().get(0).isUsagereached() &&
                        (custReturnData.getCustomerBasePlan().get(0).getQuotatype().equalsIgnoreCase("Time") || custReturnData.getCustomerBasePlan().get(0).getQuotatype().equalsIgnoreCase("Both"))) {

                    timeQuota = (int) (custReturnData.getCustomerBasePlan().get(0).getTimebasedunusedquota());

                    if (custReturnData.getCustomerBasePlan().get(0).isChunkAvailable()) {
                        if (custReturnData.getCustomerBasePlan().get(0).getTotalReservedQuota() != null) {
                            double existingTotalReservedQuota = custReturnData.getCustomerBasePlan().get(0).getTotalReservedQuota();
                            log.debug("Existing Reserved Quota ==" + existingTotalReservedQuota);
                            timeQuota -= (int) existingTotalReservedQuota;
                        }
                    }
                    timeQuota = radUtil.convertUsageToSec(timeQuota, custReturnData.getCustomerBasePlan().get(0).getTimequotaunit());
                    log.debug("Time Quota==" + timeQuota);

                    if ((seconds <= 0 || timeQuota <= 0) && !custReturnData.getCustomerBasePlan().get(0).isAllowoverusage() && isAuthSuccess) {
                        accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                        custReturnData.setAuthStatus(false);
                        custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_QUOTA);
                        quotaConsumed = true;
                    } else if (isAuthSuccess) {
                        custReturnData.getCustomerBasePlan().get(0).setTimequota(timeQuota);
                    }

                    if (custReturnData.getCustomerBasePlan().get(0).isChunkAvailable()) {
                        if (custReturnData.getCustomerBasePlan().get(0).getReservedQuotaInPer() != null) {
                            reservedQuota = (Double.valueOf(custReturnData.getCustomerBasePlan().get(0).getReservedQuotaInPer()) * custReturnData.getCustomerBasePlan().get(0).getTimebasedtotalquota()) / 100;
                            totalReservedQuota = custReturnData.getCustomerBasePlan().get(0).getTotalReservedQuota() != null ?
                                    totalReservedQuota + custReturnData.getCustomerBasePlan().get(0).getTotalReservedQuota() + reservedQuota :
                                    reservedQuota;
                        }
                    }
                    long endTimetime = System.currentTimeMillis();
                    log.info("Time Taken: for localDBValidation: after endTimetime   " + (endTimetime - startTime));
                }
            }
            if (!authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT)) {
                // Check parent quota consumed
                if (parentCust != null && isAuthSuccess) {
                    if (parentCust.getCustomerBasePlan().get(0).getVolumebasedtotalquota() != 0 && !parentCust.getCustomerBasePlan().get(0).isAllowoverusage()) {
                        if (parentCust.getCustomerBasePlan().get(0).getVolumebasedunusedquota() != -1) {
                            double usage = parentCust.getCustomerBasePlan().get(0).getVolumequota();
                            if (usage <= 0) {
                                accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                                custReturnData.setAuthStatus(false);
                                custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_VOLUME_QUOTA);
                                quotaConsumed = true;
                                isAuthSuccess = false;
                            } else {
                                custReturnData.setAuthStatus(true);
                            }
                        }
                    }

                    if (parentCust.getCustomerBasePlan().get(0).getTimebasedtotalquota() != 0 && !parentCust.getCustomerBasePlan().get(0).isAllowoverusage()) {
                        if (parentCust.getCustomerBasePlan().get(0).getTimebasedunusedquota() != -1) {
                            double usage = parentCust.getCustomerBasePlan().get(0).getTimequota();
                            if (usage <= 0) {
                                accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                                custReturnData.setAuthStatus(false);
                                custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_TIME_QUOTA);
                                reason = AAAConstant.REPLYMSG_QUOTA;
                                quotaConsumed = true;
                                isAuthSuccess = false;
                            }
                        }
                    }
                } else if (custReturnData != null && isAuthSuccess) {
                    if (custReturnData.getCustomerBasePlan().get(0).getVolumebasedtotalquota() != 0 && !custReturnData.getCustomerBasePlan().get(0).isAllowoverusage()) {
                        if (custReturnData.getCustomerBasePlan().get(0).getVolumebasedunusedquota() != -1) {
                            double usage = custReturnData.getCustomerBasePlan().get(0).getVolumequota();
                            if (usage <= 0) {
                                accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                                custReturnData.setAuthStatus(false);
                                custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_VOLUME_QUOTA);
                                quotaConsumed = true;
                                isAuthSuccess = false;
                            } else {
                                custReturnData.setAuthStatus(true);
                            }
                        }
                    }

                    if (custReturnData.getCustomerBasePlan().get(0).getTimebasedtotalquota() != 0 && !custReturnData.getCustomerBasePlan().get(0).isAllowoverusage()) {
                        if (custReturnData.getCustomerBasePlan().get(0).getTimebasedunusedquota() != -1) {
                            double usage = custReturnData.getCustomerBasePlan().get(0).getTimequota();
                            if (usage <= 0) {
                                accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                                custReturnData.setAuthStatus(false);
                                custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG_TIME_QUOTA);
                                reason = AAAConstant.REPLYMSG_QUOTA;
                                quotaConsumed = true;
                                isAuthSuccess = false;
                            }
                        }
                    }
                }
                long endTimeQoutacheck = System.currentTimeMillis();
                log.info("Time Taken: for localDBValidation: after endTimeQoutacheck   " + (endTimeQoutacheck - startTime));
            }
            //If quota consumed and allow over usage is false then sent inactive profile
            if (quotaConsumed && custReturnData != null && custReturnData.getCustomerBasePlan() != null
                    && custReturnData.getCustomerBasePlan().size() > 0 && !custReturnData.getCustomerBasePlan().get(0).isAllowoverusage()) {
                log.debug(String.format("Customer Quota Consumed for plan: " + custReturnData.getCustomerBasePlan().get(0).getPlanName()));
                handleInactiveUser(custReturnData, accessResponse, cltData, reason, request);
            }
            if (parentCust != null && isAuthSuccess && custReturnData.isAuthStatus()) {
                accessResponse.setPacketType(AAAConstant.ACCESS_ACCEPT);
                parentCust.setUsername(custReturnData.getUsername());
                radUtil.processReplyItem(accessResponse, parentCust, request, cltData.getClientGroupData().getClientGroupId(), cltData, false);
                if (custReturnData.getStrClass() != null) {
                    accessResponse.addAttribute("Class", custReturnData.getStrClass());
                }
            } else if (custReturnData != null && isAuthSuccess && custReturnData.isAuthStatus()) {
                accessResponse.setPacketType(AAAConstant.ACCESS_ACCEPT);
                radUtil.processReplyItem(accessResponse, custReturnData, request, cltData.getClientGroupData().getClientGroupId(), cltData, false);
                if (custReturnData.getStrClass() != null) {
                    accessResponse.addAttribute("Class", custReturnData.getStrClass());
                }
            }

            String retentionMac = null;
            //Concurrency Check
            if (!authenticationType.equalsIgnoreCase(CommonConstants.AUTHENTICATION_TYPE_INDEPENDENT)) {
                DBAccountingDriver dbAccountingDrive = new DBAccountingDriver();
//                int currentSession = dbAccountingDrive.getNoofCustomerSessionByCustomerId(cltData.getClientGroupId(), String.valueOf(custReturnData.getCustid()));
                int currentSession = dbAccountingDrive.getNoofCustomerSession(strUsername, strCalling, cltData.getClientGroupId());
                if (custReturnData != null && isAuthSuccess && custReturnData.isAuthStatus() && cltData.getClientGroupData().isCheckConcurrency()) {
                    accessResponse.setPacketType(AAAConstant.ACCESS_ACCEPT);

                    // Check existing live user available with same MAC
//                    if (strCalling != null) {
//                        LiveUser liveUser = dbAccountingDrive.validateExistingCustomerSession(strCalling, radiusProfile.isTerminateSessionOnDuplicateMac());
//                        if (liveUser != null && liveUser.getUserName() != null) {
//                            log.debug("Existing Concurrent Session found: " + liveUser.getUserName() + ": removed:");
//                            AccountingRequest accRequest = getAccRequestFromLiveUser(liveUser);
//                            accRequest.addAttribute("Acct-Terminate-Cause", "16");
//                            //Commenting as giving error
//                            //terminateExistingUserSession(accRequest, custReturnData, strCalling, dbAccountingDrive, radiusProfileData.getDbFieldMapping());
//                        }
//                    }

                    log.debug("Concurrent Session is: " + currentSession + ": User: " + strPacketUsername);

                    if (isParentQuotaUsed && parentCust != null) {
                        if (parentCust.getMaxconcurrentsession() != null && parentCust.getMaxconcurrentsession() > 0) {
                            if (currentSession >= parentCust.getMaxconcurrentsession()) {
                                if (custReturnData != null) {
                                    custReturnData.setStrReplyMessage(AAAConstant.CONCURRENCY_CHECK_FAIL);
                                }
                                accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                                custReturnData.setAuthStatus(false);
                                isAuthSuccess = false;
                            }
                        } else if (parentCust.getCustomerBasePlan().get(0).getConcurrency() > 0) {
                            if (currentSession >= parentCust.getCustomerBasePlan().get(0).getConcurrency()) {
                                if (custReturnData != null) {
                                    custReturnData.setStrReplyMessage(AAAConstant.CONCURRENCY_CHECK_FAIL);
                                }
                                accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                                custReturnData.setAuthStatus(false);
                                isAuthSuccess = false;
                            }
                        }
                    } else if (custReturnData.getMaxconcurrentsession() != null && custReturnData.getMaxconcurrentsession() > 0) {
                        if (currentSession >= custReturnData.getMaxconcurrentsession()) {
                            if (custReturnData != null) {
                                custReturnData.setStrReplyMessage(AAAConstant.CONCURRENCY_CHECK_FAIL);
                            }
                            accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                            custReturnData.setAuthStatus(false);
                            isAuthSuccess = false;
                        }
                    } else if (currentSession >= custReturnData.getCustomerBasePlan().get(0).getConcurrency()) {
                        if (custReturnData != null) {
                            custReturnData.setStrReplyMessage(AAAConstant.CONCURRENCY_CHECK_FAIL);
                        }
                        accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
                        custReturnData.setAuthStatus(false);
                        isAuthSuccess = false;
                    }
                    if (custReturnData.getStrReplyMessage().equalsIgnoreCase(AAAConstant.CONCURRENCY_CHECK_FAIL))
                        eventName = CommonConstants.AuthResponseEvent.CONCURRENCY_FAIL;
                }
                boolean oldSessionLogout = false;
                String oldMac = null;
                //if concurrency fail and in client group flag enable for remove old session then terminate first created Session
                if (custReturnData.getStrReplyMessage().equalsIgnoreCase(AAAConstant.CONCURRENCY_CHECK_FAIL) && cltData.getClientGroupData().isLogoutOldSessionOnNew()) {
                    //Trigger COA/DM/SNMP based on Event for old session
                    LiveUser liveUser = dbAccountingDrive.getFirstSessionBasedOnCltGrpcAndCustId(custReturnData.getCustid(), cltData.getClientGroupData().getClientGroupId());
                    if (liveUser != null && liveUser.getCallingStationId() != null) {
                        String event = CommonConstants.CoaDmResonContant.TERMINATE_SESSION;
                        RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                        changeUserData changeUserData = new changeUserData(custReturnData.getUsername(), Long.valueOf(custReturnData.getMvnoId()));
                        log.debug("Triggering COA/DM For Event: " + event, " liveuser session: " + liveUser.getAcctSessionId() + " Mac: " + liveUser.getCallingStationId());
                        radaysn.coaDMProcess(Arrays.asList(changeUserData), "COA", custReturnData, event);
                        custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG);
                        accessResponse.setPacketType(AAAConstant.ACCESS_ACCEPT);
                        isAuthSuccess = true;
                        retentionMac = liveUser.getCallingStationId();
                        oldSessionLogout = true;
                        oldMac = liveUser.getCallingStationId();
                    }
                }
                //add logic for save or update mac
                boolean authProvisionMac = false;
                if (radiusProfile.getAutoProvisionMac() != null && radiusProfile.getAutoProvisionMac().equalsIgnoreCase("Enable")) {
                    authProvisionMac = true;
                }
                if (custReturnData.getMacProvision() != null && custReturnData.getMacProvision()) {
                    authProvisionMac = true;
                }
                if (isAuthSuccess) {
                    boolean concurrencyFailed = custReturnData.getStrReplyMessage().equalsIgnoreCase(AAAConstant.CONCURRENCY_CHECK_FAIL);
                    boolean skipConcurrencyCheck = !cltData.getClientGroupData().isCheckConcurrency();

                    if (authProvisionMac) {
                        if (concurrencyFailed || oldSessionLogout) {
                            if (oldSessionLogout) {
                                // Update MAC
                                log.info("Concurrency failed or oldSessionLogout: " + oldSessionLogout +
                                        ", updating MAC. New MAC: " + strCalling + ", Old MAC: " + oldMac);
                                saveMacAndCustomerAsync(strCalling, oldMac, custReturnData, cltData.getMvnoId(), dbAuthDrive, false);
                            } else if (skipConcurrencyCheck) {
                                // Save new MAC
                                log.info("Concurrency failed, oldSessionLogout: " + oldSessionLogout +
                                        ", concurrency check skipped. Adding new MAC: " + strCalling);
                                saveMacAndCustomerAsync(strCalling, oldMac, custReturnData, cltData.getMvnoId(), dbAuthDrive, false);
                            }
                        } else {
                            // Save new MAC when concurrency is not failed
                            log.info("Concurrency not failed, adding new MAC: " + strCalling +
                                    ", authProvisionMac: " + authProvisionMac);
                            saveMacAndCustomerAsync(strCalling, oldMac, custReturnData, cltData.getMvnoId(), dbAuthDrive, false);
                        }
                    } else {
                        log.info("Skipping MAC provisioning, authProvisionMac: " + authProvisionMac +
                                ", user: " + strUsername);
                    }
                } else {
                    log.debug("Authentication failed, skipping MAC provisioning for user: " + custReturnData.getUsername());
                }


            } else {
                boolean authProvisionMac = false;
                if (radiusProfile.getAutoProvisionMac() != null && radiusProfile.getAutoProvisionMac().equalsIgnoreCase("Enable")) {
                    authProvisionMac = true;
                }
                saveMacAndCustomerAsync(strCalling, null, custReturnData, cltData.getMvnoId(), dbAuthDrive, false);
//                saveMacAndCustomerAsync(authProvisionMac, strCalling, custReturnData, cltData.getMvnoId(), 1, dbAuthDrive, retentionMac, cltData.getClientGroupData().isLogoutOldSessionOnNew());
            }
            long endTimeConcurrencycheck = System.currentTimeMillis();
            log.info("Time Taken: for localDBValidation: after endTimeConcurrencycheck   " + (endTimeConcurrencycheck - startTime));


        }
        if (totalReservedQuota > 0 && accessResponse.getPacketType() == AAAConstant.ACCESS_ACCEPT) {
            DBAccountingDriver dbAcct = new DBAccountingDriver();

            CustomerServiceImpl customerServiceImpl = new CustomerServiceImpl();
            Integer custQuotadtls = null;
            String username = custReturnData.getUsername();
            if (parentCust != null) {
                customerServiceImpl.sendReservedQuotaUpdateToAPIGateway(parentCust.getCustomerBasePlan().get(0).getCustpackageid(), true, totalReservedQuota);
                custQuotadtls = parentCust.getCustomerBasePlan().get(0).getCustquotaid();
                username = parentUserName;
                log.debug("Reserved quota updated for customer: " + parentCust.getUsername() + " available reserved quota: " + totalReservedQuota);
            } else if (custReturnData != null) {
                customerServiceImpl.sendReservedQuotaUpdateToAPIGateway(custReturnData.getCustomerBasePlan().get(0).getCustpackageid(), true, totalReservedQuota);
                custQuotadtls = custReturnData.getCustomerBasePlan().get(0).getCustquotaid();
                log.debug("Reserved quota updated for customer: " + custReturnData.getUsername() + " available reserved quota: " + totalReservedQuota);
            }
            if (dbAcct.checkReservedQuotavailabe(custReturnData.getCustid())) {
                updateCustReservedQuota(dbAcct, custReturnData, totalReservedQuota, reservedQuota, custQuotadtls);
            }
        } else if (accessResponse.getPacketType() == AAAConstant.ACCESS_REJECT && cltData.getClientGroupData().getAuthenticationProfile() != null && cltData.getClientGroupData().getAuthenticationProfile().equalsIgnoreCase("Enable")) {
            List<AccessResponse> accessResponses = cltData.getClientGroupData().getAccessResponses();
            if (!CollectionUtils.isEmpty(accessResponses)) {
                String msg = custReturnData.getStrReplyMessage();
                switch (msg) {
                    case AAAConstant.REPLYMSG_PASSWORDFAIL:
                        eventName = CommonConstants.AuthResponseEvent.WRONG_PASSWORD;
                        break;
                    case AAAConstant.REPLYMSG_USERNOTFOUND:
                        eventName = CommonConstants.AuthResponseEvent.WRONG_USERNAME;
                        break;

                    case AAAConstant.CONCURRENCY_CHECK_FAIL:
                        eventName = CommonConstants.AuthResponseEvent.CONCURRENCY_FAIL;
                        break;
                    case AAAConstant.REPLYMSG_QUOTA:
                    case AAAConstant.REPLYMSG_TIME_QUOTA:
                    case AAAConstant.REPLYMSG_VOLUME_QUOTA:
                    case AAAConstant.REPLYMSG_CUSTOMER_QUOTA:
                        eventName = CommonConstants.AuthResponseEvent.QUOTA_CONSUMED;
                        break;
                    case AAAConstant.REPLYMSG_PLANEXPIRED:
                    case AAAConstant.REPLYMSG_BASE_PLANEXPIRED:
                    case AAAConstant.REPLYMSG_CUSTOMER_EXPIRED:
                        eventName = CommonConstants.AuthResponseEvent.PLAN_EXPIRE;
                        break;
                }
                String finalEventName = eventName;
                custReturnData.setEventName(eventName);
                Optional<AccessResponse> response = accessResponses.stream().filter(res -> res.getEvent().equalsIgnoreCase(finalEventName)).findFirst();
                if (response.isPresent()) {
                    custReturnData.setStrReplyMessage(response.get().getMessage());
                    accessResponse.setPacketType(AAAConstant.ACCESS_ACCEPT);
                }
            }
            radUtil.processReplyItem(accessResponse, custReturnData, request, cltData.getClientGroupData().getClientGroupId(), cltData, true);
        }
        if (custReturnData != null) {
            if (parentCust != null)
                reason = parentCust.getStrReplyMessage();
            else
                reason = custReturnData.getStrReplyMessage();
            if (reason == null) {
                reason = AAAConstant.REPLYMSG;
            }
            accessResponse.addAttribute(REPLY_MESSAGE, reason);
            if (reason.equalsIgnoreCase(AAAConstant.REPLYMSG)) {
                custReturnData.setStatus("Active");
                custReturnData.setStrReplyMessage(AAAConstant.REPLYMSG);
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("Time Taken: for localDBValidation:   " + (endTime - startTime));
        return custReturnData;
    }

    private void updateCustReservedQuota(DBAccountingDriver dbAcct, CustomerData custReturnData, double totalReservedQuota, double reservedQuota, Integer custQuotadtls) {

        ForkJoinPool.commonPool().submit(() -> {
            try {
                dbAcct.updateReservedQuotaForChild(custReturnData.getUsername(), totalReservedQuota);
                dbAcct.addReservedQuotaDtls(custReturnData.getUsername(), custReturnData.getCustid(), custReturnData.getParentCustId(), reservedQuota, custQuotadtls);
            } catch (Exception ex) {
                log.debug(String.format("Error while performing updateCustReservedQuota operation", ex.getMessage()));
            }
        });


    }

    String getRequestAttribute(AccessRequest request, String attributeName) {
        if (request.getAttribute(attributeName) != null) {
            String value = request.getAttribute(attributeName).getAttributeValue();
            log.debug(String.format("%s: %s", attributeName, value));
            return value;
        }
        return null;
    }

    private void logCustomerData(CustomerData custReturnData) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Customer Data is  : %s", custReturnData.toString()));
        }
    }

    void handleUnknownUser(CustomerData custReturnData, RadiusPacket accessResponse, Client cltData, String reason, String strUsername, AccessRequest request) {
        long startTime = System.currentTimeMillis();
        log.warn(String.format("Customer is not Found Looking for Response"));
        List<UnknownProfileMapping> unknownProfileMappings = cltData.getClientGroupData().getUnknownProfileMappings();
        if (unknownProfileMappings != null && !unknownProfileMappings.isEmpty()) {
            ValidateExpression validate = new ValidateExpression();
            for (UnknownProfileMapping unknownProfileMapping : unknownProfileMappings) {
                if (unknownProfileMapping != null) {
                    String strCheckItem = unknownProfileMapping.getCheckitem();
                    boolean isCheckedTrue = validate.checkExpression(strCheckItem, request, custReturnData);
                    log.debug("Check Item is:" + strCheckItem + ": for: " + unknownProfileMapping.getAttribute() + ": response: " + isCheckedTrue);
                    if (isCheckedTrue) {
                        if (unknownProfileMapping.getAttributeValue().startsWith("REQ{") && unknownProfileMapping.getAttributeValue().endsWith("}")) {
                            String dynaAttribute = unknownProfileMapping.getAttributeValue().substring(4);
                            StringBuilder sb = new StringBuilder(dynaAttribute);
                            sb.deleteCharAt(dynaAttribute.length() - 1);
                            dynaAttribute = sb.toString();
                            dynaAttribute = getAttributeValueFromRequest(dynaAttribute, request);
                            if (dynaAttribute != null) {
                                accessResponse.addAttribute(unknownProfileMapping.getAttribute(), dynaAttribute);
                            }
                        } else if (unknownProfileMapping.getAttributeValue().startsWith("{") && unknownProfileMapping.getAttributeValue().endsWith("}")) {
                            try {
                                String expression = ExpressionEvaluator.getValueFromGivenExpression(unknownProfileMapping.getAttributeValue(), custReturnData, request);//unknownProfileMapping.getAttributeValue();
                                accessResponse.addAttribute(unknownProfileMapping.getAttribute(), expression);
                            } catch (Exception e) {
                                log.error(String.format("Error while check EXP value: %s", e.getMessage()));
                            }
                        } else {
                            accessResponse.addAttribute(unknownProfileMapping.getAttribute(), unknownProfileMapping.getAttributeValue());
                        }
                    }
                }
            }
            accessResponse.setPacketType(AAAConstant.ACCESS_ACCEPT);
            reason = "User Not Found (Welcome)";
            accessResponse.addAttribute(REPLY_MESSAGE, reason);
            custReturnData.setStrReplyMessage(reason);
        } else {
            log.debug(String.format("Customer Not Found for User Rejecting: %s", strUsername));
            accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
            custReturnData.setAuthStatus(false);
            custReturnData.setStrReplyMessage("User Not Found");
        }
        long endTime = System.currentTimeMillis();
        log.info("Time Taken: for handleUnknownUser:   " + (endTime - startTime));
    }

    private void handleSuspendedUser(CustomerData custReturnData, RadiusPacket accessResponse, Client cltData, String reason, String strUsername, AccessRequest request) {
        long startTime = System.currentTimeMillis();
        log.debug(String.format("Customer is Suspended Looking for Response"));
        List<SuspendedProfileMapping> suspendedMappings = cltData.getClientGroupData().getSuspendedProfileMappings();
        if (suspendedMappings != null && !suspendedMappings.isEmpty()) {
            ValidateExpression validate = new ValidateExpression();
            for (SuspendedProfileMapping suspendedProfileMapping : suspendedMappings) {
                String strCheckItem = suspendedProfileMapping.getCheckitem();
                boolean isCheckedTrue = validate.checkExpression(strCheckItem, request, custReturnData);
                log.debug("Check Item is:" + strCheckItem + ": for: " + suspendedProfileMapping.getAttribute() + ": response: " + isCheckedTrue);
                if (isCheckedTrue) {
                    if (suspendedProfileMapping.getAttributeValue().startsWith("REQ{") && suspendedProfileMapping.getAttributeValue().endsWith("}")) {
                        String dynaAttribute = suspendedProfileMapping.getAttributeValue().substring(4);
                        StringBuilder sb = new StringBuilder(dynaAttribute);
                        sb.deleteCharAt(dynaAttribute.length() - 1);
                        dynaAttribute = sb.toString();
                        dynaAttribute = getAttributeValueFromRequest(dynaAttribute, request);
                        if (dynaAttribute != null) {
                            accessResponse.addAttribute(suspendedProfileMapping.getAttribute(), dynaAttribute);
                        }
                    } else if (suspendedProfileMapping.getAttributeValue().startsWith("{") && suspendedProfileMapping.getAttributeValue().endsWith("}")) {
                        try {
                            String expression = ExpressionEvaluator.getValueFromGivenExpression(suspendedProfileMapping.getAttributeValue(), custReturnData, request);
                            accessResponse.addAttribute(suspendedProfileMapping.getAttribute(), expression);
                        } catch (Exception e) {
                            log.error(String.format("Error while check EXP value: %s", e.getMessage()));
                        }
                    } else {
                        accessResponse.addAttribute(suspendedProfileMapping.getAttribute(), suspendedProfileMapping.getAttributeValue());
                    }
                }

            }
            accessResponse.setPacketType(AAAConstant.ACCESS_ACCEPT);
            reason = "User is Suspended (Welcome)";
            accessResponse.addAttribute(REPLY_MESSAGE, reason);
            custReturnData.setStrReplyMessage(reason);
        } else {
            log.debug(String.format("Customer Not Found for User Rejecting: %s", strUsername));
            accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
            custReturnData.setAuthStatus(false);
            custReturnData.setStrReplyMessage("User is Suspended");
        }
        long endTime = System.currentTimeMillis();
        log.info("Time Taken: for handleSuspendedUser:   " + (endTime - startTime));
    }

    private void handleInactiveUser(CustomerData custReturnData, RadiusPacket accessResponse, Client cltData, String reason, AccessRequest request) {
        log.debug(String.format("Customer is Inactive Looking for Response username: " + custReturnData.getUsername()));
        long startTime = System.currentTimeMillis();
        List<InactiveProfileMapping> inActiveMapping = cltData.getClientGroupData().getInactiveProfileMappings();

        if (inActiveMapping != null && !inActiveMapping.isEmpty()) {
            ValidateExpression validate = new ValidateExpression();
            for (InactiveProfileMapping inactiveProfileMapping : inActiveMapping) {
                String strCheckItem = inactiveProfileMapping.getCheckitem();
                boolean isCheckedTrue = validate.checkExpression(strCheckItem, request, custReturnData);
                log.debug("Check Item is:" + strCheckItem + ": for: " + inactiveProfileMapping.getAttribute() + ": response: " + isCheckedTrue);
                if (isCheckedTrue) {
                    if (inactiveProfileMapping.getAttributeValue().startsWith("REQ{") && inactiveProfileMapping.getAttributeValue().endsWith("}")) {
                        String dynaAttribute = inactiveProfileMapping.getAttributeValue().substring(4);
                        StringBuilder sb = new StringBuilder(dynaAttribute);
                        sb.deleteCharAt(dynaAttribute.length() - 1);
                        dynaAttribute = sb.toString();
                        dynaAttribute = getAttributeValueFromRequest(dynaAttribute, request);
                        if (dynaAttribute != null) {
                            accessResponse.addAttribute(inactiveProfileMapping.getAttribute(), dynaAttribute);
                        }
                    } else if (inactiveProfileMapping.getAttributeValue().startsWith("{") && inactiveProfileMapping.getAttributeValue().endsWith("}")) {
                        try {
                            String expression = ExpressionEvaluator.getValueFromGivenExpression(inactiveProfileMapping.getAttributeValue(), custReturnData, request);
                            accessResponse.addAttribute(inactiveProfileMapping.getAttribute(), expression);
                        } catch (Exception e) {
                            log.error(String.format("Error while check EXP value: %s", e.getMessage()));
                        }
                    } else {
                        accessResponse.addAttribute(inactiveProfileMapping.getAttribute(), inactiveProfileMapping.getAttributeValue());
                    }
                }
            }
            accessResponse.setPacketType(AAAConstant.ACCESS_ACCEPT);
            reason = "User is Inactive (Welcome)";
            accessResponse.addAttribute(REPLY_MESSAGE, reason);
            custReturnData.setStrReplyMessage(reason);
        } else {
            log.debug(String.format("Customer Not Found for User Rejecting: %s", custReturnData.getUsername()));
            accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
            custReturnData.setAuthStatus(false);
            custReturnData.setStrReplyMessage("User is Inactive");
        }
        long endTime = System.currentTimeMillis();
        log.info("Time Taken: for handleInactiveUser:   " + (endTime - startTime));
    }


    public String getAttributeValueFromRequest(String dynaAttribute, AccessRequest request) {
        String attributeValue = null;
        try {
            log.debug("Looking for " + dynaAttribute + " attribute from request");
            RadiusAttribute attribute = request.getAttribute(dynaAttribute);
            if (attribute != null) {
                attributeValue = attribute.getAttributeValue();
                log.debug("Attribute value is : " + attributeValue);
            } else {
                log.debug("Attribute not found in the request.");
            }

        } catch (Exception e) {
            log.error("Unable to get attribute, Reason: " + e.getMessage());
            e.printStackTrace();
        }
        return attributeValue;
    }

    public AccountingRequest getAccRequestFromLiveUser(LiveUser liveUser) {
        AccountingRequest accRequest = new AccountingRequest();
        if (liveUser.getNasPortType() != null)
            accRequest.addAttribute("NAS-Port-Type", liveUser.getNasPortType());
        if (liveUser.getCallingStationId() != null)
            accRequest.addAttribute("Calling-Station-Id", liveUser.getCallingStationId());
        if (liveUser.getCalledStationId() != null)
            accRequest.addAttribute("Called-Station-Id", liveUser.getCalledStationId());
        if (liveUser.getNasPortId() != null)
            accRequest.addAttribute("NAS-Port-Id", liveUser.getNasPortId());
        if (liveUser.getUserName() != null)
            accRequest.addAttribute("User-Name", liveUser.getUserName());
        if (liveUser.getAcctSessionId() != null)
            accRequest.addAttribute("Acct-Session-Id", liveUser.getAcctSessionId());
        if (liveUser.getNasPort() != null)
            accRequest.addAttribute("NAS-Port", liveUser.getNasPort());
        if (liveUser.getFramedIpAddress() != null)
            accRequest.addAttribute("Framed-IP-Address", liveUser.getFramedIpAddress());
//		if(liveUser.getVendorSpecific() != null)
//			accRequest.addAttribute("Vendor-Specific",liveUser.getVendorSpecific());
        if (liveUser.getEventTimestamp() != null)
            accRequest.addAttribute("Event-Timestamp", liveUser.getEventTimestamp());
        if (liveUser.getAcctInputOctets() != null)
            accRequest.addAttribute("Acct-Input-Octets", liveUser.getAcctInputOctets());
        if (liveUser.getAcctOutputOctets() != null)
            accRequest.addAttribute("Acct-Output-Octets", liveUser.getAcctOutputOctets());
        if (liveUser.getAcctInputGigawords() != null)
            accRequest.addAttribute("Acct-Input-Gigawords", liveUser.getAcctInputGigawords());
        if (liveUser.getAcctOutputGigawords() != null)
            accRequest.addAttribute("Acct-Output-Gigawords", liveUser.getAcctOutputGigawords());
        if (liveUser.getAcctInputPackets() != null)
            accRequest.addAttribute("Acct-Input-Packets", liveUser.getAcctInputPackets());
        if (liveUser.getAcctOutputPackets() != null)
            accRequest.addAttribute("Acct-Output-Packets", liveUser.getAcctOutputPackets());
        if (liveUser.getAcctSessionTime() != null)
            accRequest.addAttribute("Acct-Session-Time", liveUser.getAcctSessionTime());
        if (liveUser.getNasIpAddress() != null)
            accRequest.addAttribute("NAS-IP-Address", liveUser.getNasIpAddress());
        if (liveUser.getNasIdentifier() != null)
            accRequest.addAttribute("NAS-Identifier", liveUser.getNasIdentifier());
        return accRequest;
    }

//    public void terminateExistingUserSession(AccountingRequest accRequest, CustomerData custReturnData, String strCallingId, DBAccountingDriver dbAccountingDrive, ConcurrentMap concurrentMap) {
//        ForkJoinPool.commonPool().submit(() -> {
//            try {
//                if (custReturnData != null) {
//                    dbAccountingDrive.insertCDR(null, concurrentMap, custReturnData.getMvnoId(), 0d, null, custReturnData);
//
//                }
//            } catch (Exception ex) {
//                log.debug(String.format("Error while performing terminateExistingUserSession operation", ex.getMessage()));
//            }
//        });
//    }

    public void saveMacAndCustomerAsync(String newMac, String oldMac, CustomerData custReturnData, Integer mvnoId, DBAuthenticationDriver dbAuthDrive, boolean isUpdate) {
        log.info("Save mac in customer: " + newMac + ", oldMac: " + oldMac + " customer: " + custReturnData.getUsername());
        RadiusAsyncUtility radiusAsyncUtility = new RadiusAsyncUtility();
        radiusAsyncUtility.UpdateCustomerMac(newMac, oldMac, custReturnData, mvnoId, isUpdate);
    }


    public RadiusProfileData mapRadiusProfileInfo(RadiusProfile radiusProfile, boolean isAuthRequest) {
        try {
            RadiusProfileData radiusProfileData = new RadiusProfileData();
            radiusProfileData.setName(radiusProfile.getName());
            radiusProfileData.setAccountcdrstatus(radiusProfile.getAccountCdrStatus());
            radiusProfileData.setAuthaudit(radiusProfile.getAuthAudit());
            radiusProfileData.setCheckitem(radiusProfile.getCheckItem());

            if (radiusProfile.getMappingMaster() != null && radiusProfile.getMappingMaster().getMappingMasterId() != null) {
                radiusProfileData.setMappingmasterid(radiusProfile.getMappingMaster().getMappingMasterId().intValue());
            }
            if (radiusProfile.getPriority() != null) {
                radiusProfileData.setPriority(radiusProfile.getPriority().intValue());
            }
            if (radiusProfile.getProxyServer() != null && radiusProfile.getProxyServer().getId() != null) {
                radiusProfileData.setProxyserverid(radiusProfile.getProxyServer().getId().intValue());
            }
            radiusProfileData.setSessionstatus(radiusProfile.getSessionStatus());
            radiusProfileData.setType(radiusProfile.getRequestType());

            if (!isAuthRequest) {
                CacheRetrival cacheRetrival = new CacheRetrival();
                List<DBMapping> dbMappingData = cacheRetrival.getDbMappingData();
                ConcurrentMap dbFieldMapping = new ConcurrentHashMap();
                for (DBMapping dBMapping : dbMappingData) {
                    if (radiusProfile.getMappingMaster() != null && radiusProfile.getMappingMaster().getMappingMasterId() == dBMapping.getMappingMasterId()) {
                        dbFieldMapping.put(dBMapping.getRadiusName(), dBMapping.getDbColumnName());
                    }
                }
                radiusProfileData.setDbFieldMapping(dbFieldMapping);
            }
            return radiusProfileData;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean insertOrUpdateAuthAndCDRdataAsync(boolean sassionstatus, RadiusUtility radiusUtility, AccountingRequest request,
                                                  RadiusPacket accoutningResponse, ConcurrentMap dbFieldMapping,
                                                  int mvnoId, String sourceAdd, double totalTimeMin, CustomerData custRetrunData, String acctStatusValue, Client cltData, double totalUsage, double upload, double download) {
        if (sassionstatus) {

            return radiusUtility.processAcctPacketCDR(request, accoutningResponse, dbFieldMapping, cltData.getMvnoId(), sourceAdd, totalTimeMin, custRetrunData, acctStatusValue, totalUsage, upload, download);

        } else {
            log.info(String.format("Session Storage or CDR Storage Disable"));
            return true;
        }
    }

    public void updateAcountingQuotaUse(CustomerQuotaInfo custQuotaInfo, String username, String strUsedQuota, String usedTime, Integer cprId, DBAccountingDriver dbAcct, Boolean isFreequota, RadiusUtility radiusUtility) {
        try {
            if (!isFreequota) {
                if (custQuotaInfo.getSkipQuotaReset() != null && custQuotaInfo.getSkipQuotaReset()) {
                    dbAcct.addCustomerQuotaResetHistory(custQuotaInfo);
                }
                dbAcct.updateQuota(username, String.valueOf(strUsedQuota), String.valueOf(custQuotaInfo.getVolumeBasedSessionUsedQuota()), String.valueOf(custQuotaInfo.getTimeBasedSessionUsedQuota()), String.valueOf(usedTime), cprId, custQuotaInfo.getSkipQuotaReset());
//				radiusUtility.SendUsedQotaInfo(custQuotaInfo);
            } else
                log.debug("Customer has time base policy with free quota");
        } catch (Exception ex) {
            log.error("Sync Quota with BSS Failed:" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void updateAcountingQuotaUseAsync(CustomerQuotaInfo custQuotaInfo, String username, String strUsedQuota, String usedTime, Integer cprId, DBAccountingDriver dbAcct, Boolean isFreequota, RadiusUtility radiusUtility) {
        try {
            if (!isFreequota) {
//				dbAcct.updateQuota(username, String.valueOf(strUsedQuota), String.valueOf(usedTime), cprId);
                radiusUtility.SendUsedQotaInfo(custQuotaInfo);
            } else
                log.debug("Customer has time base policy with free quota");
        } catch (Exception ex) {
            log.error("Sync Quota with BSS Failed:" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void insertOrUpdateAcctSessionAsync(boolean sassionstatus, RadiusUtility radiusUtility, AccountingRequest request,
                                               RadiusPacket accoutningResponse, ConcurrentMap dbFieldMapping,
                                               int mvnoId, String sourceAdd, CustomerData custRetrunData, String acctStatusValue, Client cltData, Boolean addLiveSessionOnInterim, double currentUsage, long currentTimeUsage, double upload, double download, boolean isFaultyMac, boolean isFirstSession, boolean interimSkipOnCoA) {

        Timestamp curentDate = new Timestamp(new Date().getTime());
        if(!(FRAMED_IP_ADDRESS_DOWN.equalsIgnoreCase(acctStatusValue) ||
                DELEGATED_IPV6_PREFIX_DOWN.equalsIgnoreCase(acctStatusValue) ||
                ALC_IPV6_ADDRESS_DOWN.equalsIgnoreCase(acctStatusValue)) &&
                !interimSkipOnCoA) {
            if (sassionstatus) {
                radiusUtility.processAcctPacketSession(request, accoutningResponse, dbFieldMapping, cltData.getMvnoId(), sourceAdd, custRetrunData, acctStatusValue, cltData, addLiveSessionOnInterim, currentUsage, currentTimeUsage, upload, download, isFaultyMac);
            } else {
                log.info(String.format("Session Storage or CDR Storage Disable"));
            }

            if (isFirstSession) {
                upload = 0;
                download = 0;
            }
        }
        RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
        radaysn.radiusPacketProcess(mvnoId, acctStatusValue, radiusUtility, request, accoutningResponse, dbFieldMapping, sourceAdd, currentTimeUsage, custRetrunData, currentUsage, upload, download, curentDate);

    }
}
