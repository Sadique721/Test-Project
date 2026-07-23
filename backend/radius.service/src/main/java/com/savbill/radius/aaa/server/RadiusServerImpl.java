package com.savbill.radius.aaa.server;

import java.net.InetSocketAddress;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;

import com.savbill.radius.aaa.constant.AAAConstant;
import com.savbill.radius.aaa.packet.AccessRequest;
import com.savbill.radius.aaa.packet.AccountingRequest;
import com.savbill.radius.aaa.packet.RadiusPacket;
import com.savbill.radius.aaa.snmp.SNMPCounters;
import com.savbill.radius.aaa.util.RadiusServer;
import com.savbill.radius.entity.Client;
import com.savbill.radius.utils.CommonConstants;
import com.savbill.radius.utils.RadiusUtils;


public class RadiusServerImpl extends RadiusServer {

	private static final String REPLY_MESSAGE = "Reply-Message";
	private static final Logger log = LoggerFactory.getLogger(RadiusServerImpl.class);
//	AuthAcctUtilityImpl authAcctUtilityImpl=new AuthAcctUtilityImpl();
	AuthServiceImpl authServiceImpl=new AuthServiceImpl();
//	AcctServiceImpl acctServiceImpl=new AcctServiceImpl();
	AcctingServiceImpl acctingService=new AcctingServiceImpl();
	DynaAuthServiceImpl dynaAuthServiceImpl=new DynaAuthServiceImpl();

	private String authenticationMode;

	@Override
	public String getSharedSecret(InetSocketAddress client) {
		RadiusUtility radUtil=new RadiusUtility();
		try {
			log.debug("In Method getSharedSecret");
			Client cltData=radUtil.identifyClientOnly(client.getAddress().toString().substring(1),null);
			if (log.isDebugEnabled()) {
				log.debug(String.format("Recevied Request From : %s Key is %s and mvno is %s",client.getAddress().toString().substring(1),cltData.getSharedKey(),cltData.getMvnoId()));
			}
			return cltData.getSharedKey();
		} catch(Exception e) {
			log.error("Error while getSharedSecret"+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getUserPassword(String userName) {
		if (log.isDebugEnabled()) {
			log.debug(String.format("UserPassword %s",userName));
		}
		return "DUMMY";
	}

	@Override
	public RadiusPacket dynaAuthRequestReceived(RadiusPacket dynaAuthRequest, InetSocketAddress client) {
		RadiusPacket dynaResponse = null;
		try {
			dynaResponse=dynaAuthServiceImpl.dynaAuthRequestReceived(dynaAuthRequest, client);
		} catch(Exception e) {
			log.error("Error while performing operation",e);
		}
		return dynaResponse;
	}

	/**
	 * Method for Auth Response
	 * @param request Radius request packet
	 * @param client address of Radius client
	 * @return
	 */
	@Override
	public RadiusPacket accessRequestReceived(AccessRequest request,InetSocketAddress client) {
		RadiusPacket accessResponse=new RadiusPacket(AAAConstant.ACCESS_REJECT,request.getPacketIdentifier());
		try {
			accessResponse=authServiceImpl.accessRequestReceived(request, client);
		} catch(Exception e) {
			accessResponse.addAttribute(REPLY_MESSAGE,"Internal Error Authentication Fail");
			accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
			log.error("Error while performing operation",e);
		}
		return accessResponse;
	}


	private void snmpCounterForAcctRequest(String acctStatusType) {
		SNMPCounters snmpCounters=new SNMPCounters();
		if(acctStatusType != null) {
			if(acctStatusType.equalsIgnoreCase("Start")) {
				snmpCounters.incrementAcctStart();
			} else if(acctStatusType.equalsIgnoreCase("Stop")) {
				snmpCounters.incrementAcctStop();
			} else if(acctStatusType.equalsIgnoreCase("Interim-Update")) {
				snmpCounters.incrementAcctUpdate();
			} else if(acctStatusType.equalsIgnoreCase("Proxy-Start")) {
				snmpCounters.incrementProxyAcctStart();
			} else if(acctStatusType.equalsIgnoreCase("Proxy-Stop")) {
				snmpCounters.incrementProxyAcctStop();
			} else if(acctStatusType.equalsIgnoreCase("Proxy-Interim-Update")) {
				snmpCounters.incrementProxyAcctUpdate();
			}
		}
	}


	/**
	 * Method for Accounting start and stop Response
	 * @param request Radius request packet
	 * @param client address of Radius client
	 * @return
	 */
	@Override
	public RadiusPacket accountingRequestReceived(AccountingRequest request, InetSocketAddress client) {
		RadiusPacket accoutningResponse=new RadiusPacket(AAAConstant.ACCOUNTING_RESPONSE,request.getPacketIdentifier());
		String authenticationType = RadiusUtils.readValueFromProperties("radius.authentication.type");
		if(authenticationType == null) {
			authenticationType = CommonConstants.AUTHENTICATION_TYPE_DEPENDENT;
		}
		SNMPCounters snmpCounters=new SNMPCounters();
		try{
			accoutningResponse=acctingService.accountingRequestReceived(request, client);
		} catch(Exception e) {
			log.error("Error while processing accounting request",e);
			e.printStackTrace();
			snmpCounters.incrementAcctFail();
		}
		return accoutningResponse;
	}

}
