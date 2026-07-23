package com.savbill.radius.aaa.server;

import com.savbill.radius.aaa.constant.AAAConstant;
import com.savbill.radius.aaa.eap.EAPMessageHandler;
import com.savbill.radius.aaa.packet.AccessRequest;
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

public class RadiusAuthProcessor implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RadiusAuthProcessor.class);

    DatagramSocket socket;
    String secret;
    RadiusPacket request;
    InetSocketAddress remoteAddress = null;

    String strUsername = null;
    Long lngPacketId = null;
    String sessionId = null;
    long lngStartTime = System.currentTimeMillis();
    private EAPMessageHandler eapMessageHandler;

    public RadiusAuthProcessor(DatagramSocket socket, InetSocketAddress remoteAddress, String secret, RadiusPacket request) {
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
        MDC.put(RadiusConstants.USERNAME, request.getAttribute("User-Name").getAttributeValue());
        if (request.getAttribute("Called-Station-Id") != null) {
            MDC.put(RadiusConstants.CALLEDSTATIONID, request.getAttribute("Called-Station-Id").getAttributeValue());
        }
        if (request.getAttribute("Calling-Station-Id") != null) {
            MDC.put(RadiusConstants.CALLINGSTATIONID, request.getAttribute("Calling-Station-Id").getAttributeValue());
        }
        try {
            // check client
            log.debug("Started...");
            //log.trace("about to call RadiusServer.handlePacket()");
            log.debug("Start Reading Packet Data");
            log.debug("received packet from " + remoteAddress + ":" + request.getAttributes().toString());
            log.debug("Stop Reading Packet Data");
            RadiusPacket response = null;
            if (request instanceof AccessRequest) {
                response = accessRequestReceived((AccessRequest) request, remoteAddress);
            }

            if (response != null) {
                //DatagramPacket packetOut = makeDatagramPacket(response, secret, remoteAddress.getAddress(), packetIn.getPort(), request);
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
            log.error("Socket Timeout:" + ste, ste);
        } catch (IOException ioe) {
            log.error("communication error:" + ioe, ioe);
        } catch (Exception e) {
            log.error("ERROR:" + e, e);
        } finally {
            long endTime = System.currentTimeMillis();
            log.warn("Time Taken To Process Authentication Request :" + (endTime - lngStartTime));
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
            MDC.remove(RadiusConstants.USERNAME);
            MDC.remove(RadiusConstants.CALLINGSTATIONID);
            MDC.remove(RadiusConstants.CALLEDSTATIONID);
        }

    }

    public RadiusPacket accessRequestReceived(AccessRequest request, InetSocketAddress client) {
        long startTime = System.currentTimeMillis();
        log.warn("Received Access Request from:" + client.getAddress() + ":Packet:" + request.toString());
        RadiusPacket accessResponse = new RadiusPacket(AAAConstant.ACCESS_REJECT, request.getPacketIdentifier());
        try {
            AuthServiceImpl authImpl = new AuthServiceImpl();
            authImpl.setEapMessageHandler(eapMessageHandler);
            accessResponse = authImpl.accessRequestReceived(request, client);
        } catch (Exception e) {
            log.error("Error while performing access request: [" + e.getMessage() + "] with request: [" + request.getAttributes().toString() + "]");
            accessResponse.addAttribute("Reply-Message", "Internal Error Authentication Fail");
            accessResponse.setPacketType(AAAConstant.ACCESS_REJECT);
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        log.debug("Time Taken To Process Authentication Request : " + (endTime - startTime));
        log.debug("Sending Access Response:[" + client.getAddress() + "-" + client.getPort() + "]" + accessResponse.getAttributes().toString());
        return accessResponse;
    }

    public void setEapMessageHandler(EAPMessageHandler eapMessageHandler) {
        this.eapMessageHandler = eapMessageHandler;
    }
}


