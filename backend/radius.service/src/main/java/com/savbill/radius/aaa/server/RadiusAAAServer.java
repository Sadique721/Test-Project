package com.savbill.radius.aaa.server;

import java.net.InetAddress;

import com.savbill.radius.aaa.snmp.SNMPTrapGenerator;
import com.savbill.radius.utils.RadiusUtils;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;

public class RadiusAAAServer {

	private static final Logger log = LoggerFactory.getLogger(RadiusAAAServer.class);

	private AuthenticationService authenticationService;
	private AccountingService accountingService;
	private DynamicAuthenticationService dynamicAuthenticationService;

	public void startServer() {
		try {
			int authMinPort=0,authMaxPort=0,acctMinPort=0,acctMaxPort=0,queueCapacity=0;
			
			try {
				authMinPort=Integer.parseInt(RadiusUtils.readValueFromProperties("authMinPort"));
				authMaxPort=Integer.parseInt(RadiusUtils.readValueFromProperties("authMaxPort"));
				acctMinPort=Integer.parseInt(RadiusUtils.readValueFromProperties("acctMinPort"));
				acctMaxPort=Integer.parseInt(RadiusUtils.readValueFromProperties("acctMaxPort"));
				queueCapacity=Integer.parseInt(RadiusUtils.readValueFromProperties("queueCapacity"));
			}
			catch(Exception e) {
				log.error("Exception to read value from environment: "+e.getMessage());
			}
			
			if(authMinPort==0) {
				authMinPort=250;
			}
			if(authMaxPort==0) {
				authMaxPort=250;
			}
			if(acctMinPort==0) {
				acctMinPort=250;
			}
			if(acctMaxPort==0) {
				acctMaxPort=250;
			}
			if(queueCapacity==0) {
				queueCapacity=5000;
			}
			
			log.info("Intializing Radius Server");
			int intAuthPort=1812;
			int intAcctPort=1813;
			int intdynaAuthPort=3799;
			String strServerIp="0.0.0.0";
			InetAddress address=InetAddress.getByName(strServerIp);

			authenticationService = new AuthenticationService(address, intAuthPort);
			authenticationService.setAuthMinThreads(authMinPort);
			authenticationService.setAuthMaxThreads(authMaxPort);
			authenticationService.setQueueCapacity(queueCapacity);
			authenticationService.start();

			accountingService = new AccountingService(address, intAcctPort);
			accountingService.setAcctMinThreads(acctMinPort);
			accountingService.setAcctMaxThreads(acctMaxPort);
			accountingService.setQueueCapacity(queueCapacity);
			accountingService.start();

			dynamicAuthenticationService = new DynamicAuthenticationService(address, intdynaAuthPort);
			dynamicAuthenticationService.start();

			log.info("*** Radius Server Started Successfully ***");
			// SNMP Up

			String snmpCommunity = RadiusUtils.readValueFromProperties("radiusA.snmpCommunity");
			if(snmpCommunity == null || snmpCommunity.isEmpty()){
				snmpCommunity = "public";
			}

			SNMPTrapGenerator trapV2 = new SNMPTrapGenerator();
			trapV2.serverUpV2(snmpCommunity,"127.0.0.1",9090,"Radius Server Up",".1.3.6.1.6.3.1.1.5.4");
		}
		catch(Exception e) {
			log.error("Error while starting radius server",e);
		}
	}

	private void stopServer() {
		try {
			log.info("Stoping Radius Server");
			String strServerIp="0.0.0.0";
			InetAddress address=InetAddress.getByName(strServerIp);
			authenticationService.stop();
			accountingService.stop();
			dynamicAuthenticationService.stop();

		}
		catch(Exception e) {
			log.error("Error while stopiing server",e);
		}
	}

	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	public AccountingService getAccountingService() {
		return accountingService;
	}

	public DynamicAuthenticationService getDynamicAuthenticationService() {
		return dynamicAuthenticationService;
	}
}
