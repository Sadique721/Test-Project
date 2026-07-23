package com.savbill.integrationsystem.MtnUssd;

import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MtnUssdService {

    @Autowired
    private CMSClient cmsClient;

    @Value(value = "${staff.username}")
    private String username;

    @Value(value = "${staff.password}")
    private String password;

    private final Logger log = LoggerFactory.getLogger(MtnUssdService.class);

    /**
     * @Author Dhaval Khalasi
     * Check that necessary details should be there ignore others
     * **/

    public void validateMtnUssdRequest(MtnUssdDTO mtnUssdDTO){
       if(Objects.isNull(mtnUssdDTO.getUssdString()) || mtnUssdDTO.getUssdString().isEmpty() || mtnUssdDTO.getUssdString().equalsIgnoreCase( " ")){
           throw new CustomValidationException(2000,"Ussdstring is Mandatory ",null);
       }
       if(Objects.isNull(mtnUssdDTO.getSessionId()) || mtnUssdDTO.getSessionId().isEmpty() || mtnUssdDTO.getSessionId().equalsIgnoreCase( " ")){
           throw new CustomValidationException(3000,"SessionId is Mandatory",null);
       }
       if(Objects.isNull(mtnUssdDTO.getMsisdn()) || mtnUssdDTO.getMsisdn().isEmpty() || mtnUssdDTO.getMsisdn().equalsIgnoreCase( " ")){
            throw new CustomValidationException(4000,"Msisdn is Mandatory ",null);
       }
    }

   /**
    * @Author Dhaval Khalasi
    * This Method is for separation with that which api called using mtnUssdDTO.ussdString
    * If string like *100*5 than plan fetch api called else plan buy api called
    * **/
    public MtnUssdResponseDTO processingMtnUssdRequest(MtnUssdDTO mtnUssdDTO , String service){
        MtnUssdResponseDTO mtnUssdResponse = new MtnUssdResponseDTO();
        log.info("Start processing mtn ussd request");
        log.info("Staff username for processing request : "+username);
        log.info("Staff password for processing request : "+password);
        if(isPlanListFetch(mtnUssdDTO)){
         log.info("Plan fetch api request initiated");
         MtnPlanFetchDTO mtnPlanFetchDTO = new MtnPlanFetchDTO();
         mtnPlanFetchDTO.setService(service);
         mtnPlanFetchDTO.setUsername(username);
         mtnPlanFetchDTO.setPassword(password);
         mtnPlanFetchDTO.setMobileNumber(mtnUssdDTO.getMsisdn());
         mtnPlanFetchDTO.setTransactionId(mtnUssdDTO.getSessionId());
         mtnUssdResponse=cmsClient.mtnPlanFetch(mtnPlanFetchDTO);
        }
        else{
            log.info("Plan buy api request initiated");
            MtnBuyPlanDTO mtnBuyPlanDTO = new MtnBuyPlanDTO();
            mtnBuyPlanDTO.setMobileNumber(mtnUssdDTO.getMsisdn());
            mtnBuyPlanDTO.setTransactionId(mtnUssdDTO.getSessionId());
            mtnBuyPlanDTO.setPlanId(Integer.parseInt(mtnUssdDTO.getUssdString()));
            mtnBuyPlanDTO.setUsername(username);
            mtnBuyPlanDTO.setPassword(password);
            mtnUssdResponse=cmsClient.mtnBuyPlan(mtnBuyPlanDTO);
        }
        return mtnUssdResponse;
    }

    /**
     * @Author Dhaval Khalasi
     * This Method is for just check if using regex.If *100 return true else return false
     * **/
    public Boolean isPlanListFetch(MtnUssdDTO mtnUssdDTO) {
        String regex = "\\*\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(mtnUssdDTO.getUssdString());
        return matcher.matches();
    }
}
