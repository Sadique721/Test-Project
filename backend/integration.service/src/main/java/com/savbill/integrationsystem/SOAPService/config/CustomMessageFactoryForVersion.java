package com.savbill.integrationsystem.SOAPService.config;

import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;


@Configuration
public class CustomMessageFactoryForVersion {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CustomMessageFactoryForVersion.class);

    // SOAP Namespace Constants
    private static final String SOAP_11_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";
    private static final String SOAP_12_NAMESPACE = "http://www.w3.org/2003/05/soap-envelope";

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServletAcctApi(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        ServletRegistrationBean<MessageDispatcherServlet> servletRegistrationBean = new ServletRegistrationBean<>(servlet);
        servletRegistrationBean.addUrlMappings("/new-api/QodServices/*", "/NPM_API_11.1.1.5/services/ServiceIfcPort/*");

        return servletRegistrationBean;    }

    @Bean
    public SaajSoapMessageFactory messageFactory() throws SOAPException {
        // Create message factories for both SOAP versions
        final MessageFactory messageFactorySoap11 = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        final MessageFactory messageFactorySoap12 = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

        // Custom MessageFactory for version detection
        MessageFactory messageFactoryWrapper = new MessageFactory() {
            @Override
            public SOAPMessage createMessage() throws SOAPException {
                return messageFactorySoap11.createMessage();
            }

            @Override
            public SOAPMessage createMessage(MimeHeaders headers, InputStream in) throws IOException, SOAPException {
                // Ensure we can read the stream multiple times
                byte[] messageBytes = IOUtils.toByteArray(in);

                // Detect SOAP version
                Optional<MessageFactory> selectedFactory = detectSOAPVersion(new ByteArrayInputStream(messageBytes), headers);

                // Use selected factory or default to SOAP 1.1
                MessageFactory factory = selectedFactory.orElse(messageFactorySoap11);

                return factory.createMessage(headers, new ByteArrayInputStream(messageBytes));
            }

            private Optional<MessageFactory> detectSOAPVersion(InputStream in, MimeHeaders headers) {
                try {
                    // Convert input stream to string for analysis
                    String xmlContent = IOUtils.toString(in, String.valueOf(StandardCharsets.UTF_8));

                    // Log the XML content for debugging
                    LOGGER.debug("\n  (:Received Request XML Content:) \n" + xmlContent);

                    // Check for SOAP 1.2 namespace
                    if (xmlContent.contains(SOAP_12_NAMESPACE)) {
                        LOGGER.info("Detected SOAP 1.2 Message");
                        headers.setHeader("Content-Type", "application/soap+xml");
                        return Optional.of(messageFactorySoap12);
                    }

                    // Check for SOAP 1.1 namespace
                    if (xmlContent.contains(SOAP_11_NAMESPACE)) {
                        LOGGER.info("Detected SOAP 1.1 Message");
                        return Optional.of(messageFactorySoap11);
                    }

                    // If no specific namespace found, log a warning
                    LOGGER.warn("Unable to determine SOAP version. Defaulting to SOAP 1.1");
                    return Optional.empty();
                } catch (IOException e) {
                    LOGGER.error("Error reading SOAP message", e);
                    return Optional.empty();
                }
            }
        };

        return new SaajSoapMessageFactory(messageFactoryWrapper);
    }
}
