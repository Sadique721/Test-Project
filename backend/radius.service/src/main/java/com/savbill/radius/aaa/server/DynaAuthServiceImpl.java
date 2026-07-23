package com.savbill.radius.aaa.server;

import com.savbill.radius.aaa.constant.AAAConstant;
import com.savbill.radius.aaa.packet.RadiusPacket;
import com.savbill.radius.aaa.snmp.SNMPCounters;
import com.savbill.radius.aaa.util.ValidateExpression;
import com.savbill.radius.config.CacheRetrival;
import com.savbill.radius.entity.Client;
import com.savbill.radius.entity.RadiusProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;


public class DynaAuthServiceImpl{

    private static final String REPLY_MESSAGE = "Reply-Message";
    private static final Logger log = LoggerFactory.getLogger(DynaAuthServiceImpl.class);

    public RadiusPacket dynaAuthRequestReceived(RadiusPacket dynaAuthRequest, InetSocketAddress client) {
        RadiusPacket dynaResponse = null;
        try {
            Date startDate =new Date();
            if (log.isDebugEnabled()) {
                log.debug("Radius CoA/DM Request Packet Recevied :"+dynaAuthRequest.getPacketIdentifier()+":Type:"+dynaAuthRequest.getPacketType());
            }
            RadiusUtility radUtil=new RadiusUtility();
            //Identify Client
            Client cltData=radUtil.identifyClient(client.getAddress().toString().substring(1),dynaAuthRequest);
            //Process Radius Policy
            CacheRetrival cacheRetrival=new CacheRetrival();
            List<RadiusProfile> profileList =cacheRetrival.getDynaAuthProfileData();
            if (log.isDebugEnabled()) {
                log.debug(String.format("Radius Profiles size ",profileList.size()));
            }

            for (RadiusProfile radiusProfile: profileList) {
                ValidateExpression validate=new ValidateExpression();
                boolean blnResponse=validate.checkExpression(radiusProfile.getCheckItem(),dynaAuthRequest,null);
                log.info(String.format("Expression Check For %s : %s",radiusProfile.getName(),blnResponse));
                if(blnResponse) {
                    log.info(String.format("Processing Data with Profile %s",radiusProfile.getName()));
                    if(radiusProfile.getProxyServer()!=null) {
                        try {
                            dynaResponse=radUtil.proxyPacketCoADM(dynaAuthRequest,radiusProfile.getProxyServer(),null);
                            log.debug("Proxy Response : "+dynaResponse.getPacketType());
                        } catch(Exception e) {

                            if(dynaAuthRequest.getPacketType()==40){
                                dynaResponse = new RadiusPacket(AAAConstant.DISCONNECT_NAK, dynaAuthRequest.getPacketIdentifier());
                            }
                            if(dynaAuthRequest.getPacketType()==43){
                                dynaResponse = new RadiusPacket(AAAConstant.COA_NAK, dynaAuthRequest.getPacketIdentifier());
                            }
                            log.debug("Proxy Failed : "+e.getMessage());
                            return dynaResponse;
                        }
                    } else {
                        log.debug(String.format("Proxy Configured : %s",radiusProfile.getProxyServer()));
                    }
                    break;
                }
            }
        } catch(Exception e) {
            log.error("Error while performing operation",e);
        } finally {
            snmpCounterForAuthRequest(dynaResponse.getPacketTypeName());
        }
        return dynaResponse;
    }

    private void snmpCounterForAuthRequest(String packetTypeName) {
        SNMPCounters snmpCounters=new SNMPCounters();
        if(packetTypeName != null) {
            if(packetTypeName.equalsIgnoreCase("Access-Accept")) {
                snmpCounters.incrementAuthSuccess();
            } else if(packetTypeName.equalsIgnoreCase("Access-Reject")) {
                snmpCounters.incrementAuthFail();
            } else if(packetTypeName.equalsIgnoreCase("Proxy-Access-Accept")) {
                snmpCounters.incrementProxyAuthSuccess();
            } else if(packetTypeName.equalsIgnoreCase("Proxy-Access-Reject")) {
                snmpCounters.incrementProxyAuthFail();
            }
        }
    }


}
