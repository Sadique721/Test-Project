package com.diameter.commons;

import java.net.Socket;

public class ConnectionFactoryImpl implements ConnectionFactory {
  private PeerProvider peerProvider;
  
  public ConnectionFactoryImpl(PeerProvider peerProvider) {
    this.peerProvider = peerProvider;
  }
  
  public final Connection createConnection(Socket clientSocket, ConnectionRole connectionRole, ConnectorContext context) throws Exception {
    String peerAddress = clientSocket.getInetAddress().getHostAddress();
    DiameterPeer diameterPeer = this.peerProvider.getPeer(peerAddress);
    if (diameterPeer == null)
      diameterPeer = this.peerProvider.getPeer(clientSocket.getInetAddress().getHostName()); 
    TCPConnection connection = null;
    if (diameterPeer != null) {
      PeerData peerData = diameterPeer.getPeerData();
      connection = new TCPConnection(clientSocket, context, (PeerConnectionData)peerData);
    } else {
      connection = new TCPConnection(clientSocket, context, null);
    } 
    connection.init();
    if (diameterPeer != null) {
      if (diameterPeer.getPeerData().getSecurityStandard() == SecurityStandard.RFC_6733)
        secureConnectionWithTLS((Connection)connection, connectionRole, context, diameterPeer.createEliteSSLContext()); 
    } else if (context.getDefaultSecurityStandard() == SecurityStandard.RFC_6733) {
      secureConnectionWithTLS((Connection)connection, connectionRole, context, context.createEliteSSLContext());
    } 
    return (Connection)connection;
  }
  
  public final Connection secureConnectionWithTLS(Connection connection, ConnectionRole connectionRole, ConnectorContext context, EliteSSLContextExt eliteSSLContext) throws HandShakeFailException {
    TLSConnection tlsConnection;
    if (connectionRole == ConnectionRole.Responder) {
      tlsConnection = new TLSConnection(context, (TCPConnection)connection, TLSConnectionMode.SERVER, eliteSSLContext);
    } else {
      tlsConnection = new TLSConnection(context, (TCPConnection)connection, TLSConnectionMode.CLIENT, eliteSSLContext);
    } 
    tlsConnection.startHandshake();
    return (Connection)tlsConnection;
  }
}
