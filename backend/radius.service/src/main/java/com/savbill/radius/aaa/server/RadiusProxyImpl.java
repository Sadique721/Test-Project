package com.savbill.radius.aaa.server;

import com.savbill.radius.aaa.packet.RadiusPacket;
import com.savbill.radius.aaa.proxy.RadiusProxy;
import com.savbill.radius.aaa.proxy.RadiusProxyConnection;
import com.savbill.radius.aaa.util.RadiusEndpoint;
import com.savbill.radius.aaa.util.RadiusException;
import com.savbill.radius.entity.Client;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RadiusProxyImpl extends RadiusProxy {
	private static final String REPLY_MESSAGE = "Reply-Message";
	private static final Logger log = LoggerFactory.getLogger(RadiusProxyImpl.class);

	@Override
	public String getSharedSecret(InetSocketAddress client) {
		RadiusUtility radUtil=new RadiusUtility();
		try 
		{
			log.debug("In Method getSharedSecret");
			Client cltData=radUtil.identifyClientOnly(client.getAddress().toString().substring(1),null);
			if (log.isDebugEnabled()) {
				log.debug(String.format("Recevied Request From : %s Key is %s",client.getAddress().toString().substring(1),cltData.getSharedKey()));
			}
			return cltData.getSharedKey();
		}
		catch(Exception e)
		{
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
		return "TEST";
	}
	
	
	@Override
	public RadiusEndpoint getProxyServer(RadiusPacket packet, RadiusEndpoint client) {
		RadiusEndpoint radendPoint=new RadiusEndpoint(client.getEndpointAddress(),client.getSharedSecret());
		return radendPoint;
	}
	
	
	@Override
	public RadiusPacket handlePacket(InetSocketAddress localAddress, InetSocketAddress remoteAddress, RadiusPacket request, String sharedSecret,RadiusPacket accessResponse)
	throws RadiusException, IOException {
		// handle incoming proxy packet
		if (localAddress.getPort() == getProxyPort()) {
			proxyPacketReceived(request, remoteAddress);
			return null;
		}
		
		// handle auth/acct packet
		RadiusEndpoint radiusClient = new RadiusEndpoint(remoteAddress, sharedSecret);
		RadiusEndpoint radiusServer = getProxyServer(request, radiusClient);
		if (radiusServer != null) {
			// proxy incoming packet to other radius server
			RadiusProxyConnection proxyConnection = new RadiusProxyConnection(radiusServer, radiusClient, request, localAddress.getPort());
			log.info("proxy packet to Address:" + proxyConnection.getRadiusServer().getEndpointAddress().getAddress()+":Port:"+proxyConnection.getRadiusServer().getEndpointAddress().getPort()+":Key:"+proxyConnection.getRadiusServer().getSharedSecret()+":");
			proxyPacket(request, proxyConnection,accessResponse);
			return null;
		} 
		else {
			log.info("Proxy Radius Server Null");
			return super.handlePacket(localAddress, remoteAddress, request, sharedSecret);
		}
	}
	
}
