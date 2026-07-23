package com.savbill.integrationsystem.PaymentIntegration.DTO;

import com.savbill.integrationsystem.AirtelAppToCRM.constant.AirtelValidateConstant;
import com.savbill.integrationsystem.core.CommonConstant;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = AirtelValidateConstant.COMMAND)
public class TigoPesaResponseDTO {

    private String type;
    private String transId;
    /**
     * refId : OrderId generated in tbltpayment table
     * */
    private Long refId;
    private String result;
    private String errorCode;
    private String errorDesc;
    private String msisdn;
    private String flag;
    private String content;

    @XmlElement(name = CommonConstant.TIGOPESA_CONSTANTS.TYPE)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement(name = CommonConstant.TIGOPESA_CONSTANTS.TXNID)
    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    @XmlElement(name = CommonConstant.TIGOPESA_CONSTANTS.REF_ID)
    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    @XmlElement(name = CommonConstant.TIGOPESA_CONSTANTS.RESULT)
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @XmlElement(name = CommonConstant.TIGOPESA_CONSTANTS.ERROR_CODE)
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @XmlElement(name = CommonConstant.TIGOPESA_CONSTANTS.ERROR_DESC)
    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    @XmlElement(name = CommonConstant.TIGOPESA_CONSTANTS.MSISDN)
    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @XmlElement(name = CommonConstant.TIGOPESA_CONSTANTS.FLAG)
    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    @XmlElement(name = CommonConstant.TIGOPESA_CONSTANTS.CONTENT)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
