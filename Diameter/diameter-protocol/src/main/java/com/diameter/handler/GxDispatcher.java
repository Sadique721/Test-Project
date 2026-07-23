package com.diameter.handler;

import com.diameter.commons.Application;
import com.diameter.commons.ApplicationEnum;
import com.diameter.commons.ApplicationListener;
import com.diameter.commons.DiameterRequest;
import com.diameter.commons.IStackContext;
import com.diameter.commons.LogManager;
import com.diameter.commons.Session;
import com.diameter.commons.SessionReleaseIndiactor;
import com.diameter.serviceImpl.CustomerServiceImpl;
import com.diameter.serviceImpl.LocalCacheManagerServiceImpl;
import com.diameter.serviceImpl.MappingHeaderServiceImpl;
import com.diameter.serviceImpl.PlanServiceImpl;
import com.diameter.serviceImpl.QOSPolicyServiceImpl;
import com.diameter.util.GenericDiameterProcessor;

public class GxDispatcher extends ApplicationListener {

    private ServerGxCCRHandler ccrHandler;
    private ServerGxRARHandler rarHandler;

    public GxDispatcher(IStackContext ctx, ApplicationEnum[] apps) {
        super(ctx, apps);
        this.ccrHandler = new ServerGxCCRHandler(ctx, apps);
        this.rarHandler = new ServerGxRARHandler(ctx, apps);
    }

    @Override
    protected void processApplicationRequest(Session session, DiameterRequest req) {
        int commandCode = req.getCommandCode();

        if (commandCode == 272) { // Credit-Control
            ccrHandler.processApplicationRequest(session, req);
        } else if (commandCode == 258) { // Re-Auth
            rarHandler.processApplicationRequest(session, req);
        } else {
            LogManager.getLogger().warn("GxDispatcher", "Unknown Gx command: " + commandCode);
        }
    }
    
    public void setMappingHeaderServiceImpl(MappingHeaderServiceImpl mappingHeaderServiceImpl) {
		this.ccrHandler.setMappingHeaderServiceImpl(mappingHeaderServiceImpl);
	}
    
    public void setCustomerServiceImpl(CustomerServiceImpl customerServiceImpl) {
		this.ccrHandler.setCustomerServiceImpl(customerServiceImpl);
	}
    
    public void setPlanServiceImpl(PlanServiceImpl planServiceImpl) {
		this.ccrHandler.setPlanServiceImpl(planServiceImpl);
	}
    
    public void setQOSPolicyServiceImpl(QOSPolicyServiceImpl qosPolicyServiceImpl) {
		this.ccrHandler.setQOSPolicyServiceImpl(qosPolicyServiceImpl);
	}
    
    public void setCacheManagerServiceImpl(LocalCacheManagerServiceImpl cacheManagerServiceImpl) {
    	this.ccrHandler.setCacheManagerServiceImpl(cacheManagerServiceImpl);
	}
    
    public void setGenericDiameterProcessor(GenericDiameterProcessor genericDiameterProcessor) {
		this.ccrHandler.setGenericDiameterProcessor(genericDiameterProcessor);
	}

    @Override
    protected SessionReleaseIndiactor createSessionReleaseIndicator(ApplicationEnum app) {
        return null;
    }

	@Override
	public String getApplicationIdentifier() {
		return Application.TGPP_GX_29_212_18.name();
	}
}
