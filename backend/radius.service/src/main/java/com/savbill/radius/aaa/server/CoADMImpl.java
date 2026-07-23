package com.savbill.radius.aaa.server;

import com.savbill.radius.aaa.data.CoaDmTracker;
import com.savbill.radius.aaa.data.CustomerData;
import com.savbill.radius.aaa.db.DBAccountingDriver;
import com.savbill.radius.aaa.packet.AccountingRequest;
import com.savbill.radius.aaa.packet.RadiusPacket;
import com.savbill.radius.entity.CoaDMProfile;
import com.savbill.radius.entity.LiveUser;
import com.savbill.radius.services.CoaDmTrackerService;
import com.savbill.radius.spring.SpringContext;
import com.savbill.radius.utils.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;


public class CoADMImpl {

    private static final Logger log = LoggerFactory.getLogger(CoADMImpl.class);

    public RadiusPacket intiateCoAAcct(boolean firecoa, Long DMProfileId, Long CoAProfileId, boolean expiry, CustomerData custRetrunData, List<CoaDMProfile> coaDMProfileDataList,
                               String strDMCoAIP, String strUsername, AccountingRequest request, DBAccountingDriver dbAcct) {
        RadiusPacket coaDMResponse = null;
        try {
            log.debug("In method intiateCoAAcct:" + firecoa);
            //TODO: Commented for ACT Testing of quota booster expire COA not triggered
//            if(checkCOAValidToTrigger(coaDmTracker)) {
//                log.debug("COA/DM already trigger for tracker: "+coaDmTracker.toString());
//                return;
//            }
            RadiusUtility radiusUtility = new RadiusUtility();
            if (firecoa && (DMProfileId != 0L || CoAProfileId != 0L) && (custRetrunData.getStatus().equals(CommonConstants.PLAN_INACTIVE)
                    || !custRetrunData.getCustomerBasePlan().get(0).isUsagereached())) {
                log.debug("CoA/DM True and Client Group Found");
                CoaDMProfile coaProfileData = null;
                try {
                    log.debug("CoA Profile Cache size:" + coaDMProfileDataList.size());
                    if (custRetrunData.getEventName() == null && custRetrunData.getCustomerBasePlan().get(0).isAllowoverusage() || (custRetrunData.getStatus().equals(CommonConstants.PLAN_INACTIVE) || custRetrunData.getCustomerBasePlan().get(0).isPlanQosFire())) {
                        if (coaProfileData == null && CoAProfileId != 0L) {
                            if (coaDMProfileDataList != null && coaDMProfileDataList.size() > 0) {
                                for (int i = 0; i < coaDMProfileDataList.size(); i++) {
                                    if (CoAProfileId == coaDMProfileDataList.get(i).getCoaDMProfileId()) {
                                        coaProfileData = coaDMProfileDataList.get(i);
                                        coaProfileData.setGateway(strDMCoAIP);
                                    }
                                }
                            }
                        } else if (coaProfileData == null && DMProfileId != 0L) {
                            if (coaDMProfileDataList != null && coaDMProfileDataList.size() > 0) {
                                for (int i = 0; i < coaDMProfileDataList.size(); i++) {
                                    if (DMProfileId == coaDMProfileDataList.get(i).getCoaDMProfileId()) {
                                        coaProfileData = coaDMProfileDataList.get(i);
                                        coaProfileData.setGateway(strDMCoAIP);
                                    }
                                }
                            }
                        }
                    } else {
                        if (coaProfileData == null && DMProfileId != 0L) {
                            if (coaDMProfileDataList != null && coaDMProfileDataList.size() > 0) {
                                for (int i = 0; i < coaDMProfileDataList.size(); i++) {
                                    if (DMProfileId == coaDMProfileDataList.get(i).getCoaDMProfileId()) {
                                        coaProfileData = coaDMProfileDataList.get(i);
                                        coaProfileData.setGateway(strDMCoAIP);
                                    }
                                }
                            }
                        } else if (coaProfileData == null && CoAProfileId != 0L) {
                            if (coaDMProfileDataList != null && coaDMProfileDataList.size() > 0) {
                                for (int i = 0; i < coaDMProfileDataList.size(); i++) {
                                    if (CoAProfileId == coaDMProfileDataList.get(i).getCoaDMProfileId()) {
                                        coaProfileData = coaDMProfileDataList.get(i);
                                        coaProfileData.setGateway(strDMCoAIP);
                                    }
                                }
                            }
                        }
                    }
                    //Event name Will be set on Volume booster plan data exhaust or any plan changed at runtime
                    if (!expiry && custRetrunData.getEventName() == null) {
                        if ((coaProfileData == null || (custRetrunData.getStatus().equals(CommonConstants.PLAN_INACTIVE) || custRetrunData.getCustomerBasePlan().get(0).isAllowoverusage())) && CoAProfileId != 0L) {
                            if (coaDMProfileDataList != null && coaDMProfileDataList.size() > 0) {
                                for (int i = 0; i < coaDMProfileDataList.size(); i++) {
                                    if (CoAProfileId == coaDMProfileDataList.get(i).getCoaDMProfileId()) {
                                        coaProfileData = coaDMProfileDataList.get(i);
                                        coaProfileData.setGateway(strDMCoAIP);
                                    }
                                }
                            }
                        }
                    }
                    log.warn("coaProfileData:" + coaProfileData + ":Expiry:" + expiry);
                    if (coaProfileData != null) {
                        boolean isMultiSessionFound = false;
                        log.warn("DM/COA Profile Found : " + coaProfileData.getName());
                        log.warn("CoA/DM Firing on:" + coaProfileData.getGateway() + ":Key:" + coaProfileData.getSharedkey() + ":Port:" + coaProfileData.getPort());
                        if (custRetrunData.getCustid() != 0) {
                            List<LiveUser> packetList = dbAcct.fetchLiveSessionsAsRequestBasedOnCustId(custRetrunData.getCustid());
                            if (!CollectionUtils.isEmpty(packetList)) {
                                isMultiSessionFound = true;
                                log.debug("Number Of Live User Found: " + packetList.size());
                                for (LiveUser liveuser : packetList) {

                                    try {
                                        AccountingRequest acctReq = (AccountingRequest) radiusUtility.getRequestFromLiveUser(liveuser);
                                        coaDMResponse = radiusUtility.initiateCoADM(coaProfileData, acctReq, strUsername, custRetrunData, strDMCoAIP);
                                        log.warn("COA/DM response:"+coaDMResponse+":For Event:"+custRetrunData.getEventName()+":");
                                        if(coaDMResponse!=null){
                                            RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                            radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), String.valueOf(coaDMResponse.getPacketType()),custRetrunData.getEventName(), custRetrunData.getMvnoId(),coaDMResponse);
                                        }
                                        else{
                                            RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                            radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), "Timeout or Error Occurs",custRetrunData.getEventName(), custRetrunData.getMvnoId(),coaDMResponse);
                                        }
                                    } catch (Exception ex) {
                                        RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                        radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), "Error or Timeout",custRetrunData.getEventName(), custRetrunData.getMvnoId(),coaDMResponse);
                                        log.error("CoA/DM Failed:" + ex.getMessage());
                                    }
                                }
                            } else {
                                log.debug("No Of Live User Found: 0");
                            }
                        }
                        if (!isMultiSessionFound) {
                            try {
                                coaDMResponse = radiusUtility.initiateCoADM(coaProfileData, request, strUsername, custRetrunData, strDMCoAIP);
                                log.warn("COA/DM response:"+coaDMResponse+":For Event:"+custRetrunData.getEventName()+":");
                                if(coaDMResponse!=null){
                                    RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                    radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), String.valueOf(coaDMResponse.getPacketType()),custRetrunData.getEventName(), custRetrunData.getMvnoId(),coaDMResponse);
                                }
                                else{
                                    RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                    radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), "Timeout or Error Occurs",custRetrunData.getEventName(), custRetrunData.getMvnoId(),coaDMResponse);
                                }
                            } catch (Exception ex) {
                                RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                radaysn.coaRespnseProcess(coaProfileData.getGateway(),request.getUserName(), "Error or Timeout",custRetrunData.getEventName(), custRetrunData.getMvnoId(),coaDMResponse);
                                log.error("CoA/DM Failed:" + ex.getMessage());
                            }
                        }
                    } else {
                        log.debug("CoA/DM Profile Not Found Skipping CoA/DM");
                    }
                } catch (Exception e) {
                    log.debug("CoA/DM Failed:" + e.getMessage());
                }
            } else {
                log.warn("CoA/DM NOT Required Username:" + strUsername + "Overusage Flag:" + custRetrunData.getCustomerBasePlan().get(0).isUsagereached());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return coaDMResponse;
    }

    public void saveCoaTrackerInfo(CoaDmTracker coaDmTracker) {
        try {
            CoaDmTrackerService coaDmTrackerService = SpringContext.getBean(CoaDmTrackerService.class);
            coaDmTrackerService.saveNewCoaDmTracker(coaDmTracker);
        } catch (Exception ex) {
            log.error("Error while save COA trigger is valid or not: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public boolean checkCOAValidToTrigger(CoaDmTracker coaDmTracker) {
        log.debug("check for COA DM trigger is valid or not for : " + coaDmTracker.toString());
        boolean result = true;
        try {
            CoaDmTrackerService coaDmTrackerService = SpringContext.getBean(CoaDmTrackerService.class);
            if (coaDmTracker.getCause() != null) {
                String cause = coaDmTracker.getCause();
                switch (cause) {
                    case CommonConstants.CoaDmResonContant.PLAN_QUOTA_EXHAUST:
                    case CommonConstants.CoaDmResonContant.QUOTA_RESET:
                        //check with cprid and cause as plan quota exhaust
                        // if entry not exists then return true
                        //if exists then false;
                        log.debug("Check COA trigger for plan quota exhaust or Quota Reset");
                        return coaDmTrackerService.checkCoaDmExistsForPlanQuotaExhaustOrQuotaReset(coaDmTracker);
                    case CommonConstants.CoaDmResonContant.PLAN_EXPIRE:
                        //check with cprid and cause as plan expire
                        // if entry not exists then return true
                        //if exists then false;
                        log.debug("Check COA trigger for plan expire");
                        return coaDmTrackerService.checkCoaDmExistsForPlanExpire(coaDmTracker);
                    case CommonConstants.CoaDmResonContant.TIME_BASE_POLICY_CHANGE:
                        //check with cprid and cause as Time base policy and timebasepolocy id
                        // if entry not exists then return true
                        //if exists then false;
                        log.debug("Check COA trigger for Time base Policy change");
                        return coaDmTrackerService.checkCoaDmExistsForTimeBasePolicy(coaDmTracker);
                }
            }
        } catch (Exception e) {
            log.error("Error while check COA trigger is valid or not: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }


}
