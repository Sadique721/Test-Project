package com.savbill.integrationsystem.soapAudit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Node;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SoapAuditService {

	@Autowired
	public SoapAuditRepository interceptorRepository;
	private static final Logger LOGGER = LoggerFactory.getLogger(SoapAuditService.class);

	public void add(SoapAudit log) {
		interceptorRepository.saveAndFlush(log);
	}
	public String getFullRequest(SOAPMessage soapMessage) throws Exception {
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			soapMessage.writeTo(byteArrayOutputStream);
			return byteArrayOutputStream.toString(StandardCharsets.UTF_8.name());
		}catch (Exception e) {
			LOGGER.error("Error converting SOAP message to string", e);
			throw new RuntimeException("Failed to process SOAP message", e);
		}
	}

	public String getResponseCode(String soapEnvelope) throws Exception {
		if (soapEnvelope == null || soapEnvelope.isEmpty()) {
			LOGGER.warn("SOAP envelope is null or empty. Returning default response code 500.");
			return "500";
		}
		Pattern patternResponseCode = Pattern.compile("<(?:\\w+:)?responseCode>(\\d+)</(?:\\w+:)?responseCode>");
		Pattern patternResponeCode = Pattern.compile("<(?:\\w+:)?responeCode>(\\d+)</(?:\\w+:)?responeCode>");
		Pattern patternFaultCode = Pattern.compile("<(?:\\w+:)?faultcode>");
		Matcher matcherFaultCode = patternFaultCode.matcher(soapEnvelope);
		if (matcherFaultCode.find()) {
			return "500";
		}
		Matcher matcherResponseCode = patternResponseCode.matcher(soapEnvelope);
		if (matcherResponseCode.find()) {
			return matcherResponseCode.group(1); // Return the extracted value
		}
		Matcher matcherResponeCode = patternResponeCode.matcher(soapEnvelope);
		if (matcherResponeCode.find()) {
			return matcherResponeCode.group(1); // Return the extracted value
		}
		return "200";
	}

	public void saveAudit(MessageContext messageContext, String requestForwardedIp) {
		try {
			SaajSoapMessage requestMessage = (SaajSoapMessage) messageContext.getRequest();
			SOAPMessage requestMessageSoapMessage = requestMessage.getSaajMessage();

			SaajSoapMessage responseMessage = (SaajSoapMessage) messageContext.getResponse();
			SOAPMessage responseMessageSoapMessage = responseMessage.getSaajMessage();

			SOAPEnvelope envelope = requestMessageSoapMessage.getSOAPPart().getEnvelope();

			String requestBody = getFullRequest(requestMessageSoapMessage);
			String responseBody = getFullRequest(responseMessageSoapMessage);
			//Extract Values
			String userName = extractValue(requestBody, "userName");
			String ipAddress = extractValue(requestBody, "ipAddress");
			String subscriberId = extractValue(requestBody, "subscriberId");
			String actionItem = extractValue(requestBody, "actionItem");

			SoapAudit log = new SoapAudit();
			log.setRequestIpAddress(requestForwardedIp);
			log.setRequestBody(requestBody);
			log.setRequestIpAddress(requestForwardedIp);
			SOAPBody body = envelope.getBody();

			body.normalize();

			String eventName = null;
			Node firstElement = body.getFirstChild();
			while (firstElement != null && firstElement.getNodeType() != Node.ELEMENT_NODE) {
				firstElement = firstElement.getNextSibling(); // Skip text/whitespace nodes
			}

			if (firstElement != null) {
				eventName = firstElement.getLocalName();
			}

			log.setEventName(eventName);
			log.setResponseBody(responseBody);
			String parameter = userName != null ? userName : (ipAddress != null ? ipAddress : subscriberId);
			log.setParameter(parameter);
			log.setActionitem((actionItem != null && !actionItem.isEmpty()) ? actionItem : null);
			String responseCode = getResponseCode(responseBody);
			if (responseCode != null && !responseCode.isEmpty()){
				log.setStatus(Integer.parseInt(responseCode));
			}
			log.setActionTime(LocalDateTime.now());
			interceptorRepository.saveAndFlush(log);

			LOGGER.info("Audit log saved: {}", log);
		} catch (Exception e) {
			LOGGER.error("Error occurred while saving audit log", e);
		}
	}

	public static String extractValue(String requestBody, String tagName) {
		Matcher matcher = Pattern.compile("<" + tagName + "[^>]*>(.*?)</" + tagName + ">", Pattern.CASE_INSENSITIVE)
				.matcher(requestBody);
		return matcher.find() ? matcher.group(1) : null;
	}
}
