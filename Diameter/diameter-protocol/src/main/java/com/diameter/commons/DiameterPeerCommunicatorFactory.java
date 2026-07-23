/**
 * 
 */
package com.diameter.commons;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DiameterPeerCommunicatorFactory {
  private static final String MODULE = "DIA-PEER-COMM-FACTORY";
  
  private final PeerProvider peerProvider;
  
  private final CommunicatorStore peerCommunicatorStore;
  
  private final IDiameterStackContext stackContext;
  
  public DiameterPeerCommunicatorFactory(IDiameterStackContext stackContext, PeerProvider peerProvider) {
    this.stackContext = stackContext;
    this.peerProvider = peerProvider;
    this.peerCommunicatorStore = new CommunicatorStore();
  }
  
  @Nullable
  public DiameterPeerCommunicator createInstance(String peerNameOrHostIdentity) {
    DiameterPeer diameterPeer = this.peerProvider.getPeer(peerNameOrHostIdentity);
    if (diameterPeer == null)
      diameterPeer = this.peerProvider.getPeerByName(peerNameOrHostIdentity); 
    if (diameterPeer == null)
      return null; 
    return getOrCreateInstance((IPeerListener)diameterPeer);
  }
  
  private DiameterPeerCommunicator getOrCreateInstance(IPeerListener peerListener) {
    DiameterPeerCommunicator diameterPeerCommunicator = this.peerCommunicatorStore.get(peerListener);
    if (diameterPeerCommunicator != null)
      return diameterPeerCommunicator; 
    synchronized (this) {
      diameterPeerCommunicator = this.peerCommunicatorStore.get(peerListener);
      if (diameterPeerCommunicator != null)
        return diameterPeerCommunicator; 
      diameterPeerCommunicator = create(peerListener);
      this.peerCommunicatorStore.store(peerListener, diameterPeerCommunicator);
    } 
    return diameterPeerCommunicator;
  }
  
  private DiameterPeerCommunicator create(IPeerListener peerListener) {
    DiameterPeerCommunicatorImpl diameterPeerCommunicator = new DiameterPeerCommunicatorImpl(peerListener, this.stackContext);
    diameterPeerCommunicator.init();
    if (LogManager.getLogger().isDebugLogLevel())
      LogManager.getLogger().debug("DIA-PEER-COMM-FACTORY", "Created new Peer communicator for peer: " + peerListener.getPeerName()); 
    String failoverPeerName = peerListener.getPeerData().getSecondaryPeerName();
    if (failoverPeerName == null) {
      if (LogManager.getLogger().isInfoLogLevel())
        LogManager.getLogger().info("DIA-PEER-COMM-FACTORY", "No failover peer configured for peer: " + peerListener.getHostIdentity()); 
      return diameterPeerCommunicator;
    } 
    DiameterPeer diameterPeer = this.peerProvider.getPeerByName(failoverPeerName);
    if (diameterPeer == null) {
      LogManager.getLogger().warn("DIA-PEER-COMM-FACTORY", "Secondary peer: " + failoverPeerName + " attached with peer: " + peerListener.getHostIdentity() + " not found. High availability will not work.");
      return diameterPeerCommunicator;
    } 
    diameterPeerCommunicator.setFailoverPeerListener((IPeerListener)diameterPeer);
    return diameterPeerCommunicator;
  }
  
  private class DiameterPeerCommunicatorImpl extends ESCommunicatorImpl implements DiameterPeerCommunicator, DiameterPeerStatusListener {
    private final IPeerListener peerListener;
    
    private IDiameterStackContext stackContext;
    
    private IPeerListener failoverPeerListener;
    
    public DiameterPeerCommunicatorImpl(IPeerListener peerListener, IDiameterStackContext stackContext) {
      super(stackContext.getTaskScheduler());
      this.peerListener = peerListener;
      this.stackContext = stackContext;
    }
    
    public void setFailoverPeerListener(IPeerListener failoverPeerListener) {
      this.failoverPeerListener = failoverPeerListener;
    }
    
    public void init() {
      DiameterPeerState diameterPeerState = this.peerListener.registerStatusListener(this);
      if (diameterPeerState == DiameterPeerState.R_Open || diameterPeerState == DiameterPeerState.I_Open) {
        markAlive();
      } else {
        markClosed();
      } 
    }
    
    public void sendClientInitiatedRequest(DiameterSession session, DiameterRequest diameterRequest, ResponseListener listener) throws CommunicationException {
      if (!isAlive())
        throw new CommunicationException("Unable to send diameter request. Reason: " + 
            getName() + " not live"); 
      send(this.peerListener, session, diameterRequest, listener);
    }
    
    public void sendServerInitiatedRequest(DiameterSession session, DiameterRequest diameterRequest, ResponseListener listener) throws CommunicationException {
      if (this.failoverPeerListener == null) {
        if (LogManager.getLogger().isDebugLogLevel())
          LogManager.getLogger().debug("ES-COMM", "No failover communicator attached with peer: " + this.peerListener.getHostIdentity() + ", server initiated request failover will not occur."); 
        try {
          send(this.peerListener, session, diameterRequest, listener);
        } catch (CommunicationException ex) {
          throw new CommunicationException(ex);
        } 
      } else {
        try {
          send(this.peerListener, session, diameterRequest, new ServerInitiatedRequestFailoverListener(diameterRequest, listener));
        } catch (CommunicationException ex) {
          if (LogManager.getLogger().isInfoLogLevel())
            LogManager.getLogger().info("ES-COMM", "Failed to communicate with peer: " + this.peerListener.getHostIdentity() + " Reason: " + ex
                .getMessage() + ", trying failover communicator: " + this.failoverPeerListener.getHostIdentity()); 
          send(this.failoverPeerListener, session, diameterRequest, listener);
        } 
      } 
    }
    
    private void send(IPeerListener peerListener, DiameterSession session, DiameterRequest diameterRequest, @Nonnull ResponseListener listener) throws CommunicationException {
      try {
        peerListener.sendDiameterRequest(diameterRequest, listener);
        this.stackContext.updateRealmOutputStatistics((DiameterPacket)diameterRequest, peerListener.getRealm(), diameterRequest.getRoutingAction());
      } catch (UnhandledTransitionException ex) {
        diameterRequest.addFailedPeer(peerListener.getHostIdentity());
        throw new CommunicationException(ex);
      } 
    }
    
    public void sendAnswer(DiameterRequest diameterRequest, DiameterAnswer diameterAnswer) throws CommunicationException {
      if (!isAlive())
        throw new CommunicationException("Unable to send diameter answer. Reason: " + 
            getName() + " not live"); 
      try {
        this.peerListener.sendDiameterAnswer(diameterAnswer);
        this.stackContext.updateRealmOutputStatistics((DiameterPacket)diameterAnswer, this.peerListener.getRealm(), diameterRequest.getRoutingAction());
      } catch (UnhandledTransitionException ex) {
        throw new CommunicationException(ex);
      } 
    }
    
    public String getHostIdentity() {
      return this.peerListener.getHostIdentity();
    }
    
    protected int getStatusCheckDuration() {
      return 0;
    }
    
    public void scan() {}
    
    public String getName() {
      return this.peerListener.getPeerName();
    }
    
    public String getTypeName() {
      return "PEER-COMM";
    }
    
    public void markOpen() {
      markAlive();
    }
    
    public void markClosed() {
      markDead();
    }
    
    private class ServerInitiatedRequestFailoverListener implements ResponseListener {
      private DiameterRequest diameterRequest;
      
      private ResponseListener listener;
      
      public ServerInitiatedRequestFailoverListener(DiameterRequest diameterRequest, ResponseListener listener) {
        this.diameterRequest = diameterRequest;
        this.listener = listener;
      }
      
      public void requestTimedout(String hostIdentity, DiameterSession session) {
        this.diameterRequest.addFailedPeer(hostIdentity);
        if (LogManager.getLogger().isInfoLogLevel())
          LogManager.getLogger().info("ES-COMM", "Request timedout, trying failover communicator: " + DiameterPeerCommunicatorFactory.DiameterPeerCommunicatorImpl.this.failoverPeerListener.getHostIdentity()); 
        try {
          DiameterPeerCommunicatorFactory.DiameterPeerCommunicatorImpl.this.send(DiameterPeerCommunicatorFactory.DiameterPeerCommunicatorImpl.this.failoverPeerListener, session, this.diameterRequest, this.listener);
        } catch (CommunicationException e) {
          if (LogManager.getLogger().isInfoLogLevel())
            LogManager.getLogger().info("ES-COMM", "Unable to send request to failover communicator: " + DiameterPeerCommunicatorFactory.DiameterPeerCommunicatorImpl.this.failoverPeerListener.getHostIdentity() + ", Reason: " + e
                .getMessage()); 
          LogManager.ignoreTrace((Exception)e);
          this.listener.requestTimedout(hostIdentity, session);
        } 
      }
      
      public void responseReceived(DiameterAnswer diameterAnswer, String hostIdentity, DiameterSession session) {
        ResponseListener.RetryableResultCode retryableResultCode = new ResponseListener.RetryableResultCode(diameterAnswer);
        if (retryableResultCode.isRetryable()) {
          this.diameterRequest.addFailedPeer(hostIdentity);
          if (LogManager.getLogger().isInfoLogLevel())
            LogManager.getLogger().info("ES-COMM", "Retryable result code: " + retryableResultCode.getResultCode() + " received, trying failover communicator: " + DiameterPeerCommunicatorFactory.DiameterPeerCommunicatorImpl.this
                .failoverPeerListener.getHostIdentity()); 
          try {
            DiameterPeerCommunicatorFactory.DiameterPeerCommunicatorImpl.this.send(DiameterPeerCommunicatorFactory.DiameterPeerCommunicatorImpl.this.failoverPeerListener, session, this.diameterRequest, this.listener);
          } catch (CommunicationException e) {
            if (LogManager.getLogger().isInfoLogLevel())
              LogManager.getLogger().info("ES-COMM", "Unable to send request to failover communicator: " + DiameterPeerCommunicatorFactory.DiameterPeerCommunicatorImpl.this.failoverPeerListener.getHostIdentity() + ", Reason: " + e
                  .getMessage()); 
            LogManager.ignoreTrace((Exception)e);
            this.listener.responseReceived(diameterAnswer, hostIdentity, session);
          } 
        } else {
          this.listener.responseReceived(diameterAnswer, hostIdentity, session);
        } 
      }
    }
  }
  
  private static class CommunicatorStore {
    private final Map<IPeerListener, DiameterPeerCommunicator> diameterPeerCommunicators;
    
    private final Lock readLock;
    
    private final Lock writeLock;
    
    public CommunicatorStore() {
      ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
      this.readLock = lock.readLock();
      this.writeLock = lock.writeLock();
      this.diameterPeerCommunicators = new IdentityHashMap<>();
    }
    
    private DiameterPeerCommunicator get(IPeerListener parameter) {
      this.readLock.lock();
      try {
        return this.diameterPeerCommunicators.get(parameter);
      } finally {
        this.readLock.unlock();
      } 
    }
    
    private void store(IPeerListener peer, DiameterPeerCommunicator communicator) {
      this.writeLock.lock();
      try {
        this.diameterPeerCommunicators.put(peer, communicator);
      } finally {
        this.writeLock.unlock();
      } 
    }
  }
}
