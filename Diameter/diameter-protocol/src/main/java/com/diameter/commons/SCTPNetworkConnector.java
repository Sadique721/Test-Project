package com.diameter.commons;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;

import org.apache.logging.log4j.ThreadContext;

import com.diameter.commons.EliteThreadFactory.EliteThread;
import com.diameter.stack.DiameterStack;
import com.sun.nio.sctp.Association;	//NOSONAR
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;
import com.sun.nio.sctp.SctpStandardSocketOptions;
public class SCTPNetworkConnector implements INetworkConnector {

	protected static final String MODULE = "SCTP-NET-CONNECTOR";
	private static final short DEFAULT_BUFFER_SIZE = -1;
	private static final int SO_LINGER_SECONDS = 1;
	
	private final DiameterStack stack;
	private ArrayList<AssociationHandler> associationHandlers;
	private ConnectorContext context;
	protected int socketReceiveBufferSize;
	protected int socketSendBufferSize;
	private String networkAddress;
	private int port;
	private SecurityStandard securityStandard;
	private EliteSSLParameter secutiryParameter;
	private SocketDetail boundSocketDetail;
	private ConnectorState currentState = ConnectorState.NOT_STARTED;
	private InetSocketAddress serverSocketAddress;
	private ServiceRemarks remarks;
	private boolean stopRequested;
	private SctpServerChannel serverChannel;

	public SCTPNetworkConnector(DiameterStack stack) {
		this.stack = stack;
		this.associationHandlers = new ArrayList<>();
		context = new ConnectorContextImpl();
	}

	@Override
	public boolean start(ConnectionFactory connectionFactory) {
		boundSocketDetail = new SocketDetail(networkAddress, port);
		if (currentState == ConnectorState.NOT_STARTED || currentState == ConnectorState.STOPPED) {
			currentState = ConnectorState.STARTUP_IN_PROGRESS;
			
			try {
				port = 3869;
				InetAddress bindAddress = InetAddress.getByName(networkAddress);
				serverSocketAddress = new InetSocketAddress(bindAddress, port);
				serverChannel = SctpServerChannel.open();
				serverChannel.bind(serverSocketAddress);
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Server socket port: " + port);
					LogManager.getLogger().info(MODULE, "Server socket ip: " + bindAddress);
				}
				
			} catch (IOException e) { 
				Closeables.closeQuietly(serverChannel);
				LogManager.getLogger().warn(MODULE, "Problem while binding configured service on address: " + networkAddress + ", Reason: " + e.getMessage()
						+ ", service will attempt to listen on " + CommonConstants.UNIVERSAL_IP);
				
				if ( ! bindServiceOnUniversalIp(port)) {
					
					if (port != DiameterConstants.DIAMETER_SERVICE_PORT) {
						
						LogManager.getLogger().warn(MODULE, "Failed to start service on socket: " + CommonConstants.UNIVERSAL_IP + ":" + port 
								+ ". Reason: " + e.getMessage()
								+  ", service will attempt to listen on socket: " + CommonConstants.UNIVERSAL_IP + ":" + DiameterConstants.DIAMETER_SERVICE_PORT);
						
						if ( ! bindServiceOnUniversalIp(DiameterConstants.DIAMETER_SERVICE_PORT)) {
							LogManager.getLogger().warn(MODULE, "Failed to start service on socket: " + CommonConstants.UNIVERSAL_IP + ":" + DiameterConstants.DIAMETER_SERVICE_PORT 
									+ ". Reason: " + e.getMessage());
							return false;
						} 
					} else {
						
						LogManager.getLogger().warn(MODULE, "Failed to start service on socket: " + CommonConstants.UNIVERSAL_IP + ":" + DiameterConstants.DIAMETER_SERVICE_PORT 
								+ ". Reason: " + e.getMessage());
						return false;
					}
				}
				
				LogManager.ignoreTrace(e);
			}
			try {
				EliteThread connListenerThread = new EliteThread(new ConnectionListener(), CommonConstants.DIAMETER_STACK_IDENTIFIER + "-CONN-LIS-THR", CommonConstants.DIAMETER_STACK_IDENTIFIER);
				connListenerThread.setPriority(stack.getMainThreadPriority());
				connListenerThread.start();
			} catch(Exception exp) { 
				remarks = ServiceRemarks.UNKNOWN_PROBLEM;
				currentState = ConnectorState.NOT_STARTED;
				LogManager.getLogger().error(MODULE,"Failed to start " + CommonConstants.DIAMETER_STACK_IDENTIFIER + ". Reason: " + exp.getMessage());
				LogManager.getLogger().trace(MODULE, exp);
				return false; 
			}
		}
		return true;
	}
	
	private boolean bindServiceOnUniversalIp(int port) {

		try {
			InetAddress bindAddress = InetAddress.getByName(CommonConstants.UNIVERSAL_IP);

			serverSocketAddress = new InetSocketAddress(bindAddress, port);
			remarks = ServiceRemarks.STARTED_ON_UNIVERSAL_IP;
			boundSocketDetail = new SocketDetail(serverSocketAddress.getHostName(), port);
			serverChannel = SctpServerChannel.open();
			serverChannel.bind(serverSocketAddress);
		} catch (Exception e) {
			Closeables.closeQuietly(serverChannel);
			remarks = ServiceRemarks.PROBLEM_BINDING_IP_PORT;
			currentState = ConnectorState.NOT_STARTED;
			LogManager.getLogger().error(MODULE, "Failed to start service on universal IP. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return false;
		}

		return true;
	}

	@Override
	public void openConnection(IPeerListener peerListener) {
		ConnectionSender connectionSender = new ConnectionSender(peerListener);
        Thread connectionSendThread = new EliteThread(connectionSender, CommonConstants.DIAMETER_STACK_IDENTIFIER + "-CONN-SEND-THR", CommonConstants.DIAMETER_STACK_IDENTIFIER);
        connectionSendThread.setPriority(stack.getMainThreadPriority());
        connectionSendThread.start();
	}
	
	private final class ConnectorContextImpl implements ConnectorContext {
		@Override
		public int getSocketReceiveBufferSize() {
			return socketReceiveBufferSize;
		}

		@Override
		public int getSocketSendBufferSize() {
			return socketSendBufferSize;
		}

		@Override
		public boolean removeConnectionHandler(NetworkConnectionHandler handler) {
			if(handler != null){
				associationHandlers.remove(handler);
				LogManager.getLogger().warn(MODULE, "Total connection handlers are: " + associationHandlers.size());
				return true;
			}
			return false;
		}

		@Override
		public void executeInAsync(Packet packet, NetworkConnectionHandler handler) {

			try {
				stack.addMDC((DiameterPacket) packet);
				PacketProcessImpl packetProcess = new PacketProcessImpl(packet, handler, stack, ThreadContext.getContext());
				stack.submitToWorker(packetProcess);
			} catch (Exception exp) {
				LogManager.getLogger().warn(MODULE, exp.getMessage());
				LogManager.getLogger().trace(MODULE, exp);
			} finally {
				stack.clearMDC();
			}
		}

		@Override
		public String getNetworkAddress() {
			return serverSocketAddress.getAddress().getHostAddress();
		}

		@Override
		public int getNetworkPort() {
			return port;
		}

		@Override
		public void executeInSync(Packet packet,NetworkConnectionHandler handler) {
			stack.handleReceivedMessage(packet, handler);
		}

		@Override
		public PeerConnectionData getPeerConnectionData(String ipAddress) {
			return stack.getStackContext().getPeerData(ipAddress);
		}

		@Override
		public <T> ScheduledFuture<T> scheduleCallableSingleExecutionTask(CallableSingleExecutionAsyncTask<T> task) {
			return stack.getStackContext().scheduleCallableSingleExecutionTask(task);
		}

		@Override
		public SecurityStandard getDefaultSecurityStandard() {
			return SCTPNetworkConnector.this.geSecurityStandard();
		}

		@Override
		public EliteSSLContextExt createEliteSSLContext() throws Exception {
			EliteSSLContextFactory eliteSSLContextFactory = stack.getStackContext().getEliteSSLContextFactory();
			return eliteSSLContextFactory.createSSLContext(getDefalutSSLParameter());
		}
	}

	public class ConnectionSender implements Runnable {

		private IPeerListener peerListener;

		public ConnectionSender(IPeerListener peerListener) {
			this.peerListener = peerListener;
		}

		@Override
		public void run() {
			ConnectionEvents connectionEvent = ConnectionEvents.CONNECTION_FAILURE;
			DiameterPeerEvent peerEvent = DiameterPeerEvent.IRcvConnNack;
			
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				String logMessageToAppend =  peerListener.getLocalIpAddress() == null ? "" : ", with local address: " + peerListener.getLocalIpAddress() + ":" + peerListener.getLocalPort(); 
				LogManager.getLogger().info(MODULE, "Attempting connection to " + peerListener.getRemoteInetAddress()+ ":" + peerListener.getCommunicationPort() + logMessageToAppend);
			}
			InetSocketAddress localAddress = null;
			if (peerListener.getLocalIpAddress() != null) {
				localAddress = new InetSocketAddress(peerListener.getLocalIpAddress().trim(), peerListener.getLocalPort());
			} else {
				localAddress = new InetSocketAddress(peerListener.getLocalPort());
			}
			
			InetSocketAddress remoteAddress = new InetSocketAddress(peerListener.getRemoteInetAddress(), peerListener.getCommunicationPort());
			SctpChannel channel;
			Association association;
			try {
				channel = SctpChannel.open(); //NOSONAR - AssociationHandler will close it.
				channel.bind(localAddress);
				channel.connect(remoteAddress);
				association = channel.association();
			} catch (Exception e) {
				LogManager.getLogger().warn(MODULE, "Unable to open SCTP channel, Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
				peerListener.handleEvent(peerEvent, connectionEvent);
				return;
			}
			
			setChannelParameters(channel, remoteAddress, peerListener.getPeerData());
			
			AssociationHandler associationHandler = new AssociationHandler(channel, association, remoteAddress, 
					ConnectionRole.Initiator, context, localAddress);
			EliteThread associactionHandlerThread = new EliteThread(associationHandler, CommonConstants.DIAMETER_STACK_IDENTIFIER 
					+ "-CONN-HL" + remoteAddress, CommonConstants.DIAMETER_STACK_IDENTIFIER);
			associactionHandlerThread.start();
			associationHandlers.add(associationHandler);
			peerListener.setConnectionListener(associationHandler);
			peerListener.handleEvent(DiameterPeerEvent.IRcvConnAck, ConnectionEvents.CONNECTION_CREATED);
			
		}
		
	}

	@Override
	public boolean stop() {
		if(stopRequested == true){
			LogManager.getLogger().debug(MODULE, "Shutdown in progress");
			return true;
		}
		
		this.stopRequested = true;
		
		associationHandlers.forEach(handler -> handler.closeConnection(ConnectionEvents.SHUTDOWN));
		
		try {
			serverChannel.close();
		} catch (IOException e) {
			LogManager.getLogger().warn(MODULE, "Unable to stop Server Channel for " 
					+ serverSocketAddress + " Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return false;
		}
		return true;
	}
	
	private class ConnectionListener implements Runnable {

		@Override
		public void run() {
			currentState = ConnectorState.RUNNING;
			while(serverChannel.isOpen()) {
				SctpChannel sctpChannel = null;
				try {
					sctpChannel = serverChannel.accept();
					InetSocketAddress address = (InetSocketAddress)sctpChannel.getOption(SctpStandardSocketOptions.SCTP_PRIMARY_ADDR);
					Association association = sctpChannel.association();
					PeerData peerData = stack.getPeerData(address.getAddress().getHostAddress());
					if (peerData == null) {
						peerData = stack.getPeerData(address.getHostName());
					}
					
					setChannelParameters(sctpChannel, address, peerData);
					AssociationHandler associationHandler = new AssociationHandler(sctpChannel, association, address, 
							ConnectionRole.Responder, context, serverSocketAddress);
					EliteThread associactionHandlerThread = new EliteThread(associationHandler, CommonConstants.DIAMETER_STACK_IDENTIFIER 
							+ "-CONN-HL" + address, CommonConstants.DIAMETER_STACK_IDENTIFIER);
					associactionHandlerThread.start();
					associationHandlers.add(associationHandler);
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Successfully Added new connection handler: " 
								+ associactionHandlerThread.getName());
						LogManager.getLogger().debug(MODULE, "Total connection handlers are: " + associationHandlers.size());
					}
				} catch (Exception e) {
					LogManager.getLogger().warn(MODULE, "Unable to accept channel, Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
					Closeables.closeQuietly(sctpChannel);
				}
			}
			currentState = ConnectorState.STOPPED;
		}
		
	}
	
	private void setChannelParameters(SctpChannel sctpChannel, InetSocketAddress address, PeerData peerData) {
		String clientAddress = address.getAddress().getHostAddress();
		int clientPort = address.getPort();
		
		int sockRecBufSize;
		int sockSendBufSize;
		boolean nagleAlgo;
		if (peerData != null ){
			sockRecBufSize = peerData.getSocketReceiveBufferSize();
			sockSendBufSize = peerData.getSocketSendBufferSize();
			nagleAlgo = peerData.isNagleAlgoEnabled();
		
		} else {
			sockRecBufSize = context.getSocketReceiveBufferSize();
			sockSendBufSize = context.getSocketSendBufferSize();
			nagleAlgo = false;
		}
		
		try {
			if (sockSendBufSize == DEFAULT_BUFFER_SIZE) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Configured send buffer size: " + sockSendBufSize + " for " + clientAddress 
							+ ":" + clientPort + ", so using OS default send buffer size: " + sctpChannel.getOption(SctpStandardSocketOptions.SO_SNDBUF));
				}
			} else {
				sctpChannel.setOption(SctpStandardSocketOptions.SO_SNDBUF, sockSendBufSize);
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Socket send buffer size for " + clientAddress + ":" + clientPort + " is " + sockSendBufSize);
				}
			}

		} catch (IOException e) {
				LogManager.getLogger().warn(MODULE, "Invalid send buffer size: " + sockSendBufSize + " configured for " + clientAddress + ":" + clientPort 
						+ ", so using OS default send buffer size.");
				LogManager.getLogger().trace(MODULE, e);
		}
		
		try { 
			if (sockRecBufSize == DEFAULT_BUFFER_SIZE) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Configured receive buffer size: " + sockRecBufSize + " for " + clientAddress 
							+ ":" + clientPort + ", so using OS default receive buffer size: " + sctpChannel.getOption(SctpStandardSocketOptions.SO_RCVBUF));
				}
			} else {
				sctpChannel.setOption(SctpStandardSocketOptions.SO_RCVBUF, sockRecBufSize);
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Socket receive buffer size for " + clientAddress + ":" + clientPort + " is " + sockRecBufSize);
				}
			}
		} catch (IOException e) {
				LogManager.getLogger().warn(MODULE, "Invalid receive buffer size: " + sockRecBufSize + " configured for " + clientAddress + ":" + clientPort 
						+ ", so using OS default send buffer size.");
				LogManager.getLogger().trace(MODULE, e);
		}
		
		try {
			sctpChannel.setOption(SctpStandardSocketOptions.SCTP_NODELAY, !nagleAlgo);
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "SCTP No-Delay set for: " + clientAddress+":"+clientPort + " is: " + !nagleAlgo);
			}
		} catch (IOException e) {
			// this exception occurs when passed value in seOption is not boolean
			// so this will not occur
			LogManager.getLogger().trace(MODULE, e);
		}
		
		try {
			sctpChannel.setOption(SctpStandardSocketOptions.SO_LINGER, SO_LINGER_SECONDS);
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "SCTP So Linger is set for: " + 
						clientAddress+":"+clientPort + 
						" is: " + SO_LINGER_SECONDS);
			}
		} catch (IOException e) {
			LogManager.getLogger().warn(MODULE, "Default TCP So Linger is set for: " + 
						clientAddress+":"+clientPort);
			LogManager.getLogger().trace(MODULE, e);
		}
	}
	
	@Override
	public String getNetworkAddress() {
		return networkAddress;
	}

	@Override
	public int getNetworkPort() {
		return port;
	}

	@Override
	public SocketDetail getBondSocketDetail() {
		return boundSocketDetail;
	}

	@Override
	public ServiceRemarks getRemarks() {
		return remarks;
	}

	@Override
	public EliteSSLParameter getDefalutSSLParameter() {
		return secutiryParameter;
	}

	@Override
	public SecurityStandard geSecurityStandard() {
		return securityStandard;
	}

	@Override
	public TransportProtocols getTransportProtocol() {
		return TransportProtocols.SCTP;
	}

	public void setSocketReceiveBufferSize(int socketReceiveBufferSize) {
		this.socketReceiveBufferSize = socketReceiveBufferSize;
		
	}

	public void setSocketSendBufferSize(int socketSendBufferSize) {
		this.socketSendBufferSize = socketSendBufferSize;
		
	}

	public void setNetworkAddress(String networkAddress) {
		this.networkAddress = networkAddress;
	}

	public void setNetworkPort(int port) {
		this.port = port;
	}

	public void setSecurityStandard(SecurityStandard securityStandard) {
		this.securityStandard = securityStandard;
	}

	public void setDefalutSSLParameter(EliteSSLParameter secutiryParameter) {
		this.secutiryParameter = secutiryParameter;
	}

}
