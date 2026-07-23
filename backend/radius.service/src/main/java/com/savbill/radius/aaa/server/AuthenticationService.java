package com.savbill.radius.aaa.server;

import com.savbill.radius.aaa.eap.EAPMessageHandler;
import com.savbill.radius.aaa.packet.AccessRequest;
import com.savbill.radius.aaa.packet.AccountingRequest;
import com.savbill.radius.aaa.packet.RadiusPacket;
import com.savbill.radius.aaa.util.RadiusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class AuthenticationService extends AuthAcctService {


    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final EAPMessageHandler eapMessageHandler = new EAPMessageHandler();

    public AuthenticationService(InetAddress address, int intAuthPort) {
        super(address, intAuthPort);
    }

    @Override
    public RadiusPacket accessRequestReceived(AccessRequest accessRequest, InetSocketAddress client) throws RadiusException {
        String plaintext = getUserPassword(accessRequest.getUserName());
        int type = RadiusPacket.ACCESS_REJECT;
        if (plaintext != null && accessRequest.verifyPassword(plaintext))
            type = RadiusPacket.ACCESS_ACCEPT;

        RadiusPacket answer = new RadiusPacket(type, accessRequest.getPacketIdentifier());
        copyProxyState(accessRequest, answer);
        return answer;

    }

    @Override
    public RadiusPacket accountingRequestReceived(AccountingRequest accountingRequest, InetSocketAddress client) throws RadiusException {
        return null;
    }

    @Override
    public RadiusPacket dynaAuthRequestReceived(RadiusPacket dynaAuthRequest, InetSocketAddress client) throws RadiusException {
        return null;
    }

    @Override
    protected RadiusPacket handlePacket(InetSocketAddress localAddress, InetSocketAddress remoteAddress, RadiusPacket request, String sharedSecret) throws RadiusException, IOException {
        RadiusPacket response = null;

        // check for duplicates
//            if (!isPacketDuplicate(request, remoteAddress)) {
        if (localAddress.getPort() == getSocketPort()) {
            RadiusAuthProcessor processor = new RadiusAuthProcessor(getDatagramSocket(), remoteAddress, sharedSecret, request);
            processor.setEapMessageHandler(eapMessageHandler);
            getAuthPool().execute(processor);
        } else {
            // ignore packet on unknown port
        }

//            } else
//                logger.info("ignore duplicate packet");

        return response;
    }
}
