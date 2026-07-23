package com.diameter.commons;

import java.net.Socket;

public interface ConnectionFactory {
  Connection createConnection(Socket paramSocket, ConnectionRole paramConnectionRole, ConnectorContext paramConnectorContext) throws Exception;
  
  Connection secureConnectionWithTLS(Connection paramConnection, ConnectionRole paramConnectionRole, ConnectorContext paramConnectorContext, EliteSSLContextExt paramEliteSSLContextExt) throws HandShakeFailException;
}