/**
 * $Id: RadiusServer.java,v 1.11 2008/04/24 05:22:50 wuttke Exp $
 * Created on 09.04.2005
 *
 * @author Matthias Wuttke
 * @version $Revision: 1.11 $
 */
package com.savbill.radius.aaa.util;

import com.savbill.radius.aaa.attribute.RadiusAttribute;
import com.savbill.radius.aaa.packet.AccessRequest;
import com.savbill.radius.aaa.packet.AccountingRequest;
import com.savbill.radius.aaa.packet.RadiusPacket;
import com.savbill.radius.aaa.server.RadiusAcctProcessor;
import com.savbill.radius.aaa.server.RadiusAuthProcessor;
import com.savbill.radius.aaa.server.RadiusDynaauthProcessor;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Implements a simple Radius server. This class must be subclassed to
 * provide an implementation for getSharedSecret() and getUserPassword().
 * If the server supports accounting, it must override
 * accountingRequestReceived().
 */
public abstract class RadiusServer {


    private ExecutorService authExecutor;

    private ExecutorService acctExecutor;

    private ExecutorService dynaExecutor;

    private static final Logger logger = LoggerFactory.getLogger(RadiusServer.class);

    /**
     * Returns the shared secret used to communicate with the client with the
     * passed IP address or null if the client is not allowed at this server.
     *
     * @param client IP address and port number of client
     * @return shared secret or null
     */
    public abstract String getSharedSecret(InetSocketAddress client);

    /**
     * Returns the password of the passed user. Either this
     * method or accessRequestReceived() should be overriden.
     *
     * @param userName user name
     * @return plain-text password or null if user unknown
     */
    public abstract String getUserPassword(String userName);

    /**
     * Constructs an answer for an Access-Request packet. Either this
     * method or isUserAuthenticated should be overriden.
     *
     * @param accessRequest Radius request packet
     * @param client        address of Radius client
     * @return response packet or null if no packet shall be sent
     * @throws RadiusException malformed request packet; if this
     *                         exception is thrown, no answer will be sent
     */
    public RadiusPacket accessRequestReceived(AccessRequest accessRequest, InetSocketAddress client)
            throws RadiusException {
        String plaintext = getUserPassword(accessRequest.getUserName());
        int type = RadiusPacket.ACCESS_REJECT;
        if (plaintext != null && accessRequest.verifyPassword(plaintext))
            type = RadiusPacket.ACCESS_ACCEPT;

        RadiusPacket answer = new RadiusPacket(type, accessRequest.getPacketIdentifier());
        copyProxyState(accessRequest, answer);
        return answer;
    }

    /**
     * Constructs an answer for an Accounting-Request packet. This method
     * should be overriden if accounting is supported.
     *
     * @param accountingRequest Radius request packet
     * @param client            address of Radius client
     * @return response packet or null if no packet shall be sent
     * @throws RadiusException malformed request packet; if this
     *                         exception is thrown, no answer will be sent
     */
    public RadiusPacket accountingRequestReceived(AccountingRequest accountingRequest, InetSocketAddress client)
            throws RadiusException {
        RadiusPacket answer = new RadiusPacket(RadiusPacket.ACCOUNTING_RESPONSE, accountingRequest.getPacketIdentifier());
        copyProxyState(accountingRequest, answer);
        return answer;
    }

    public RadiusPacket dynaAuthRequestReceived(RadiusPacket dynaAuthRequest, InetSocketAddress client)
            throws RadiusException {
        //Change will required here jk
        RadiusPacket answer = new RadiusPacket(RadiusPacket.COA_ACK, dynaAuthRequest.getPacketIdentifier());
        copyProxyState(dynaAuthRequest, answer);
        return answer;
    }

    /**
     * Starts the Radius server.
     *
     * @param listenAuth open auth port?
     * @param listenAcct open acct port?
     */
    public void start(boolean listenAuth, boolean listenAcct, boolean listendyna) {

        if (listenAuth) {
            new Thread() {
                public void run() {
                    setName("Radius Auth Listener");
                    try {
                        logger.info("starting RadiusAuthListener on port " + getAuthPort());
                        listenAuth();
                        logger.info("RadiusAuthListener is being terminated");
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("auth thread stopped by exception", e);
                    } finally {
                        authSocket.close();
                        logger.debug("auth socket closed");
                    }
                }
            }.start();
        }

        if (listenAcct) {
            new Thread() {
                public void run() {
                    setName("Radius Acct Listener");
                    try {
                        logger.info("starting RadiusAcctListener on port " + getAcctPort());
                        listenAcct();
                        logger.info("RadiusAcctListener is being terminated");
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("acct thread stopped by exception", e);
                    } finally {
                        acctSocket.close();
                        logger.debug("acct socket closed");
                    }
                }
            }.start();
        }

        if (listendyna) {
            new Thread() {
                public void run() {
                    setName("Radius listendyna Listener");
                    try {
                        logger.info("starting listendyna on port " + getdynaAuthPort());
                        listendynaauth();
                        logger.info("listendyna is being terminated");
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("listendyna thread stopped by exception", e);
                    } finally {
                        dynaauthSocket.close();
                        logger.debug("listendyna socket closed");
                    }
                }
            }.start();
        }
    }

    /**
     * Stops the server and closes the sockets.
     */
    public void stop() {
        logger.info("stopping Radius server");
        closing = true;
        if (authSocket != null)
            authSocket.close();
        if (acctSocket != null)
            acctSocket.close();
        if (dynaauthSocket != null)
            dynaauthSocket.close();
    }

    /**
     * Returns the auth port the server will listen on.
     *
     * @return auth port
     */
    public int getAuthPort() {
        return authPort;
    }


    public ExecutorService getAuthPool() {

        if (authExecutor == null) {
            System.out.println("AUTH POOL Initialized: min:" + authMinThreads + ",Max:" + authMaxThreads);
            authExecutor = new ThreadPoolExecutor(authMinThreads, authMaxThreads, threadBenchTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("AUTHPOOL-%d").build());
        }
        return authExecutor;
    }

    public ExecutorService getAcctPool() {

        if (acctExecutor == null) {
            System.out.println("ACCT POOL Initialized: min:" + acctMinThreads + ",Max:" + acctMaxThreads);
            acctExecutor = new ThreadPoolExecutor(acctMinThreads, acctMaxThreads, threadBenchTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("ACCTPOOL-%d").build());
        }
        return acctExecutor;
    }

    public ExecutorService getDynaAuthPool() {

        if (dynaExecutor == null) {
            System.out.println("getDynaAuthPool POOL Initialized: min:" + authMinThreads + ",Max:" + authMaxThreads);
            dynaExecutor = new ThreadPoolExecutor(authMinThreads, authMaxThreads, threadBenchTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("DYNAUTHPOOL-%d").build());
        }
        return dynaExecutor;
    }

    /**
     * Sets the auth port the server will listen on.
     *
     * @param authPort auth port, 1-65535
     */
    public void setAuthPort(int authPort) {
        if (authPort < 1 || authPort > 65535)
            throw new IllegalArgumentException("bad port number");
        this.authPort = authPort;
        this.authSocket = null;
    }

    /**
     * Returns the socket timeout (ms).
     *
     * @return socket timeout
     */
    public int getSocketTimeout() {
        return socketTimeout;
    }

    /**
     * Sets the socket timeout.
     *
     * @param socketTimeout socket timeout, >0 ms
     * @throws SocketException
     */
    public void setSocketTimeout(int socketTimeout)
            throws SocketException {
        if (socketTimeout < 1)
            throw new IllegalArgumentException("socket tiemout must be positive");
        this.socketTimeout = socketTimeout;
        if (authSocket != null)
            authSocket.setSoTimeout(socketTimeout);
        if (acctSocket != null)
            acctSocket.setSoTimeout(socketTimeout);
        if (dynaauthSocket != null)
            dynaauthSocket.setSoTimeout(socketTimeout);
    }

    /**
     * Sets the acct port the server will listen on.
     *
     * @param acctPort acct port 1-65535
     */
    public void setAcctPort(int acctPort) {
        if (acctPort < 1 || acctPort > 65535)
            throw new IllegalArgumentException("bad port number");
        this.acctPort = acctPort;
        this.acctSocket = null;
    }

    public void setDynaauthPort(int acctPort) {
        if (dynaPort < 1 || dynaPort > 65535)
            throw new IllegalArgumentException("bad port number");
        this.dynaPort = dynaPort;
        this.dynaauthSocket = null;
    }

    /**
     * Returns the acct port the server will listen on.
     *
     * @return acct port
     */
    public int getAcctPort() {
        return acctPort;
    }

    public int getdynaAuthPort() {
        return dynaPort;
    }

    /**
     * Returns the duplicate interval in ms.
     * A packet is discarded as a duplicate if in the duplicate interval
     * there was another packet with the same identifier originating from the
     * same address.
     *
     * @return duplicate interval (ms)
     */
    public long getDuplicateInterval() {
        return duplicateInterval;
    }

    /**
     * Sets the duplicate interval in ms.
     * A packet is discarded as a duplicate if in the duplicate interval
     * there was another packet with the same identifier originating from the
     * same address.
     *
     * @param duplicateInterval duplicate interval (ms), >0
     */
    public void setDuplicateInterval(long duplicateInterval) {
        if (duplicateInterval <= 0)
            throw new IllegalArgumentException("duplicate interval must be positive");
        this.duplicateInterval = duplicateInterval;
    }

    /**
     * Returns the IP address the server listens on.
     * Returns null if listening on the wildcard address.
     *
     * @return listen address or null
     */
    public InetAddress getListenAddress() {
        return listenAddress;
    }

    /**
     * Sets the address the server listens on.
     * Must be called before start().
     * Defaults to null, meaning listen on every
     * local address (wildcard address).
     *
     * @param listenAddress listen address or null
     */
    public void setListenAddress(InetAddress listenAddress) {
        this.listenAddress = listenAddress;
    }

    /**
     * Copies all Proxy-State attributes from the request
     * packet to the response packet.
     *
     * @param request request packet
     * @param answer  response packet
     */
    protected void copyProxyState(RadiusPacket request, RadiusPacket answer) {
        List proxyStateAttrs = request.getAttributes(33);
        for (Iterator i = proxyStateAttrs.iterator(); i.hasNext(); ) {
            RadiusAttribute proxyStateAttr = (RadiusAttribute) i.next();
            answer.addAttribute(proxyStateAttr);
        }
    }

    /**
     * Listens on the auth port (blocks the current thread).
     * Returns when stop() is called.
     *
     * @throws SocketException
     * @throws InterruptedException
     */
    protected void listenAuth()
            throws SocketException {
        listen(getAuthSocket());
    }

    /**
     * Listens on the acct port (blocks the current thread).
     * Returns when stop() is called.
     *
     * @throws SocketException
     * @throws InterruptedException
     */
    protected void listenAcct()
            throws SocketException {
        listen(getAcctSocket());
    }


    protected void listendynaauth()
            throws SocketException {
        listen(getdynauthSocket());
    }

    /**
     * Listens on the passed socket, blocks until stop() is called.
     *
     * @param s socket to listen on
     */
    protected void listen(final DatagramSocket s) {
        final DatagramPacket packetIn = new DatagramPacket(new byte[RadiusPacket.MAX_PACKET_LENGTH], RadiusPacket.MAX_PACKET_LENGTH);
        while (true) {
            try {
                // receive packet
                try {
                    logger.trace("about to call socket.receive()");
                    s.receive(packetIn);
//					Thread.yield();
                    if (logger.isDebugEnabled())
                        logger.debug("receive buffer size = " + s.getReceiveBufferSize());
                } catch (SocketException se) {
                    if (closing) {
                        // end thread
                        logger.info("got closing signal - end listen thread");
                        return;
                    } else {
                        // retry s.receive()
                        logger.error("SocketException during s.receive() -> retry", se);
                        continue;
                    }
                }

                try {

                    InetSocketAddress localAddress = (InetSocketAddress) s.getLocalSocketAddress();
                    InetSocketAddress remoteAddress = new InetSocketAddress(packetIn.getAddress(), packetIn.getPort());
                    String secret = getSharedSecret(remoteAddress);
                    RadiusPacket request = makeRadiusPacket(packetIn, secret);
                    packetIn.setData(new byte[RadiusPacket.MAX_PACKET_LENGTH], 0, RadiusPacket.MAX_PACKET_LENGTH);
                    handlePacket(localAddress, remoteAddress, request, secret);

                } catch (SocketTimeoutException ste) {
                    // this is expected behaviour
                    logger.trace("normal socket timeout");
                } catch (IOException ioe) {
                    // error while reading/writing socket
                    logger.error("communication error", ioe);
                } catch (RadiusException re) {
                    // malformed packet
                    re.printStackTrace();
                    logger.error("malformed Radius packet", re);
                } catch (Exception e) {
                    logger.error("ERROR:", e);
                    e.printStackTrace();
                }
            } catch (SocketTimeoutException ste) {
                // this is expected behaviour
                logger.trace("normal socket timeout");
            } catch (IOException ioe) {
                // error while reading/writing socket
                logger.error("communication error", ioe);
            }
        }
    }

    /**
     * Handles the received Radius packet and constructs a response.
     *
     * @param localAddress  local address the packet was received on
     * @param remoteAddress remote address the packet was sent by
     * @param request       the packet
     * @return response packet or null for no response
     * @throws RadiusException
     */
    protected RadiusPacket handlePacket(InetSocketAddress localAddress, InetSocketAddress remoteAddress, RadiusPacket request, String sharedSecret)
            throws RadiusException, IOException {
        RadiusPacket response = null;

        // check for duplicates
//		if (!isPacketDuplicate(request, remoteAddress)) {
        if (localAddress.getPort() == getAuthPort()) {
            RadiusAuthProcessor processor = new RadiusAuthProcessor(getAuthSocket(), remoteAddress, sharedSecret, request);
            getAuthPool().execute(processor);
        } else if (localAddress.getPort() == getAcctPort()) {
            RadiusAcctProcessor processor = new RadiusAcctProcessor(getAcctSocket(), remoteAddress, sharedSecret, request);
            getAcctPool().execute(processor);
        } else if (localAddress.getPort() == getdynaAuthPort()) {
            logger.info("Request Packet" + request);
            RadiusDynaauthProcessor processor = new RadiusDynaauthProcessor(getdynauthSocket(), remoteAddress, sharedSecret, request);
            getDynaAuthPool().execute(processor);
        } else {
            // ignore packet on unknown port
        }

//		} else
//			logger.info("ignore duplicate packet");

        return response;
    }

    /**
     * Returns a socket bound to the auth port.
     *
     * @return socket
     * @throws SocketException
     */
    protected DatagramSocket getAuthSocket()
            throws SocketException {
        if (authSocket == null) {
            if (getListenAddress() == null)
                authSocket = new DatagramSocket(getAuthPort());
            else
                authSocket = new DatagramSocket(getAuthPort(), getListenAddress());
            authSocket.setSoTimeout(getSocketTimeout());
        }
        return authSocket;
    }

    /**
     * Returns a socket bound to the acct port.
     *
     * @return socket
     * @throws SocketException
     */
    protected DatagramSocket getAcctSocket()
            throws SocketException {
        if (acctSocket == null) {
            if (getListenAddress() == null)
                acctSocket = new DatagramSocket(getAcctPort());
            else
                acctSocket = new DatagramSocket(getAcctPort(), getListenAddress());
            acctSocket.setSoTimeout(getSocketTimeout());
        }
        return acctSocket;
    }

    protected DatagramSocket getdynauthSocket()
            throws SocketException {
        if (dynaauthSocket == null) {
            if (getListenAddress() == null)
                dynaauthSocket = new DatagramSocket(getdynaAuthPort());
            else
                dynaauthSocket = new DatagramSocket(getdynaAuthPort(), getListenAddress());
            dynaauthSocket.setSoTimeout(getSocketTimeout());
        }
        return dynaauthSocket;
    }

    /**
     * Creates a Radius response datagram packet from a RadiusPacket to be send.
     *
     * @param packet  RadiusPacket
     * @param secret  shared secret to encode packet
     * @param address where to send the packet
     * @param port    destination port
     * @param request request packet
     * @return new datagram packet
     * @throws IOException
     */
    protected DatagramPacket makeDatagramPacket(RadiusPacket packet, String secret, InetAddress address, int port,
                                                RadiusPacket request)
            throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        packet.encodeResponsePacket(bos, secret, request);
        byte[] data = bos.toByteArray();

        DatagramPacket datagram = new DatagramPacket(data, data.length, address, port);
        return datagram;
    }

    /**
     * Creates a RadiusPacket for a Radius request from a received
     * datagram packet.
     *
     * @param packet received datagram
     * @return RadiusPacket object
     * @throws RadiusException malformed packet
     * @throws IOException     communication error (after getRetryCount()
     *                         retries)
     */
    protected RadiusPacket makeRadiusPacket(DatagramPacket packet, String sharedSecret)
            throws IOException, RadiusException {
        ByteArrayInputStream in = new ByteArrayInputStream(packet.getData());
        return RadiusPacket.decodeRequestPacket(in, sharedSecret);
    }

    /**
     * Checks whether the passed packet is a duplicate.
     * A packet is duplicate if another packet with the same identifier
     * has been sent from the same host in the last time.
     *
     * @param packet packet in question
     * @param address client address
     * @return true if it is duplicate
     */
    /*
    protected boolean isPacketDuplicate(RadiusPacket packet, InetSocketAddress address) {
        long now = System.currentTimeMillis();
        long intervalStart = now - getDuplicateInterval();

        byte[] authenticator = packet.getAuthenticator();

        synchronized (receivedPackets) {
            for (Iterator i = receivedPackets.iterator(); i.hasNext(); ) {
                ReceivedPacket p = (ReceivedPacket) i.next();
                if (p.receiveTime < intervalStart) {
                    // packet is older than duplicate interval
                    i.remove();
                } else {
                    if (p.address.equals(address) && p.packetIdentifier == packet.getPacketIdentifier()) {
                        if (authenticator != null && p.authenticator != null) {
                            // packet is duplicate if stored authenticator is equal
                            // to the packet authenticator
                            return Arrays.equals(p.authenticator, authenticator);
                        } else {
                            // should not happen, packet is duplicate
                            return true;
                        }
                    }
                }
            }

            // add packet to receive list
            ReceivedPacket rp = new ReceivedPacket();
            rp.address = address;
            rp.packetIdentifier = packet.getPacketIdentifier();
            rp.receiveTime = now;
            rp.authenticator = authenticator;
            receivedPackets.add(rp);
        }

        return false;
    }
    */
    private InetAddress listenAddress = null;
    private int authPort = 1812;
    private int acctPort = 1813;
    private int dynaPort = 3799;
    private DatagramSocket authSocket = null;
    private DatagramSocket acctSocket = null;
    private DatagramSocket dynaauthSocket = null;

    private int socketTimeout = 3000;
    private List receivedPackets = new LinkedList();
    private long duplicateInterval = 30000; // 20 s
    private boolean closing = false;

    private int authMinThreads = 250;
    private int authMaxThreads = 250;

    private int acctMinThreads = 250;
    private int acctMaxThreads = 250;

    private int threadBenchTime = 0;

    public int getAuthMinThreads() {
        return authMinThreads;
    }

    public void setAuthMinThreads(int authMinThreads) {
        this.authMinThreads = authMinThreads;
    }

    public int getAuthMaxThreads() {
        return authMaxThreads;
    }

    public void setAuthMaxThreads(int authMaxThreads) {
        this.authMaxThreads = authMaxThreads;
    }

    public int getAcctMinThreads() {
        return acctMinThreads;
    }

    public void setAcctMinThreads(int acctMinThreads) {
        this.acctMinThreads = acctMinThreads;
    }

    public int getAcctMaxThreads() {
        return acctMaxThreads;
    }

    public void setAcctMaxThreads(int acctMaxThreads) {
        this.acctMaxThreads = acctMaxThreads;
    }

    public int getThreadBenchTime() {
        return threadBenchTime;
    }

    public void setThreadBenchTime(int threadBenchTime) {
        this.threadBenchTime = threadBenchTime;
    }

}

/**
 * This internal class represents a packet that has been received by
 * the server.
 */
class ReceivedPacket {

    /**
     * The identifier of the packet.
     */
    public int packetIdentifier;

    /**
     * The time the packet was received.
     */
    public long receiveTime;

    /**
     * The address of the host who sent the packet.
     */
    public InetSocketAddress address;

    /**
     * Authenticator of the received packet.
     */
    public byte[] authenticator;

}
