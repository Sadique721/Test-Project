package com.diameter.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.diameter.config.AuditProperties;
import com.diameter.model.DiameterAudit;
import com.diameter.service.AsyncDiameterAuditService;

@Component
public class CsvAuditWorker {
	private static final Logger log = LoggerFactory.getLogger(CsvAuditWorker.class);
	
	private final AsyncDiameterAuditService queueService;
	private final AuditProperties properties;
	private final CsvFormatter formatter = new CsvFormatter();
	private final CsvFileRotator fileRotator;
	private volatile boolean running = true;
	private Thread workerThread;

	public CsvAuditWorker(AsyncDiameterAuditService queueService, AuditProperties properties) {
		this.queueService = queueService;
		this.properties = properties;
		this.fileRotator = new CsvFileRotator(properties);
	}

	@PostConstruct
	public void start() {
		workerThread = new Thread(this::processQueue);
		workerThread.setName("csv-audit-worker");
		workerThread.setDaemon(true);
		workerThread.start();
		log.info("CSV Audit Worker Started");
	}

	private void processQueue() {
		BlockingQueue<DiameterAudit> queue = queueService.getQueue();
		List<DiameterAudit> batch = new ArrayList<>(properties.getBatchSize());
		long lastFlushTime = System.currentTimeMillis();
		while (running || !queue.isEmpty()) {
			try {
				DiameterAudit audit = queue.poll(5, TimeUnit.SECONDS);
				if (audit != null) {
					batch.add(audit);
				}
				queue.drainTo(batch, properties.getBatchSize() - batch.size());
				boolean flush = batch.size() >= properties.getBatchSize();
				boolean timeout = System.currentTimeMillis() - lastFlushTime >= properties.getFlushIntervalMs();
				if (!batch.isEmpty() && (flush || timeout)) {
					writeBatch(batch);
					batch.clear();
					lastFlushTime = System.currentTimeMillis();
				}
			} catch (Exception ex) {
				log.error("CSV Worker Error", ex);
			}
		}
		if (!batch.isEmpty()) {
			try {
				writeBatch(batch);
			} catch (Exception e) {
				log.error("Final Batch Write Failed", e);
			}
		}
		try {
			fileRotator.close();
		} catch (IOException e) {
			log.error("Unable to close CSV Writer", e);
		}
		log.info("CSV Audit Worker Stopped");
	}

	private void writeBatch(List<DiameterAudit> batch) throws IOException {
		BufferedWriter writer = fileRotator.getWriter();
		for (DiameterAudit audit : batch) {
			writer.write(formatter.format(audit));
		}
		writer.flush();
	}

	@PreDestroy
	public void shutdown() {
		log.info("Stopping CSV Worker...");
		running = false;
		if (workerThread != null) {
			workerThread.interrupt();
			try {
				workerThread.join(10000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
