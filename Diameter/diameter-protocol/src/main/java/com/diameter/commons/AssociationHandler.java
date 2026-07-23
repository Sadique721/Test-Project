package com.diameter.commons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.sun.nio.sctp.Association; //NOSONAR
import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;

public class AssociationHandler implements NetworkConnectionHandler, Runnable {

	private static final String MODULE = "ASSOCIATION-HANDLER";

	private final SctpChannel sctpChannel;
	private final ConnectionRole connectionRole;
	private final ConnectorContext context;
	private final ByteBuffer receiveBuffer;
	private final int maxOutStreams;
	private final ReentrantLock connectionLock;

	private List<NetworkConnectionEventListener> networkConnectionEventListeners;

	private InetSocketAddress address;
	private CircularCounter<Integer> lastSentStream;
	private InetSocketAddress serverSocketAddress;
	private String hostName;
	private Association association;


	public AssociationHandler(SctpChannel sctpChannel, Association association, InetSocketAddress address, ConnectionRole connectionRole, 
			ConnectorContext context, InetSocketAddress serverSocketAddress) {
		this.sctpChannel = sctpChannel;
		this.association = association;
		this.address = address;
		this.connectionRole = connectionRole;
		this.context = context;
		this.serverSocketAddress = serverSocketAddress;
		this.receiveBuffer = ByteBuffer.allocate(DiameterPacket.DIA_PCKT_MAX_EXPECTED_LENGTH);
		this.maxOutStreams = association.maxOutboundStreams();
		this.networkConnectionEventListeners = new ArrayList<>();
		this.connectionLock = new ReentrantLock();
		this.lastSentStream = new ThreadSafeCounter<>(new IntegerCounter(maxOutStreams));
	}

	@Override
	public void send(Packet packet) throws IOException {
		if ( ! sctpChannel.isOpen()) {
			throw new IOException("Connection is closed");
		}
		DiameterPacket diameterPacket = (DiameterPacket) packet;
		int stream = diameterPacket.getRcvdOnStream();
		if(stream < 0) {
			stream = lastSentStream.next();
		}
		MessageInfo info = MessageInfo.createOutgoing(association, address, stream);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			diameterPacket.writeTo(outputStream);
			sctpChannel.send(ByteBuffer.wrap(outputStream.toByteArray()), info);
		} catch (IOException e) {
			if ( ! sctpChannel.isOpen()) {
				closeConnection(ConnectionEvents.DISCONNECTED);
			} else {
				LogManager.getLogger().warn(MODULE, "Unable to send packet to: " + this.address + ", Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}

	@Override
	public void run() {
		ConnectionEvents event = ConnectionEvents.DISCONNECTED;
		try (Selector selector = Selector.open()) {
			doConnectionNegotiation();
			sctpChannel.configureBlocking(false);
			sctpChannel.register(selector, SelectionKey.OP_READ);
			while(sctpChannel.isOpen()) {
				selector.select();
				Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey key = selectedKeys.next();
					if(key.isReadable()) {
						receiveBuffer.clear();
						MessageInfo info = sctpChannel.receive(receiveBuffer, null, null);
						receiveBuffer.flip();
						byte[] packetBytes = new byte[receiveBuffer.remaining()];
						receiveBuffer.get(packetBytes);
						processRecivedPacket(info, packetBytes);
					}
					selectedKeys.remove();
				}
			}
		} catch (IOException e) {
			LogManager.getLogger().error(MODULE, "Closing Connection due to I/O Error handling client " + 
					address + ", Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			closeConnection(event);
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Closing Connection due to General error handling client " + 
					address + ", Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
			closeConnection(event);
		}
	}

	/**
	 * @param info
	 * @param packetBytes
	 */
	private void processRecivedPacket(MessageInfo info, byte[] packetBytes) {
		try {
			DiameterPacket diameterPacket = createDiameterPacket(info, packetBytes);
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Packet obtained from stream, assigning task to process packet");
			}
			context.executeInAsync(diameterPacket, this);
		} catch (MalformedPacketException e) {
			LogManager.getLogger().warn(MODULE, " Malformed Diameter Packet recieved from Peer: " + 
					address + ", Reason: " + e.getMessage());
			LogManager.ignoreTrace(e);
		}
	}

	private DiameterPacket createDiameterPacket(MessageInfo info, byte[] packetBytes) throws MalformedPacketException {
		ByteArrayInputStream stream = new ByteArrayInputStream(packetBytes);
		byte[] header = new byte[DiameterPacket.DEFAULT_DIAMETER_PACKET_LENGTH];
		stream.read(header, 0, DiameterPacket.DEFAULT_DIAMETER_PACKET_LENGTH);
		DiameterPacket diameterPacket = DiameterPacket.createPacket(header);
		try {
			diameterPacket.parsePacketAVPBytes(stream);
		} catch (IOException e) {
			throw new MalformedPacketException("Exception occured while parsing packet with HbH-ID=" + diameterPacket.getHop_by_hopIdentifier() + 
					" and EtE-ID=" + diameterPacket.getEnd_to_endIdentifier(), e);
		}
		diameterPacket.setRcvdOnStream(info.streamNumber());
		return diameterPacket;
	}

	private void doConnectionNegotiation() throws IOException, MalformedPacketException {
		MessageInfo info = sctpChannel.receive(receiveBuffer, null, null);
		receiveBuffer.flip();
		byte[] packetBytes = new byte[receiveBuffer.remaining()];
		receiveBuffer.get(packetBytes);
		receiveBuffer.clear();

		DiameterPacket packet = createDiameterPacket(info, packetBytes);
		if (LogManager.getLogger().isDebugLogLevel())
			LogManager.getLogger().debug(MODULE, "Packet obtained from stream, assigning task to process packet");
		context.executeInSync(packet, this);
	}

	private void notifyNetworkEventListener(ConnectionEvents event, List<NetworkConnectionEventListener> connectionEventListeners) {
		switch (event) {
		case CONNECTION_ESTABLISHED:
			for(NetworkConnectionEventListener connectionEventListener : connectionEventListeners) {
				connectionEventListener.connectionEstablished();
			}
			break;
		case HANDSHAKE_FAIL:
		case CONNECTION_BREAK:
		case DISCONNECTED:
			for(NetworkConnectionEventListener connectionEventListener : connectionEventListeners) {
				connectionEventListener.connectionBreak(this, event);
			}
			break;
		case CONNECTION_DPR:
			Map<PeerDataCode,String> eventParam = new EnumMap<>(PeerDataCode.class);
			eventParam.put(PeerDataCode.DISCONNECT_REASON, CommonConstants.MALFORMED_PACKET);
			for(NetworkConnectionEventListener connectionEventListener : connectionEventListeners) {
				connectionEventListener.connectionDPR(eventParam, event);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean isConnected() {
		return sctpChannel.isOpen();
	}

	@Override
	public void addNetworkConnectionEventListener(NetworkConnectionEventListener networkConnectionEventListener) {
		networkConnectionEventListeners.add(networkConnectionEventListener);
	}

	@Override
	public void closeConnection(ConnectionEvents event) {
		try {
			if (connectionLock.tryLock(10, TimeUnit.MILLISECONDS) == false) {
				return;
			}

			if (sctpChannel.isOpen() == false) {
				if (LogManager.getLogger().isDebugLogLevel())
					LogManager.getLogger().debug(MODULE, "Close connection invoked where connection is already closed.");
				return;
			}

			/*
			 * Disconnected event is generated for Network Faliure,
			 * hence for this case closing Connection for Clean Up.
			 * 
			 * In all other cases, 
			 * We close connection after generating Notification Event 
			 * (viz. generally used for DPR Sending)
			 * 
			 * Benefit: Traffic will be stopped sending TO Peer 
			 * as soon as network down is detected.
			 * 
			 */
			if(ConnectionEvents.DISCONNECTED == event) {
				sctpChannel.close();
			}

			if (context.removeConnectionHandler(this)) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Successfully removed Connection Handler(" + 
							address  + ") from Total Connection Listeners");
				}
			} else {
				if (LogManager.getLogger().isWarnLogLevel()) {
					LogManager.getLogger().warn(MODULE, "Failed to remove Connection Handler(" + 
							address + ") from Total Connection Listeners");
				}
			}

			if(networkConnectionEventListeners != null && !networkConnectionEventListeners.isEmpty()){
				notifyNetworkEventListener(event, networkConnectionEventListeners);
			}

			sctpChannel.close();

		} catch (InterruptedException e) {
			LogManager.ignoreTrace(e);
			Thread.currentThread().interrupt();
			return;
		} catch (IOException e) {
			LogManager.getLogger().warn(MODULE, "Unable to close association for " + address + " Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}finally {
			try{
				connectionLock.unlock();
			} catch (Exception e) {
				LogManager.ignoreTrace(e);
			} 
		}
	}

	@Override
	public boolean isResponder() {
		return connectionRole == ConnectionRole.Responder;
	}

	@Override
	public String getSourceIpAddress() {
		return address.getAddress().getHostAddress();
	}

	@Override
	public int getSourcePort() {
		return address.getPort();
	}

	@Override
	public String getHostName() {
		return hostName;
	}

	@Override
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	@Override
	public String getLocalAddress() {
		return serverSocketAddress.getAddress().getHostAddress();
	}

	@Override
	public void secureConnection(PeerConnectionData peerData, EliteSSLContextExt eliteSSLContext)
			throws HandShakeFailException {
		//this will be implemented with ssl support in SCTP
	}

	@Override
	public int getLocalPort() {
		return serverSocketAddress.getPort();
	}

	@Override
	public SecurityProtocol getSecurityProtocol() {
		return null;
	}

	@Override
	public void terminateConnection() {
		if (context.removeConnectionHandler(this)) {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Successfully removed Connection Handler(" + 
						address + ") from Total Connection Listeners");
			}
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Failed to remove Connection Handler(" + 
						address + ") from Total Connection Listeners");
			}
		}
		try {
			sctpChannel.close();
		} catch (IOException e) {
			LogManager.getLogger().warn(MODULE, "Unable to close association for " + address + " Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	@Override
	public InetAddress getSourceInetAddress() {
		return address.getAddress();
	}


}