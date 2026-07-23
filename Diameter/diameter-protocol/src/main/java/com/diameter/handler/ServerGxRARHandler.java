package com.diameter.handler;

import com.diameter.commons.Application;
import com.diameter.commons.ApplicationEnum;
import com.diameter.commons.ApplicationListener;
import com.diameter.commons.CommunicationException;
import com.diameter.commons.DiameterAnswer;
import com.diameter.commons.DiameterDictionary;
import com.diameter.commons.DiameterRequest;
import com.diameter.commons.IDiameterAVP;
import com.diameter.commons.IStackContext;
import com.diameter.commons.LogManager;
import com.diameter.commons.Session;
import com.diameter.commons.SessionReleaseIndiactor;

/**
 * Handles RAR (Re-Auth-Request) on Gx Interface from PCRF.
 * Typically used to update policy rules in an active session.
 */
public class ServerGxRARHandler extends ApplicationListener {

    private static final org.slf4j.Logger METHOD_LOG = org.slf4j.LoggerFactory.getLogger(ServerGxRARHandler.class);

    private IStackContext stackContext;

    public ServerGxRARHandler(IStackContext stackContext, ApplicationEnum[] applicationEnums) {
        super(stackContext, applicationEnums);
        this.stackContext = stackContext;
    }

    @Override
    public String getApplicationIdentifier() {
        return Application.TGPP_GX_29_212_18.name();
    }

    @Override
    protected void processApplicationRequest(Session session, DiameterRequest diameterRequest) {
        long __mStart = System.currentTimeMillis();
        if (METHOD_LOG.isDebugEnabled()) {
            METHOD_LOG.debug(">> ENTRY processApplicationRequest sessionId={}", (session != null ? session.getSessionId() : "null"));
        }
        try {
        LogManager.getLogger().info("ServerGxRARHandler", "Received RAR (Gx) from: " + diameterRequest.getRequestingHost());

        IDiameterAVP sessionIdAvp = diameterRequest.getAVP("0:263");
        String sessionId = sessionIdAvp != null ? sessionIdAvp.getStringValue() : "UNKNOWN";
        LogManager.getLogger().info("ServerGxRARHandler", "Session-Id: " + sessionId);

        IDiameterAVP reAuthTypeAvp = diameterRequest.getAVP("0:285");
        String reAuthType = reAuthTypeAvp != null ? reAuthTypeAvp.getStringValue() : "N/A";
        LogManager.getLogger().info("ServerGxRARHandler", "Re-Auth-Request-Type: " + reAuthType);

        // Perform re-authorization logic here
        // For example, update subscriber policies, QoS, or terminate session

        // Build RAA (Re-Auth-Answer)
        DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest);

        // Add AVPs to indicate success
        IDiameterAVP resultCodeAvp = DiameterDictionary.getInstance().getAttribute("0:268"); // Result-Code
        resultCodeAvp.setInteger(2001); // DIAMETER_SUCCESS
        diameterAnswer.addAvp(resultCodeAvp);

        try {
            stackContext.getPeerCommunicator(diameterRequest.getRequestingHost())
                        .sendAnswer(diameterRequest, diameterAnswer);
            LogManager.getLogger().info("ServerGxRARHandler", "RAA sent successfully for session: " + sessionId);
        } catch (CommunicationException e) {
            LogManager.getLogger().error("ServerGxRARHandler", "CommunicationException while sending RAA: ", e);
        }
        } catch (RuntimeException __ex) {
            METHOD_LOG.error("!! EXCEPTION in processApplicationRequest after {}ms", System.currentTimeMillis() - __mStart, __ex);
            throw __ex;
        } finally {
            if (METHOD_LOG.isDebugEnabled()) {
                METHOD_LOG.debug("<< EXIT processApplicationRequest tookMs={}", System.currentTimeMillis() - __mStart);
            }
        }
    }

    @Override
    protected SessionReleaseIndiactor createSessionReleaseIndicator(ApplicationEnum applicationEnum) {
        return null;
    }
}
