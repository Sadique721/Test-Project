package com.diameter.commons;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import javax.net.SocketFactory;

import org.apache.logging.log4j.ThreadContext;

import com.diameter.stack.DiameterStack;

public abstract class TCPNetworkConnector implements INetworkConnector {
	public static final int CONNECTION_TIMEOUT_MS = 3000;

	public static final int SOCKET_IDLE_TIMEOUT_MS = 2000;

	private ConnectorState currentState = ConnectorState.NOT_STARTED;

	private ServerSocket serverSocket;

	protected DiameterStack stack;

	private SocketDetail boundSocketDetail;

	private Thread connectionLSNRThread;

	private List<ConnectionHandler> totalConnectionHandlers;

	private static final String MODULE = "TCP-NET-CONNECTOR";

	private ConnectorContext context;

	private ConnectionFactory connectionFactory;

	private ServiceRemarks remarks;

	private boolean stopRequested = false;

	public TCPNetworkConnector(final DiameterStack stack) {
		this.stack = stack;
		this.totalConnectionHandlers = new ArrayList<>();
		this.context = new ConnectorContext() {
			public int getSocketReceiveBufferSize() {
				return TCPNetworkConnector.this.getSocketReceiveBufferSize();
			}

			public int getSocketSendBufferSize() {
				return TCPNetworkConnector.this.getSocketSendBufferSize();
			}

			public boolean removeConnectionHandler(NetworkConnectionHandler handler) {
				if (handler != null) {
					TCPNetworkConnector.this.totalConnectionHandlers.remove(handler);
					LogManager.getLogger().warn("TCP-NET-CONNECTOR", "Total connection handlers are: "
							+ TCPNetworkConnector.this.totalConnectionHandlers.size());
					return true;
				}
				return false;
			}

			public void executeInAsync(Packet packet, NetworkConnectionHandler handler) {
				try {
					stack.addMDC((DiameterPacket) packet);
					stack.submitToWorker((DiameterStack.PacketProcess) new PacketProcessImpl(packet, handler, stack,
							ThreadContext.getContext()));
				} catch (Exception exp) {
					LogManager.getLogger().warn("TCP-NET-CONNECTOR", exp.getMessage());
					LogManager.getLogger().trace("TCP-NET-CONNECTOR", exp);
				} finally {
					stack.clearMDC();
				}
			}

			public String getNetworkAddress() {
				return TCPNetworkConnector.this.serverSocket.getInetAddress().getHostAddress();
			}

			public int getNetworkPort() {
				return TCPNetworkConnector.this.serverSocket.getLocalPort();
			}

			public void executeInSync(Packet packet, NetworkConnectionHandler handler) {
				stack.handleReceivedMessage(packet, handler);
			}

			public PeerConnectionData getPeerConnectionData(String ipAddress) {
				return (PeerConnectionData) stack.getStackContext().getPeerData(ipAddress);
			}

			public <T> ScheduledFuture<T> scheduleCallableSingleExecutionTask(
					CallableSingleExecutionAsyncTask<T> task) {
				return stack.getStackContext().scheduleCallableSingleExecutionTask(task);
			}

			public SecurityStandard getDefaultSecurityStandard() {
				return TCPNetworkConnector.this.geSecurityStandard();
			}

			public EliteSSLContextExt createEliteSSLContext() throws Exception {
				EliteSSLContextFactory eliteSSLContextFactory = stack.getStackContext().getEliteSSLContextFactory();
				return eliteSSLContextFactory.createSSLContext(TCPNetworkConnector.this.getDefalutSSLParameter());
			}
		};
	}

	public boolean start(ConnectionFactory connectionFactory) {
		this.boundSocketDetail = new SocketDetail(getNetworkAddress(), getNetworkPort());
		this.connectionFactory = connectionFactory;
		if (this.currentState == ConnectorState.NOT_STARTED || this.currentState == ConnectorState.STOPPED) {
			this.currentState = ConnectorState.STARTUP_IN_PROGRESS;
			InetAddress bindAddress = null;
			if (this.serverSocket == null || this.serverSocket.isClosed()) {
				try {
					bindAddress = InetAddress.getByName(getNetworkAddress());
					SocketAddress socketAddress = new InetSocketAddress(bindAddress, getNetworkPort());
					this.serverSocket = new ServerSocket();
					this.serverSocket.bind(socketAddress);
					this.serverSocket.setSoTimeout(2000);
					LogManager.getLogger().info("TCP-NET-CONNECTOR",
							"Server socket ip: " + this.serverSocket.getLocalSocketAddress());
				} catch (IOException e) {
					if (e instanceof UnknownHostException) {
						LogManager.getLogger().warn("TCP-NET-CONNECTOR", "Unknown host address: " + getNetworkAddress()
								+ ", Reason: " + e.getMessage() + ", service will be listening on " + "0.0.0.0");
					} else {
						LogManager.getLogger().warn("TCP-NET-CONNECTOR",
								"Problem while binding configured service on address: " + getNetworkAddress()
										+ ", Reason: " + e.getMessage() + ", service will be listening on "
										+ "0.0.0.0");
					}
					if (!bindServiceOnUniversalIp(getNetworkPort()))
						if (getNetworkPort() != 3868) {
							LogManager.getLogger().warn("TCP-NET-CONNECTOR",
									"Failed to start service on socket: 0.0.0.0:" + getNetworkPort() + ". Reason: "
											+ e.getMessage() + ", service will attempt to listen on socket: "
											+ "0.0.0.0");
							if (!bindServiceOnUniversalIp(3868)) {
								LogManager.getLogger().warn("TCP-NET-CONNECTOR",
										"Failed to start service on socket: 0.0.0.0:3868. Reason: " + e.getMessage());
								return false;
							}
						} else {
							LogManager.getLogger().warn("TCP-NET-CONNECTOR",
									"Failed to start service on socket: 0.0.0.0:3868. Reason: " + e.getMessage());
							return false;
						}
				}
				try {
					this.connectionLSNRThread = new Thread(new ConnectionListener());
					this.connectionLSNRThread.setName(getThreadIdentifier() + "-LIS-THR");
					this.connectionLSNRThread.setPriority(this.stack.getMainThreadPriority());
					this.connectionLSNRThread.start();
					this.boundSocketDetail = new SocketDetail(this.serverSocket.getInetAddress().getHostAddress(),
							this.serverSocket.getLocalPort());
				} catch (Exception exp) {
					this.remarks = ServiceRemarks.UNKNOWN_PROBLEM;
					this.currentState = ConnectorState.NOT_STARTED;
					LogManager.getLogger().error("TCP-NET-CONNECTOR",
							"Failed to start " + getThreadIdentifier() + ". Reason: " + exp.getMessage());
					LogManager.getLogger().trace("TCP-NET-CONNECTOR", exp);
					return false;
				}
			}
		}
		return true;
	}

	private boolean bindServiceOnUniversalIp(int port) {
		try {
			InetAddress bindAddress = InetAddress.getByName("0.0.0.0");
			SocketAddress socketAddress = new InetSocketAddress(bindAddress, port);
			this.serverSocket = new ServerSocket();
			this.serverSocket.bind(socketAddress);
			this.remarks = ServiceRemarks.STARTED_ON_UNIVERSAL_IP;
			this.serverSocket.setSoTimeout(2000);
			this.boundSocketDetail = new SocketDetail(this.serverSocket.getInetAddress().getHostAddress(),
					this.serverSocket.getLocalPort());
		} catch (Exception e) {
			this.remarks = ServiceRemarks.PROBLEM_BINDING_IP_PORT;
			this.currentState = ConnectorState.NOT_STARTED;
			LogManager.getLogger().error("TCP-NET-CONNECTOR",
					"Failed to start service on universal IP. Reason: " + e.getMessage());
			LogManager.getLogger().trace("TCP-NET-CONNECTOR", e);
			return false;
		}
		return true;
	}

	public class ConnectionListener implements Runnable {
		public void run() {
			TCPNetworkConnector.this.currentState = ConnectorState.RUNNING;
			while (!TCPNetworkConnector.this.stopRequested) {
				ConnectionHandler connectionHandler = null;
				Socket clientSocket = null;
				try {
					clientSocket = TCPNetworkConnector.this.serverSocket.accept();
					Connection connection = TCPNetworkConnector.this.connectionFactory.createConnection(clientSocket,
							ConnectionRole.Responder, TCPNetworkConnector.this.context);
					connectionHandler = new ConnectionHandler(connection, TCPNetworkConnector.this.connectionFactory,
							ConnectionRole.Responder, TCPNetworkConnector.this.context);
					EliteThreadFactory.EliteThread eliteThread = new EliteThreadFactory.EliteThread(connectionHandler,
							TCPNetworkConnector.this.getThreadIdentifier() + "-HL-"
									+ clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort(),
							"DIA-STACK");
					eliteThread.setPriority(TCPNetworkConnector.this.stack.getMainThreadPriority());
					eliteThread.start();
					TCPNetworkConnector.this.totalConnectionHandlers.add(connectionHandler);
					if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
						LogManager.getLogger().debug("TCP-NET-CONNECTOR", "Successfully Added new connection handler: "
								+ connectionHandler.getSourceIpAddress() + ":" + connectionHandler.getSourcePort());
						LogManager.getLogger().debug("TCP-NET-CONNECTOR", "Total connection handlers are: "
								+ TCPNetworkConnector.this.totalConnectionHandlers.size());
					}
				} catch (SocketTimeoutException socketExp) {
					if (clientSocket != null)
						try {
							clientSocket.close();
						} catch (IOException ex) {
							LogManager.getLogger().trace("TCP-NET-CONNECTOR", ex);
						}
					if (TCPNetworkConnector.this.stopRequested && LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn("TCP-NET-CONNECTOR",
								"Stop request for TCPNetworkConnector, stopping accept new connections request.");
					LogManager.ignoreTrace(socketExp);
				} catch (SocketException socketExp) {
					if (clientSocket != null)
						try {
							clientSocket.close();
						} catch (IOException ex) {
							LogManager.getLogger().trace("TCP-NET-CONNECTOR", ex);
						}
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn("TCP-NET-CONNECTOR", socketExp.getMessage());
				} catch (Exception e) {
					if (clientSocket != null)
						try {
							clientSocket.close();
						} catch (IOException ex) {
							LogManager.getLogger().trace("TCP-NET-CONNECTOR", ex);
						}
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn("TCP-NET-CONNECTOR", e.getMessage());
					LogManager.getLogger().trace("TCP-NET-CONNECTOR", e);
				}
			}
			TCPNetworkConnector.this.currentState = ConnectorState.STOPPED;
		}
	}

	public void openConnection(IPeerListener peerListener) {
		ConnectionSender connectionSender = new ConnectionSender(peerListener);
		EliteThreadFactory.EliteThread eliteThread = new EliteThreadFactory.EliteThread(connectionSender,
				getThreadIdentifier() + "-SEND-THR", "DIA-STACK");
		eliteThread.setPriority(this.stack.getMainThreadPriority());
		eliteThread.start();
	}

	public class ConnectionSender implements Runnable {
		private IPeerListener peerListener;

		public ConnectionSender(IPeerListener peerListener) {
			this.peerListener = peerListener;
		}

		public void run() {
			ConnectionEvents connectionEvent = ConnectionEvents.CONNECTION_FAILURE;
			DiameterPeerEvent peerEvent = DiameterPeerEvent.IRcvConnNack;
			ConnectionHandler connectionHandler = null;
			Socket clientSocket = null;
			try {
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					String logMessageToAppend = (this.peerListener.getLocalIpAddress() == null) ? ""
							: (", with local address: " + this.peerListener.getLocalIpAddress() + ":"
									+ this.peerListener.getLocalPort());
					LogManager.getLogger().info("TCP-NET-CONNECTOR",
							"Attempting connection to " + this.peerListener.getRemoteInetAddress() + ":"
									+ this.peerListener.getCommunicationPort() + logMessageToAppend);
				}
				InetAddress localInetAddress = null;
				try {
					localInetAddress = resolveLocalAddress();
				} catch (UnknownHostException e) {
					if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error("TCP-NET-CONNECTOR",
								"Invalid local address: " + e.getMessage() + " for connection: "
										+ this.peerListener.getRemoteInetAddress() + ":"
										+ this.peerListener.getCommunicationPort());
					LogManager.getLogger().trace("TCP-NET-CONNECTOR", e);
					if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
						LogManager.getLogger().info("TCP-NET-CONNECTOR",
								"ConnectionEvent: " + ConnectionEvents.CONNECTION_FAILURE + " with Peer Event: "
										+ peerEvent + " for Peer: " + this.peerListener.getPeerName());
					this.peerListener.handleEvent((IEventEnum) peerEvent, ConnectionEvents.CONNECTION_FAILURE);
					return;
				}
				clientSocket = SocketFactory.getDefault().createSocket();
				clientSocket.bind(new InetSocketAddress(localInetAddress, this.peerListener.getLocalPort()));
				clientSocket.connect(new InetSocketAddress(this.peerListener.getRemoteInetAddress(),
						this.peerListener.getCommunicationPort()), 3000);
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info("TCP-NET-CONNECTOR",
							"Successfully connected to " + this.peerListener.getRemoteInetAddress() + ":"
									+ this.peerListener.getCommunicationPort() + ", with local Address: "
									+ clientSocket.getLocalAddress() + ":" + clientSocket.getLocalPort());
				Connection connection = TCPNetworkConnector.this.connectionFactory.createConnection(clientSocket,
						ConnectionRole.Initiator, TCPNetworkConnector.this.context);
				connectionHandler = new ConnectionHandler(connection, TCPNetworkConnector.this.connectionFactory,
						ConnectionRole.Initiator, TCPNetworkConnector.this.context);
				EliteThreadFactory.EliteThread eliteThread = new EliteThreadFactory.EliteThread(connectionHandler,
						TCPNetworkConnector.this.getThreadIdentifier() + "-HL-"
								+ clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort(),
						"DIA-STACK");
				eliteThread.setPriority(TCPNetworkConnector.this.stack.getMainThreadPriority());
				eliteThread.start();
				this.peerListener.setConnectionListener(connectionHandler);
				TCPNetworkConnector.this.totalConnectionHandlers.add(connectionHandler);
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug("TCP-NET-CONNECTOR",
							"Successfully Added new connection handler: " + connectionHandler.getSourceIpAddress() + ":"
									+ connectionHandler.getSourcePort() + " Local Address: "
									+ clientSocket.getLocalAddress() + ":" + clientSocket.getLocalPort());
					LogManager.getLogger().debug("TCP-NET-CONNECTOR", "Total connection handlers are: "
							+ TCPNetworkConnector.this.totalConnectionHandlers.size());
				}
				connectionEvent = ConnectionEvents.CONNECTION_CREATED;
				peerEvent = DiameterPeerEvent.IRcvConnAck;
			} catch (IOException e) {
				if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error("TCP-NET-CONNECTOR",
							e.getMessage() + " for connection: " + this.peerListener.getRemoteInetAddress() + ":"
									+ this.peerListener.getCommunicationPort());
				LogManager.getLogger().trace("TCP-NET-CONNECTOR", e);
				try {
					if (clientSocket != null && !clientSocket.isClosed()) {
						clientSocket.close();
						clientSocket = null;
					}
				} catch (Exception exc) {
					LogManager.ignoreTrace(exc);
				}
			} catch (Exception ex) {
				try {
					if (clientSocket != null) {
						clientSocket.close();
						clientSocket = null;
					}
				} catch (IOException e) {
					LogManager.getLogger().trace("TCP-NET-CONNECTOR", e);
				}
				LogManager.getLogger().trace("TCP-NET-CONNECTOR", ex);
			} finally {
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info("TCP-NET-CONNECTOR", "ConnectionEvent: " + connectionEvent
							+ " with Peer Event: " + peerEvent + " for Peer: " + this.peerListener.getPeerName());
				this.peerListener.handleEvent((IEventEnum) peerEvent, connectionEvent);
			}
		}

		private InetAddress resolveLocalAddress() throws UnknownHostException {
			InetAddress localInetAddress = (this.peerListener.getLocalIpAddress() == null) ? null
					: InetAddress.getByName(this.peerListener.getLocalIpAddress());
			return localInetAddress;
		}
	}

	public String getNetworkAddress() {
		return "127.0.0.1";
	}

	public SocketDetail getBondSocketDetail() {
		return this.boundSocketDetail;
	}

	public ServiceRemarks getRemarks() {
		return this.remarks;
	}

	public int getNetworkPort() {
		return 3868;
	}

	public boolean stop() {
		if (this.stopRequested == true) {
			LogManager.getLogger().debug("TCP-NET-CONNECTOR", "Shutdown in progress");
			return true;
		}
		this.stopRequested = true;
		try {
			this.serverSocket.close();
		} catch (Exception ex) {
			LogManager.getLogger().trace("TCP-NET-CONNECTOR", ex);
		}
		ConnectionHandler[] connectionHandlers = this.totalConnectionHandlers
				.<ConnectionHandler>toArray(new ConnectionHandler[this.totalConnectionHandlers.size()]);
		for (ConnectionHandler connHandler : connectionHandlers) {
			try {
				connHandler.closeConnection(ConnectionEvents.SHUTDOWN);
			} catch (Exception ex) {
				LogManager.getLogger().trace("TCP-NET-CONNECTOR", ex);
			}
		}
		return true;
	}

	public TransportProtocols getTransportProtocol() {
		return TransportProtocols.TCP;
	}

	protected abstract int getSocketReceiveBufferSize();

	protected abstract int getSocketSendBufferSize();

	protected abstract String getThreadIdentifier();
}
