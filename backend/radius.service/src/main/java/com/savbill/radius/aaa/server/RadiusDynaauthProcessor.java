package com.savbill.radius.aaa.server;

import com.savbill.radius.aaa.constant.AAAConstant;
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

public class RadiusDynaauthProcessor implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(RadiusDynaauthProcessor.class);

    DatagramSocket socket;
    String secret;
    RadiusPacket request;
    InetSocketAddress remoteAddress = null;

    String strUsername = null;
    Long lngPacketId = null;
    String sessionId = null;
    long lngStartTime = System.currentTimeMillis();


    public RadiusDynaauthProcessor(DatagramSocket socket, InetSocketAddress remoteAddress, String secret, RadiusPacket request) {
        super();
        this.socket = socket;
        this.secret = secret;
        this.request = request;
        this.remoteAddress = remoteAddress;

    }


    @Override
    public void run() {
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_AUTH);
        MDC.put(RadiusConstants.TRACE_ID, String.valueOf(new Date().getTime()));
        MDC.put(RadiusConstants.SPAN_ID, String.valueOf(new Date().getTime()));
        if (request.getAttribute("User-Name") != null)
            MDC.put(RadiusConstants.USERNAME, request.getAttribute("User-Name").getAttributeValue());
        if (request.getAttribute("Called-Station-Id") != null) {
            MDC.put(RadiusConstants.CALLEDSTATIONID, request.getAttribute("Called-Station-Id").getAttributeValue());
        }
        if (request.getAttribute("Calling-Station-Id") != null) {
            MDC.put(RadiusConstants.CALLINGSTATIONID, request.getAttribute("Calling-Station-Id").getAttributeValue());
        }
        try {

            log.debug("Started...");
            InetSocketAddress localAddress = (InetSocketAddress) socket.getLocalSocketAddress();
            if (secret == null) {
                //if (log.isInfoEnabled())
                log.debug("ignoring packet from unknown client " + remoteAddress + " received on local address " + localAddress);
            }
            if (log.isInfoEnabled()) {
                log.debug("received packet from " + remoteAddress + " on local address " + localAddress + ": " + request.getAttributes().toString());
            }
            RadiusPacket response = null;
            response = dynaAuthRequestReceived(request, remoteAddress);
            if (response != null) {
                log.debug("send response: " + response);
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
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
            MDC.remove(RadiusConstants.USERNAME);
            MDC.remove(RadiusConstants.CALLINGSTATIONID);
            MDC.remove(RadiusConstants.CALLEDSTATIONID);
        }

        long endTime = System.currentTimeMillis();
        log.debug("Ended. Time to Final Respond: " + (endTime - lngStartTime));
    }

    public RadiusPacket dynaAuthRequestReceived(RadiusPacket request, InetSocketAddress client) {
        RadiusPacket coadmResponse = new RadiusPacket();
        log.debug("DynaAuthRequestReceived with packet type: " + request.getPacketType());
        if (request.getPacketType() == 41) {
            coadmResponse = new RadiusPacket(AAAConstant.DISCONNECT_ACK, request.getPacketIdentifier());
        } else if (request.getPacketType() == 42) {
            coadmResponse = new RadiusPacket(AAAConstant.DISCONNECT_NAK, request.getPacketIdentifier());
        }
        if (request.getPacketType() == 44) {
            coadmResponse = new RadiusPacket(AAAConstant.COA_ACK, request.getPacketIdentifier());
        } else if (request.getPacketType() == 45) {
            coadmResponse = new RadiusPacket(AAAConstant.COA_NAK, request.getPacketIdentifier());
        } else if (request.getPacketType() == 40) {
            coadmResponse = new RadiusPacket(AAAConstant.DISCONNECT_REQUEST, request.getPacketIdentifier());
        }

        try {
            DynaAuthServiceImpl dynaauthImpl = new DynaAuthServiceImpl();
            coadmResponse = dynaauthImpl.dynaAuthRequestReceived(request, client);

        } catch (Exception e) {
            e.printStackTrace();
        }
        String coaDmResponse = "";
        if (coadmResponse != null && coadmResponse.getAttributes() != null)
            coaDmResponse = coadmResponse.getAttributes().toString();
        log.debug("Sending dynaAuth Response:[" + client.getAddress() + "-" + client.getPort() + "] " + coaDmResponse);
        log.debug(String.format("Sending dynaAuth Response:[ %s - %s], %s", client.getAddress(), client.getPort(), coaDmResponse));
        return coadmResponse;
    }

}
