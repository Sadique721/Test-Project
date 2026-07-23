package com.savbill.radius.aaa.server;

import com.savbill.radius.aaa.constant.AAAConstant;
import com.savbill.radius.aaa.packet.AccountingRequest;
import com.savbill.radius.aaa.packet.RadiusPacket;
import com.savbill.radius.utils.RadiusConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.Date;

public class RadiusAcctProcessor implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RadiusAcctProcessor.class);

    DatagramSocket socket;
    String secret;
    RadiusPacket request;
    InetSocketAddress remoteAddress = null;

    String strUsername = null;
    Long lngPacketId = null;
    String sessionId = null;
    long lngStartTime = System.currentTimeMillis();


    public RadiusAcctProcessor(DatagramSocket socket, InetSocketAddress remoteAddress, String secret, RadiusPacket request) {
        super();
        this.socket = socket;
        this.secret = secret;
        this.request = request;
        this.remoteAddress = remoteAddress;

    }


    @Override
    public void run() {
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_ACCT);
        MDC.put(RadiusConstants.TRACE_ID, String.valueOf(new Date().getTime()));
        MDC.put(RadiusConstants.SPAN_ID, String.valueOf(new Date().getTime()));

        if (request.getAttribute("User-Name") != null) {
            MDC.put(RadiusConstants.USERNAME, request.getAttribute("User-Name").getAttributeValue());
        }
        if (request.getAttribute("Called-Station-Id") != null) {
            MDC.put(RadiusConstants.CALLEDSTATIONID, request.getAttribute("Called-Station-Id").getAttributeValue());
        }
        if (request.getAttribute("Calling-Station-Id") != null) {
            MDC.put(RadiusConstants.CALLINGSTATIONID, request.getAttribute("Calling-Station-Id").getAttributeValue());
        }
        try {

            log.debug("Started...");
            // check client
            InetSocketAddress localAddress = (InetSocketAddress) socket.getLocalSocketAddress();
            if (secret == null) {
                //if (log.isInfoEnabled())
                log.debug("ignoring packet from unknown client " + remoteAddress + " received on local address " + localAddress);
            }
            log.warn("received packet from " + remoteAddress + " on local address " + localAddress + ": " + request.getAttributes().toString());
//            // parse packet
//            if (log.isInfoEnabled()) {
//                log.info("received packet from " + remoteAddress + " on local address " + localAddress + ": " + request.getAttributes().toString());
//            }
            // handle packet
            //log.trace("about to call RadiusServer.handlePacket()");
            RadiusPacket response = null;
            if (request.getPacketType() == 12) {
                response = new RadiusPacket(AAAConstant.ACCOUNTING_RESPONSE, request.getPacketIdentifier());
                log.warn("Found Packet status type: " + request.getPacketType());
                if (response != null) {
                    log.warn("send response: " + response.getAttributes().toString());
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    response.encodeResponsePacket(bos, secret, request);
                    byte[] data = bos.toByteArray();
                    DatagramPacket packetOut = new DatagramPacket(data, data.length, remoteAddress.getAddress(), remoteAddress.getPort());
                    socket.send(packetOut);
                } else {
                    log.debug("no response sent");
                }
            }
            if (request instanceof AccountingRequest)
                response = accountingRequestReceived((AccountingRequest) request, remoteAddress);

            // send response
            if (response != null) {
                //if (log.isInfoEnabled())
                log.warn("send response: " + response.getAttributes().toString());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                response.encodeResponsePacket(bos, secret, request);
                byte[] data = bos.toByteArray();
                DatagramPacket packetOut = new DatagramPacket(data, data.length, remoteAddress.getAddress(), remoteAddress.getPort());
                socket.send(packetOut);
            } else {
                log.debug("no response sent");
            }
        } catch (SocketTimeoutException ste) {
            // this is expected behaviour
            //log.trace("normal socket timeout");
        } catch (IOException ioe) {
            // error while reading/writing socket
            log.error("communication error", ioe);
        } catch (Exception e) {
            log.error("ERROR:", e);
            e.printStackTrace();
        } finally {
            long endTime = System.currentTimeMillis();
            log.warn("Ended. Time to Final Respond Acct: " + (endTime - lngStartTime));
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
            MDC.remove(RadiusConstants.USERNAME);
            MDC.remove(RadiusConstants.CALLINGSTATIONID);
            MDC.remove(RadiusConstants.CALLEDSTATIONID);
        }
    }

    public RadiusPacket accountingRequestReceived(AccountingRequest request, InetSocketAddress client) {
        RadiusPacket accoutningResponse = new RadiusPacket(AAAConstant.ACCOUNTING_RESPONSE, request.getPacketIdentifier());
        try {
            AcctingServiceImpl authImpl = new AcctingServiceImpl();
            accoutningResponse = authImpl.accountingRequestReceived(request, client);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("Sending Accounting :[" + client.getAddress() + "-" + client.getPort() + "] " + " Response:[" + accoutningResponse.getAttributes().toString() + "]");
        log.debug(String.format("Sending Accounting Response:[ %s - %s], %s", client.getAddress(), client.getPort(), accoutningResponse.getAttributes().toString()));
        return accoutningResponse;
    }

}
