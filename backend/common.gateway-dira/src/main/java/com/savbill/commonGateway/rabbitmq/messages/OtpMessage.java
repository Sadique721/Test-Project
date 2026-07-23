package com.savbill.commonGateway.rabbitmq.messages;

import com.savbill.commonGateway.moules.OTP.domain.OTP;
import com.savbill.commonGateway.rabbitmq.RabbitMqConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpMessage {

    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String traceId;
    private String spanId;
    private String currentUser;
    private String emailTemplate;
    private String smsTemplate;
    private String appendUrl;
    private Map<String,Object> otpData;
    private boolean isSmsConfigured;
    private boolean isEmailConfigured;
    private String timeframe;
    private String datetime;

    public OtpMessage(OTP otp, String message, String sourceName, String emailId, Integer mvnoId, String timeframe, String datetime , Long buId,String authEventName)
    {
        Map<String,Object> map = new HashMap<>();
        map.put(RabbitMqConstants.OTP,otp.getOtp());
        map.put(RabbitMqConstants.MOBILE_NUMBER, otp.getMobile_email());
        map.put(RabbitMqConstants.COUNTRY_CODE, otp.getCountryCode());
        map.put(RabbitMqConstants.EMAIL_ID, emailId);
        map.put(RabbitMqConstants.MVNO_ID,mvnoId);
        if(Objects.nonNull(buId)) {
            map.put(RabbitMqConstants.BU_ID, buId);
        }
        if(Objects.isNull(buId)){
            map.put(RabbitMqConstants.BU_ID, null);
        }
        this.setMessageDate(new Date());
        this.setMessageId(UUID.randomUUID().toString());
        this.setMessage(message);

        this.setOtpData(map);
        this.sourceName = sourceName;
        this.currentUser = (String) MDC.get(RabbitMqConstants.USER_NAME);
        if(authEventName.equalsIgnoreCase("OTP BY EMAIL")){
            this.isEmailConfigured=true;
            this.isSmsConfigured=false;
        }else if(authEventName.equalsIgnoreCase("OTP BY SMS")){
            this.isEmailConfigured=false;
            this.isSmsConfigured=true;
        }

        this.timeframe = timeframe;
        this.datetime = datetime;
    }
}
