package com.diameter.util;

import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diameter.model.PeerConfiguration;
import com.diameter.serviceImpl.PeerConfigurationServiceImpl;

public class DiameterValidator {
	
	private static final Logger logger = LoggerFactory.getLogger(PeerConfigurationServiceImpl.class);
	private static final Pattern IPV4_PATTERN = Pattern.compile(
    	    "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)"
    	    + "|"
    	    + "([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+" // Domain labels
    	    + "[A-Za-z]{2,}$"
    	);
 
	public static void validatePeer(PeerConfiguration config) {
		logger.debug("Validating peer configuration: {}", config.getNodeName());
        //validatePorts(config);
        validateIpAddresses(config.getIpAddresses());
        validateIpAddresses(List.of(config.getRemoteIpAddress()));
	}

	private static void validatePorts(PeerConfiguration config) {
		logger.debug("Validating peer configuration ports: {}", config.getTlsTcpListenPort() , config.getDtlsSctpListenPort());
		if ((config.getTlsTcpListenPort() > 0 || config.getDtlsSctpListenPort() > 0)
        	    && (config.getCertificateType() == null || config.getCertificateType().isBlank())) {
        	    throw new IllegalArgumentException("Certificate type is required when TLS/TCP or DTLS/SCTP ports are configured.");
        	}
		if ((config.getTlsTcpListenPort() > 0 || config.getDtlsSctpListenPort() > 0)
        	    && (config.getCertificateName() == null || config.getCertificateName().isBlank())) {
        	    throw new IllegalArgumentException("Certificate name is required when TLS/TCP or DTLS/SCTP ports are configured.");
        	}
	}

    public static void validateIpAddresses(List<String> ips) {
    	logger.debug("Validating IPs: {}", ips);
	    for (String ip : ips) {
	        if (!IPV4_PATTERN.matcher(ip).matches()) {
	            throw new IllegalArgumentException("Invalid IP address: " + ip);
	        }
	    }
	}
}
