package com.diameter.service;

import java.util.concurrent.BlockingQueue;

import com.diameter.model.DiameterAudit;

public interface AsyncDiameterAuditService {
	
	void saveAudit(DiameterAudit audit);
	
	boolean publish(DiameterAudit audit);
	
	BlockingQueue<DiameterAudit> getQueue();
	
}
