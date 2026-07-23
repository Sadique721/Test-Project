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

public class AccountingService extends AuthAcctService {

    private static final Logger logger = LoggerFactory.getLogger(AccountingService.class);

    public AccountingService(InetAddress address, int intAcctPort) {
        super(address, intAcctPort);
    }

    @Override
    public RadiusPacket accessRequestReceived(AccessRequest accessRequest, InetSocketAddress client) throws RadiusException {
        return null;
    }

    @Override
    public RadiusPacket accountingRequestReceived(AccountingRequest accountingRequest, InetSocketAddress client) throws RadiusException {
        RadiusPacket answer = new RadiusPacket(RadiusPacket.ACCOUNTING_RESPONSE, accountingRequest.getPacketIdentifier());
        copyProxyState(accountingRequest, answer);
        return answer;
    }

    @Override
    public RadiusPacket dynaAuthRequestReceived(RadiusPacket dynaAuthRequest, InetSocketAddress client) throws RadiusException {
        return null;
    }

    @Override
    protected RadiusPacket handlePacket(InetSocketAddress localAddress, InetSocketAddress remoteAddress, RadiusPacket request, String sharedSecret) throws RadiusException, IOException {
        RadiusPacket response = null;

        // check for duplicates
//        if (!isPacketDuplicate(request, remoteAddress)) {
        RadiusAcctProcessor processor = new RadiusAcctProcessor(getDatagramSocket(), remoteAddress, sharedSecret, request);
        getAcctPool().execute(processor);

//        } else
//            logger.info("ignore duplicate packet");

        return response;
    }
}
