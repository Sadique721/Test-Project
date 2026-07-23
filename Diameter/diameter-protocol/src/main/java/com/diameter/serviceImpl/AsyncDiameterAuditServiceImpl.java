package com.diameter.serviceImpl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.diameter.config.AuditProperties;
import com.diameter.config.AuditRoutingProperties;
import com.diameter.model.AuditRoute;
import com.diameter.model.DiameterAudit;
import com.diameter.service.AsyncDiameterAuditService;
import com.diameter.service.DiameterAuditService;

@Service
public class AsyncDiameterAuditServiceImpl implements AsyncDiameterAuditService{
	
	private static final Logger logger = LoggerFactory.getLogger(AsyncDiameterAuditServiceImpl.class);
	
	@Autowired
    private DiameterAuditService diameterAuditService;
	
	@Autowired
	private AuditProperties properties;
	
	@Autowired
	private AuditRoutingProperties routingProperties;
	
	private BlockingQueue<DiameterAudit> queue;
	
	@PostConstruct
    public void init() {
        this.queue = new LinkedBlockingQueue<>(properties.getQueueSize());
    }

    @Async("auditExecutor")
    public void saveAudit(DiameterAudit audit) {
    	boolean bPersistInDb= true;
    	
    	AuditRoute route = routingProperties.getRouting().get(audit.getRequestType()); 
    	
    	if (route == null || route.isDb()) { 
    		bPersistInDb= true;
    	}else {
    		bPersistInDb= false;
    	}
    	
    	// Publish audit for background CSV writing 
    	if(!bPersistInDb) {
    		boolean accepted = publish(audit);
        	if (!accepted) { 
        		// Queue is full 
        		bPersistInDb = true;
        		logger.warn("Audit queue is full. Dropping audit for TransactionId={}", audit.getTransactionId());
        	}
    	}
    	
    	if(bPersistInDb) {
	        try {
	            diameterAuditService.createAudit(audit);
	        } catch(Exception e) {
	        	logger.error("Async audit insert failed session={}",audit.getSessionId(),e);
	        }
    	}
    }
    
    public boolean publish(DiameterAudit audit) {
        return queue.offer(audit);

    }

    public BlockingQueue<DiameterAudit> getQueue() {
        return queue;

    }

    @PreDestroy
    public void shutdown() {
    	logger.warn("Audit Queue Shutdown.");
    }

}
