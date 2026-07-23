package com.savbill.radius.aaa.server;

import com.savbill.radius.aaa.packet.AccessRequest;
import com.savbill.radius.aaa.packet.AccountingRequest;
import com.savbill.radius.aaa.packet.RadiusPacket;
import com.savbill.radius.aaa.util.RadiusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class DynamicAuthenticationService extends AuthAcctService {


    private static final Logger logger = LoggerFactory.getLogger(DynamicAuthenticationService.class);


    public DynamicAuthenticationService(InetAddress address, int port) {
        super(address, port);
    }

    @Override
    public RadiusPacket accessRequestReceived(AccessRequest accessRequest, InetSocketAddress client) throws RadiusException {
        return null;
    }

    @Override
    public RadiusPacket accountingRequestReceived(AccountingRequest accountingRequest, InetSocketAddress client) throws RadiusException {
        return null;
    }

    @Override
    public RadiusPacket dynaAuthRequestReceived(RadiusPacket dynaAuthRequest, InetSocketAddress client) throws RadiusException {
        //Change will required here jk
        RadiusPacket answer = new RadiusPacket(RadiusPacket.COA_ACK, dynaAuthRequest.getPacketIdentifier());
        copyProxyState(dynaAuthRequest, answer);
        return answer;
    }

    @Override
    protected RadiusPacket handlePacket(InetSocketAddress localAddress, InetSocketAddress remoteAddress, RadiusPacket request, String sharedSecret) throws RadiusException, IOException {
        RadiusPacket response = null;

        // check for duplicates
//        if (!isPacketDuplicate(request, remoteAddress)) {
        RadiusDynaauthProcessor dynaauthProcessor = new RadiusDynaauthProcessor(getDatagramSocket(), remoteAddress, sharedSecret, request);
        getAuthPool().execute(dynaauthProcessor);

//        } else
//            logger.info("ignore duplicate packet");

        return response;
    }
}
