package com.savbill.taskmanagement.core.modules.Schedulars;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.MailReceiver;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@EnableScheduling
public class MailReaderScheduler {

    final Logger log = LoggerFactory.getLogger(MailReaderScheduler.class);

    @Autowired
    @Lazy
    private List<MailReceiver> mailReceivers;


    @Autowired
    @Lazy
    private DirectChannel receiveEmailChannel;

    @Value("${enable.mailConfig1}")
    private String MAIL_CONFIG_1;

    @Value("${enable.mailConfig2}")
    private String MAIL_CONFIG_2;

    @Value("${mail.imap1.username}")
    private String MAIL_IMAP_1;
    @Value("${mail.imap2.username}")
    private String MAIL_IMAP_2;


    @Scheduled(fixedRate = 600000) // Check every 10 minute
    public void checkAndRestartMailReceiver() {
        for (MailReceiver mailReceiver : mailReceivers) {
            if (isMailReciverEnabled(mailReceiver)) {
                restartMailReceiver(mailReceiver);
            }
        }
    }


    private boolean isMailReceiverActive(MailReceiver mailReceiver) {
        // Implement a check to see if the mail receiver is active
        // This could involve checking a health endpoint, connection status, etc.
        // Example (simplified):
        try {
            mailReceiver.receive();
            log.info("mail server is alive no need to restarted");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void restartMailReceiver(MailReceiver mailReceiver) {
        try {
            ((ImapMailReceiver) mailReceiver).destroy();
            ((ImapMailReceiver) mailReceiver).afterPropertiesSet();
            log.info("Mail receiver restarted successfully.");
        } catch (Exception e) {
            log.error("Failed to restart mail receiver", e);
        }
    }

    private Boolean isMailReciverEnabled(MailReceiver mailReceiver){
        Boolean flag = false;
        if (mailReceiver instanceof ImapMailReceiver) {
            ImapMailReceiver imapMailReceiver = (ImapMailReceiver) mailReceiver;
            if (imapMailReceiver.toString().contains(MAIL_IMAP_1) && Objects.equals(MAIL_CONFIG_1, "true")) {
                return true;
            }
            if (imapMailReceiver.toString().contains(MAIL_IMAP_2) && Objects.equals(MAIL_CONFIG_2, "true")) {
                return true;
            }
        }

        return  flag;
    }
}
