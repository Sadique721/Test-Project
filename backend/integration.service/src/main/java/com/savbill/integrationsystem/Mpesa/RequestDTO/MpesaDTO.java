package com.savbill.integrationsystem.Mpesa.RequestDTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MpesaDTO {
    private String requestUrl;
    private String resultUrl;
    private String queueTimeOutUrl;
    private String partyA;
    private String commandId;
    private String consumerKey;
    private String consumerSecret;
    private String scheduleTime;
    private String redirectTimeInSeconds;
    private String certificatePath;
    private String expressSimulateCallbackUrl;
    private String passKey;
    private String transactionType;
    private String trxCode;
    private String partyB;
    private String initiatorName;
    private String initiatorPassword;

    public MpesaDTO(String requestUrl, String resultUrl, String queueTimeOutUrl, String partyA, String commandId, String consumerKey, String consumerSecret,String scheduleTime,String redirectTimeInSeconds,String certificatePath,String expressSimulateCallbackUrl,String passKey,String transactionType,String trxCode,String partyB,String initiatorName,String initiatorPassword) {
        this.requestUrl = requestUrl;
        this.resultUrl = resultUrl;
        this.queueTimeOutUrl = queueTimeOutUrl;
        this.partyA = partyA;
        this.commandId = commandId;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.scheduleTime = scheduleTime;
        this.redirectTimeInSeconds = redirectTimeInSeconds;
        this.certificatePath = certificatePath;
        this.expressSimulateCallbackUrl = expressSimulateCallbackUrl;
        this.passKey = passKey;
        this.transactionType = transactionType;
        this.trxCode = trxCode;
        this.partyB = partyB;
        this.initiatorName=initiatorName;
        this.initiatorPassword=initiatorPassword;
    }
}
