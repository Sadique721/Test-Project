package com.diameter.serviceImpl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.diameter.commons.ApplicationEnum;
import com.diameter.commons.DiameterPeer;
import com.diameter.commons.DiameterPeerState;
import com.diameter.commons.DiameterUtils;
import com.diameter.commons.Session;
import com.diameter.commons.SessionState;
import com.diameter.commons.SessionsFactory;
import com.diameter.dto.DiameterPeerResponse;
import com.diameter.dto.DiameterSessionResponse;
import com.diameter.model.DiameterLiveSession;
import com.diameter.repository.DiameterLiveSessionRepository;
import com.diameter.service.DiameterMonitoringService;
import com.diameter.stack.DiameterStack;
import com.diameter.util.GenericDiameterProcessor;

@Service
public class DiameterMonitoringServiceImpl implements DiameterMonitoringService {

	private static final Logger logger = LoggerFactory.getLogger(DiameterMonitoringServiceImpl.class);

	@Autowired
    private DiameterStack diameterStack;

    @Autowired
    private DiameterLiveSessionRepository
            liveSessionRepository;

//    @Autowired
//    private GenericDiameterProcessor genericDiameterProcessor;
//
//    @Autowired
//    private MappingHeaderServiceImpl mappingHeaderServiceImpl;

    /**
     * Fetch live session by session id
     */
    @Override
    public DiameterLiveSession getSessionById(
            String sessionId) {

        return liveSessionRepository
                .getSessionById(sessionId);
    }


    /**
     * Fetch all live sessions
     */
    public List<DiameterSessionResponse> getLiveSessions() {
        Map<String, DiameterSessionResponse> sessionMap = new LinkedHashMap<>();

        for (ApplicationEnum app : diameterStack.getStackContext().getApplicationsIdentifiersList()) {

            try {
                SessionsFactory factory =diameterStack.getSessionFactoryManager().getSessionFactory(app.getApplicationId());

                Collection<Session> sessions = factory.getAllSessions();
                if (sessions == null) {
                    continue;
                }

                Timestamp createDate = null;
                Timestamp lastAccessDate = null;
                
                for (Session session : sessions) {
                	SessionState state = session.getSessionState();
                	
                	createDate =
                            new Timestamp(
                            		session.getCreationTime());

                    lastAccessDate =
                            new Timestamp(
                            		session.getLastAccessedTime());

                    sessionMap.put(session.getSessionId(),new DiameterSessionResponse(
                            session.getSessionId(),
                            app.getApplicationId(),
                            state != null ? state.name() : "UNKNOWN",
                        		createDate,
                                lastAccessDate
                    ));
                }

            } catch (Exception ex) {
                logger.error("Failed to read live sessions for application", ex);
            }
        }
        return new ArrayList<>(sessionMap.values());
    }

    /**
     * Fetch peer status
     */
    public List<DiameterPeerResponse> getPeerStatus() {
        List<DiameterPeerResponse> response = new ArrayList<>();

        Collection<DiameterPeer> peers = diameterStack.getDiameterPeersTable().getPeerList();
        for (DiameterPeer peer : peers) {
        	
        	DiameterPeerState diameterPeerState=DiameterPeerState.fromStateOrdinal(peer.getPeerState());

            response.add(
                    new DiameterPeerResponse(
                            peer.getPeerName(),
                            peer.getHostIdentity(),
                            peer.getRemoteInetAddress() != null
                                    ? peer.getRemoteInetAddress().getHostAddress()
                                    : null,
                            peer.getLocalPort(),
                            diameterPeerState != null
                                    ? diameterPeerState.name()
                                    : "UNKNOWN",
                            peer.isAlive()
                    )
            );
        }
        return response;
    }
}
