package com.savbill.taskmanagement.core.modules.EmailConfig.service;


import com.savbill.taskmanagement.core.modules.EmailConfig.domain.EmailConfigBSS;
import com.savbill.taskmanagement.core.modules.EmailConfig.repository.EmailConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EmailConfigService {

      @Autowired
      private EmailConfigRepository emailConfigRepository;

      public void getEmailconfigFromMessage(Map<String , Object> email){
            EmailConfigBSS emailConfigBSS =  new EmailConfigBSS();
            if(email.get("buid") != null) {
                  if (!isNewConfig(Long.parseLong(email.get("mvnoid").toString()), Long.parseLong(email.get("buid").toString()))) {
                         emailConfigBSS = emailConfigRepository.findAllByMvnoIdAndBuId(Long.parseLong(email.get("mvnoid").toString()),Long.parseLong(email.get("buid").toString())).get(0);
                  }
            }
            else{
                  if (!isNewConfig(Long.parseLong(email.get("mvnoid").toString()), null)) {
                        emailConfigBSS = emailConfigRepository.findAllByMvnoIdAndBuId(Long.parseLong(email.get("mvnoid").toString()),null).get(0);
                  }
            }
            emailConfigBSS.setUserName((String) email.get("username"));
            emailConfigBSS.setPassword((String) email.get("password"));
            emailConfigBSS.setPort((String) email.get("port"));
            emailConfigBSS.setHostServer((String)email.get("hostserver"));
            emailConfigBSS.setAuthType((String)email.get("authtype"));
            if(email.get("smtpauth").equals("1")) {
                  emailConfigBSS.setSmtpAuth(true);
            }
            else{
                  emailConfigBSS.setSmtpAuth(false);
            }
            if(email.get("buid") != null) {
                  emailConfigBSS.setBuId(Long.parseLong(email.get("buid").toString()));
            }
            emailConfigBSS.setMvnoId(Long.parseLong(email.get("mvnoid").toString()));
            emailConfigRepository.save(emailConfigBSS);

      }

      public boolean isNewConfig(Long mvnoId , Long buId){
            boolean isNewConfig = true;
            List<EmailConfigBSS> emailConfigBSSList = emailConfigRepository.findAllByMvnoIdAndBuId(mvnoId,buId);
            if(!emailConfigBSSList.isEmpty()) {
                  isNewConfig = false;
            }
            return  isNewConfig;
      }

      public EmailConfigBSS getEmailConfigFromMvnoIdAndBuId(Long mvnoId , Long buId){
            List<EmailConfigBSS> emailConfigBSS = emailConfigRepository.findAllByMvnoIdAndBuId(mvnoId,buId);
            if(!emailConfigBSS.isEmpty()){
                  return emailConfigBSS.get(0);
            }
            return  null;
      }
}
