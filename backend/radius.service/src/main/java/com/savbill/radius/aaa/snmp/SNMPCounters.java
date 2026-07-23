package com.savbill.radius.aaa.snmp;

import java.io.IOException;
import java.util.Date;

import com.savbill.radius.utils.RadiusUtils;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.MessageDispatcher;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.MessageException;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.log.LogFactory;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.StateReference;
import org.snmp4j.mp.StatusInformation;
import org.snmp4j.security.Priv3DES;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.TransportIpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.tools.console.SnmpRequest;
import org.snmp4j.transport.AbstractTransportMapping;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

public class SNMPCounters implements CommandResponder {
	public static int authRequest = 0;

	public static int temp_authRequest = 0;
	public static int authSuccess = 0;
	public static int authFail = 0;
	public static int acctSuccess = 0;
	public static int acctFail = 0;
	public static int dynaSuccess = 0;
	public static int dynaFail = 0;
	public static int acctRequest = 0;

	public static int temp_acctRequest = 0;

	public static Date startDate;

	public static Date resetStartdate;

	public static int acctStart = 0;
	public static int acctStop = 0;
	public static int acctUpdate= 0;
	
	public static int proxyAuthSuccess = 0;
	public static int proxyAuthFail = 0;
	public static int proxyAcctSuccess = 0;
	public static int proxyAcctFail = 0;
	public static int proxyDynaSuccess = 0;
	public static int proxyDynaFail = 0;
	public static int proxyAcctStart = 0;
	public static int proxyAcctStop = 0;
	public static int proxyAcctUpdate= 0;

	public void incrementAuthSuccess() {
		authSuccess++;
	}

	public void initializedDate() {
		startDate = new Date();
		resetStartdate = new Date();
	}

	public void initializedResetStartDate() {
		resetStartdate = new Date();
		temp_acctRequest = 0;
		temp_authRequest = 0;
	}

	public void incrementAuthRequest() {
		authRequest++;
		temp_authRequest++;
	}

	public void incrementTempAuthRequest() {
		temp_authRequest++;
	}

	public void incrementAcctRequest() {
		acctRequest++;
		temp_acctRequest++;
	}

	public void incrementTempAcctRequest() {
		temp_acctRequest++;
	}

	public void incrementAuthFail() {
		authFail++;
	}

	public void incrementAcctSuccess() {
		acctSuccess++;
	}

	public void incrementAcctFail() {
		acctFail++;
	}

	public void incrementDynaSucess() {
		dynaSuccess++;
	}

	public void incrementDynaFail() {
		dynaFail++;
	}

	public void incrementAcctStart() {
		acctStart++;
	}
	
	public void incrementAcctStop() {
		acctStop++;
	}
	
	public void incrementAcctUpdate() {
		acctUpdate++;
	}
	
	public void incrementProxyAuthSuccess() {
		proxyAuthSuccess++;
	}

	public void incrementProxyAuthFail() {
		proxyAuthFail++;
	}

	public void incrementProxyAcctSuccess() {
		proxyAcctSuccess++;
	}

	public void incrementProxyAcctFail() {
		proxyAcctFail++;
	}

	public void incrementProxyDynaSucess() {
		proxyDynaSuccess++;
	}

	public void incrementProxyDynaFail() {
		proxyDynaFail++;
	}

	public void incrementProxyAcctStart() {
		proxyAcctStart++;
	}
	
	public void incrementProxyAcctStop() {
		proxyAcctStop++;
	}
	
	public void incrementProxyAcctUpdate() {
		proxyAcctUpdate++;
	}
	
	
	public synchronized void listen(TransportIpAddress address) throws IOException {
		AbstractTransportMapping transport;
		if (address instanceof TcpAddress) {
			transport = new DefaultTcpTransportMapping((TcpAddress) address);
		} else {
			transport = new DefaultUdpTransportMapping((UdpAddress) address);
		}

		ThreadPool threadPool = ThreadPool.create("DispatcherPool", 10);
		MessageDispatcher mtDispatcher = new MultiThreadedMessageDispatcher(threadPool, new MessageDispatcherImpl());

		// add message processing models
		mtDispatcher.addMessageProcessingModel(new MPv1());
		mtDispatcher.addMessageProcessingModel(new MPv2c());

		// add all security protocols
		SecurityProtocols.getInstance().addDefaultProtocols();
		SecurityProtocols.getInstance().addPrivacyProtocol(new Priv3DES());

		//Create Target
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString("public"));

		Snmp snmp = new Snmp(mtDispatcher, transport);
		snmp.addCommandResponder(this);

		transport.listen();
		System.out.println("SNMP Agent Listening on " + address);
		initializedDate();
		try {
			this.wait();
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * This method will be called whenever a pdu is received on the given port
	 * specified in the listen() method
	 */
	public synchronized void processPdu(CommandResponderEvent cmdRespEvent) {
		String community = new String(cmdRespEvent.getSecurityName());
		String defaultCommunity = RadiusUtils.readValueFromProperties("radiusA.snmpCommunity");

		if(defaultCommunity == null || defaultCommunity.isEmpty()){
			defaultCommunity = "public";
		}

		if (!defaultCommunity.equals(community)) {
			LogFactory.getLogger(SnmpRequest.class).warn("Community not matched " + community);
			return;
		}
		PDU pdu = cmdRespEvent.getPDU();
		if (pdu != null) {

			int pduType = pdu.getType();
			if ((pduType != PDU.TRAP) && (pduType != PDU.V1TRAP) && (pduType != PDU.REPORT)
					&& (pduType != PDU.RESPONSE)) {
				pdu.setErrorIndex(0);
				pdu.setErrorStatus(0);

				// forming response for requested OID request
				if (pdu.get(0).getOid().equals(new OID(OIDConstant.AUTH_SUCCESS_OID))) {
					//		System.out.println("requested OID is  " + pdu.get(0).getOid());
					pdu.set(0, new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(authSuccess)));
				}
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.AUTH_REJECT_OID)))
				{
					//		System.out.println("requested OID is  " + pdu.get(0).getOid());
					pdu.set(0, new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(authFail)));
				} 
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.ACCT_SUCCESS_OID)))
				{
					//		System.out.println("requested OID is  " + pdu.get(0).getOid());
					pdu.set(0, new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(acctSuccess)));
				} 
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.ACCT_FAIL_OID)))
				{
					//		System.out.println("requested OID is  " + pdu.get(0).getOid());
					pdu.set(0, new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(acctFail)));
				} 
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.DynaAUTH_SUCCESS_OID)))
				{
					//		System.out.println("requested OID is  " + pdu.get(0).getOid());
					pdu.set(0, new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(dynaSuccess)));
				} 
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.dynaAUTH_REJECT_OID)))
				{
					//		System.out.println("requested OID is  " + pdu.get(0).getOid());
					pdu.set(0, new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(dynaFail)));
				}
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.ACCT_START_OID)))
				{
					//		System.out.println("requested OID is  " + pdu.get(0).getOid());
					pdu.set(0, new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(acctStart)));
				}
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.ACCT_STOP_OID)))
				{
					//		System.out.println("requested OID is  " + pdu.get(0).getOid());
					pdu.set(0, new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(acctStop)));
				}
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.ACCT_UPDATE_OID)))
				{
					//		System.out.println("requested OID is  " + pdu.get(0).getOid());
					pdu.set(0, new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(acctUpdate)));
				}
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.PDU_RESET))) {
					pdu.clear();
				} 
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.AUTH_PROXY_SUCCESS_OID))) {
					//		System.out.println("requested OID is  " + pdu.get(0).getOid());
					pdu.set(0, new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(proxyAuthSuccess)));
				}
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.AUTH_PROXY_REJECT_OID)))
				{
					//		System.out.println("requested OID is  " + pdu.get(0).getOid());
					pdu.set(0, new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(proxyAuthFail)));
				} 
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.ACCT_PROXY_SUCCESS_OID)))
				{
					//		System.out.println("requested OID is  " + pdu.get(0).getOid());
					pdu.set(0, new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(proxyAcctSuccess)));
				} 
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.ACCT_PROXY_FAIL_OID)))
				{
					//		System.out.println("requested OID is  " + pdu.get(0).getOid());
					pdu.set(0, new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(proxyAcctFail)));
				} 
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.DynaAUTH_PROXY_SUCCESS_OID)))
				{
					//		System.out.println("requested OID is  " + pdu.get(0).getOid());
					pdu.set(0, new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(proxyDynaSuccess)));
				} 
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.dynaAUTH_PROXY_REJECT_OID)))
				{
					//		System.out.println("requested OID is  " + pdu.get(0).getOid());
					pdu.set(0, new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(proxyDynaFail)));
				}
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.ACCT_PROXY_START_OID)))
				{
					//		System.out.println("requested OID is  " + pdu.get(0).getOid());
					pdu.set(0, new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(proxyAcctStart)));
				}
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.ACCT_PROXY_STOP_OID)))
				{
					//		System.out.println("requested OID is  " + pdu.get(0).getOid());
					pdu.set(0, new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(proxyAcctStop)));
				}
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.ACCT_PROXY_UPDATE_OID)))
				{
					//		System.out.println("requested OID is  " + pdu.get(0).getOid());
					pdu.set(0, new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(proxyAcctUpdate)));
				}
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.AUTH_REQUEST_OID)))
				{
					//		System.out.println("requested OID is  " + pdu.get(0).getOid());
					pdu.set(0, new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(authRequest)));
				}
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.ACCT_REQUEST_OID)))
				{
					//		System.out.println("requested OID is  " + pdu.get(0).getOid());
					pdu.set(0, new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(acctRequest)));
				}
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.AUTH_TPS_OID)))
				{
					Date endDate = new Date();
					int tps = 0;
					try {
						long seconds = (endDate.getTime() - resetStartdate.getTime()) / 1000;
						if(seconds==0){
							seconds=1;
						}
						System.out.println("Seconds  " + seconds);
						System.out.println("Request  " + temp_authRequest);
						tps = (int) (temp_authRequest / seconds);
						System.out.println("TPS  " + tps);
					} catch (Exception e) {
						System.err.println("Error while sending response for TPS: " + e.getMessage());
					}
					pdu.set(0, new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(tps)));
				}
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.ACCT_TPS_OID)))
				{
					Date endDate=new Date();
					int tps = 0;
					try {
						long seconds = (endDate.getTime() - resetStartdate.getTime()) / 1000;
						if(seconds==0){
							seconds=1;
						}
						System.out.println("Seconds  " + seconds);
						System.out.println("Request  " + temp_acctRequest);
						tps = (int) (temp_acctRequest / seconds);
						System.out.println("TPS  " + tps);
					}catch (Exception e){
						System.err.println("Error while sending response for TPS: " + e.getMessage());
					}
					pdu.set(0, new VariableBinding(new OID(pdu.get(0).getOid()), new Integer32(tps)));
				}
				else if (pdu.get(0).getOid().equals(new OID(OIDConstant.PDU_RESET))) {
					pdu.clear();
				} 
				else {
					//		System.out.println("Unknown SNMP OID");
				}
				pdu.setType(PDU.RESPONSE);

				StatusInformation statusInformation = new StatusInformation();
				StateReference ref = cmdRespEvent.getStateReference();
				try {
					//		System.out.println("response sent please check your SNMP manager");
					//		System.out.println(cmdRespEvent.getPDU());
					cmdRespEvent.getMessageDispatcher().returnResponsePdu(cmdRespEvent.getMessageProcessingModel(),
							cmdRespEvent.getSecurityModel(), cmdRespEvent.getSecurityName(),
							cmdRespEvent.getSecurityLevel(), pdu, cmdRespEvent.getMaxSizeResponsePDU(), ref,
							statusInformation);
				} catch (MessageException ex) {
					System.err.println("Error while sending response: " + ex.getMessage());
					LogFactory.getLogger(SnmpRequest.class).error(ex);
				}
			}
		}
	}

}
