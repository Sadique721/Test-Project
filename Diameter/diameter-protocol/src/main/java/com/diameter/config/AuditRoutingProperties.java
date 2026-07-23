package com.diameter.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.diameter.model.AuditRoute;

@Component
@ConfigurationProperties(prefix = "audit")
public class AuditRoutingProperties {
	private Map<String, AuditRoute> routing = new HashMap<>();

	public Map<String, AuditRoute> getRouting() {
		return routing;
	}

	public void setRouting(Map<String, AuditRoute> routing) {
		this.routing = routing;
	}
}
