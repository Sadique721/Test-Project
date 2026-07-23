package com.savbill.ticketmanagement.core.modules.MailConfigration.config;


import com.savbill.ticketmanagement.core.modules.ClientServ.repository.ClientServiceRepository;
import com.savbill.ticketmanagement.core.modules.Mail.domain.ReceiveEmailConfiguration;
import com.savbill.ticketmanagement.core.modules.Mail.service.ReceiveEmailConfigurationService;
import com.savbill.ticketmanagement.core.modules.MailConfigration.service.ReceiveMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
//import org.springframework.integration.mail.ImapMailReceiver;
//import org.springframework.integration.mail.MailReceiver;
//import org.springframework.integration.mail.MailReceivingMessageSource;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.MailReceiver;
import org.springframework.integration.mail.MailReceivingMessageSource;
import org.springframework.messaging.Message;

import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Configuration
@EnableIntegration
public class MailReceiverConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MailReceiverConfiguration.class);

    private final ReceiveMailService receiveMailService;



    @Autowired
    ClientServiceRepository clientServiceRepository;

    @Autowired
    ReceiveEmailConfigurationService receiveEmailConfigurationService;

    public MailReceiverConfiguration(ReceiveMailService receiveMailService) {
        this.receiveMailService = receiveMailService;
    }

    @ServiceActivator(inputChannel = "receiveEmailChannel")
    public void receive(Message<?> message) {
        receiveMailService.handleReceivedMail((MimeMessage) message.getPayload());
    }

    @Bean("receiveEmailChannel")
    public DirectChannel defaultChannel() {
        DirectChannel directChannel = new DirectChannel();
        directChannel.setDatatypes(MimeMessage.class);
        return directChannel;
    }

    @Bean
    @InboundChannelAdapter(
            channel = "receiveEmailChannel",
            poller = @Poller(fixedDelay = "5000", taskExecutor = "asyncTaskExecutor")
    )
    @ConditionalOnProperty(prefix = "enable", name = "mailConfig1") // Change property name for each email account
    public MailReceivingMessageSource mailMessageSource1(@Qualifier("imapMailReceiver1") MailReceiver mailReceiver) {
        MailReceivingMessageSource mailReceivingMessageSource = new MailReceivingMessageSource(mailReceiver);
        return mailReceivingMessageSource;
    }

    @Bean
    @InboundChannelAdapter(
            channel = "receiveEmailChannel",
            poller = @Poller(fixedDelay = "5000", taskExecutor = "asyncTaskExecutor")
    )
    @ConditionalOnProperty(prefix = "enable", name = "mailConfig2") // Change property name for each email account
    public MailReceivingMessageSource mailMessageSource2(@Qualifier("imapMailReceiver2") MailReceiver mailReceiver) {
        MailReceivingMessageSource mailReceivingMessageSource = new MailReceivingMessageSource(mailReceiver);
        return mailReceivingMessageSource;
    }

    private MailReceivingMessageSource createMailReceivingMessageSource(MailReceiver mailReceiver, ReceiveEmailConfiguration receiveEmailConfiguration) {
        MailReceivingMessageSource mailReceivingMessageSource = new MailReceivingMessageSource(mailReceiver);
        mailReceivingMessageSource.setBeanName(receiveEmailConfiguration.getName());
        return mailReceivingMessageSource;
    }

    @Bean
    public MailReceiver imapMailReceiver1(@Value("imaps://${mail.imap1.username}:${mail.imap1.password}@${mail.imap1.host}:${mail.imap1.port}/INBOX") String storeUrl) {
        return configureMailReceiver(storeUrl);
    }

    @Bean
    public MailReceiver imapMailReceiver2(@Value("imaps://${mail.imap2.username}:${mail.imap2.password}@${mail.imap2.host}:${mail.imap2.port}/INBOX") String storeUrl) {
        return configureMailReceiver(storeUrl);
    }



    public MailReceiver configureMailReceiver( String storeUrl) {
        log.info("IMAP connection url: {}", storeUrl);
        System.out.println("^@^@^@^@^"+storeUrl+"^@^@^@^@");
        ImapMailReceiver imapMailReceiver = new ImapMailReceiver(storeUrl);
        imapMailReceiver.setShouldMarkMessagesAsRead(true);
        imapMailReceiver.setShouldDeleteMessages(false);
        imapMailReceiver.setMaxFetchSize(100);
        imapMailReceiver.setAutoCloseFolder(false);
        imapMailReceiver.setCancelIdleInterval(120);

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        javaMailProperties.put("mail.imap.socketFactory.fallback", false);
        javaMailProperties.put("mail.store.protocol", "imaps");
//        javaMailProperties.put("mail.debug", "true");
        javaMailProperties.put("mail.debug", "false");
        javaMailProperties.put("mail.imaps.fetchsize", "1948576");
        //javaMailProperties.put("mail.polling.interval", "60000"); // Polling interval: 1 minute
        javaMailProperties.put("mail.reconnect.interval", "300000"); // Reconnect interval: 5 minutes
        javaMailProperties.put("mail.max.reconnect.attempts", "3"); // Maximum reconnect attempts: 3
        javaMailProperties.put("mail.reconnect.backoff.time", "30000"); // Reconnect backoff time: 30 seconds

        imapMailReceiver.setJavaMailProperties(javaMailProperties);

        return imapMailReceiver;
    }
}
