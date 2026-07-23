package com.savbill.radius.aaa.server;

import com.savbill.radius.aaa.attribute.RadiusAttribute;
import com.savbill.radius.aaa.packet.AccessRequest;
import com.savbill.radius.aaa.packet.AccountingRequest;
import com.savbill.radius.aaa.packet.RadiusPacket;
import com.savbill.radius.aaa.util.RadiusException;
import com.savbill.radius.entity.Client;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Iterator;
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
public abstract class AuthAcctService {

    private static final Logger log = LoggerFactory.getLogger(AuthAcctService.class);

    private InetAddress listenAddress = null;
    private ExecutorService authService;

    private ExecutorService acctExecutor;

    private static final Logger logger = LoggerFactory.getLogger(AuthAcctService.class);

    public AuthAcctService(InetAddress address, int intAuthPort) {
        this.listenAddress = address;
        this.SocketPort = intAuthPort;
    }

    /**
     * Returns the shared secret used to communicate with the client with the
     * passed IP address or null if the client is not allowed at this server.
     *
     * @param client IP address and port number of client
     * @return shared secret or null
     */
//	public abstract String getSharedSecret(InetSocketAddress client);
    public String getSharedSecret(InetSocketAddress client) {
        RadiusUtility radUtil = new RadiusUtility();
        try {
            log.debug("In Method getSharedSecret");
            Client cltData = radUtil.identifyClientOnly(client.getAddress().toString().substring(1),null);
            if (log.isDebugEnabled()) {
                log.debug(String.format("Recevied Request From : %s Key is %s and mvno is %s", client.getAddress().toString().substring(1), cltData.getSharedKey(), cltData.getMvnoId()));
            }
            return cltData.getSharedKey();
        } catch (Exception e) {
            log.error("Error while getSharedSecret" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Returns the password of the passed user. Either this
     * method or accessRequestReceived() should be overriden.
     *
     * @param userName user name
     * @return plain-text password or null if user unknown
     */
    public String getUserPassword(String userName) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("UserPassword %s", userName));
        }
        return "DUMMY";
    }

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
    public abstract RadiusPacket accessRequestReceived(AccessRequest accessRequest, InetSocketAddress client)
            throws RadiusException;

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
    public abstract RadiusPacket accountingRequestReceived(AccountingRequest accountingRequest, InetSocketAddress client)
            throws RadiusException;

    public abstract RadiusPacket dynaAuthRequestReceived(RadiusPacket dynaAuthRequest, InetSocketAddress client)
            throws RadiusException;

    /**
     * Starts the Radius server.
     */
    public void start() {

        new Thread() {
            public void run() {
                setName("Radius Auth Listener");
                try {
                    logger.info("starting RadiusAuthListener on port " + getSocketPort());
                    listenAuth();
                    logger.info("RadiusAuthListener is being terminated");
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("auth thread stopped by exception", e);
                } finally {
                    datagramSocket.close();
                    logger.debug("auth socket closed");
                }
            }
        }.start();

    }

    /**
     * Stops the server and closes the sockets.
     */
    public void stop() {
        logger.info("stopping Radius server");
        closing = true;
        if (datagramSocket != null)
            datagramSocket.close();
    }

    /**
     * Returns the auth port the server will listen on.
     *
     * @return auth port
     */
    public int getSocketPort() {
        return SocketPort;
    }


    public ExecutorService getAuthPool() {

        if (authService == null) {
            System.out.println("AUTH POOL Initialized: min:" + authMinThreads + ",Max:" + authMaxThreads+" ,queueCapacity: "+queueCapacity);
            authService = new ThreadPoolExecutor(authMinThreads, authMaxThreads, threadBenchTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(queueCapacity), new ThreadFactoryBuilder().setNameFormat("AUTHPOOL-%d").build());
        }
        return authService;
    }

    public ExecutorService getAcctPool() {

        if (acctExecutor == null) {
            System.out.println("ACCT POOL Initialized: min:" + acctMinThreads + ",Max:" + acctMaxThreads+" ,queueCapacity: "+queueCapacity);
            acctExecutor = new ThreadPoolExecutor(acctMinThreads, acctMaxThreads, threadBenchTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(queueCapacity), new ThreadFactoryBuilder().setNameFormat("ACCTPOOL-%d").build());
        }
        return acctExecutor;
    }

    /**
     * Sets the auth port the server will listen on.
     *
     * @param socketPort auth port, 1-65535
     */
    public void setSocketPort(int socketPort) {
        if (socketPort < 1 || socketPort > 65535)
            throw new IllegalArgumentException("bad port number");
        this.SocketPort = socketPort;
        this.datagramSocket = null;
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
        if (datagramSocket != null)
            datagramSocket.setSoTimeout(socketTimeout);
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
        listen(getDatagramSocket());
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
    protected abstract RadiusPacket handlePacket(InetSocketAddress localAddress, InetSocketAddress remoteAddress, RadiusPacket request, String sharedSecret)
            throws RadiusException, IOException;

    /**
     * Returns a socket bound to the auth port.
     *
     * @return socket
     * @throws SocketException
     */
    protected DatagramSocket getDatagramSocket()
            throws SocketException {
        if (datagramSocket == null) {
            if (getListenAddress() == null)
                datagramSocket = new DatagramSocket(getSocketPort());
            else
                datagramSocket = new DatagramSocket(getSocketPort(), getListenAddress());
            datagramSocket.setSoTimeout(getSocketTimeout());
        }
        return datagramSocket;
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
     * @param packet  packet in question
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
    private int SocketPort = 1812;
    private DatagramSocket datagramSocket = null;
    private int socketTimeout = 3000;
    //    private List receivedPackets = new LinkedList();
    private long duplicateInterval = 30000; // 20 s
    private boolean closing = false;

    private int authMinThreads = 250;
    private int authMaxThreads = 250;

    private int acctMinThreads = 250;
    private int acctMaxThreads = 250;

    private int threadBenchTime = 0;

    private int queueCapacity = 5000;

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

    public int getThreadBenchTime() {
        return threadBenchTime;
    }

    public void setThreadBenchTime(int threadBenchTime) {
        this.threadBenchTime = threadBenchTime;
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

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
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
