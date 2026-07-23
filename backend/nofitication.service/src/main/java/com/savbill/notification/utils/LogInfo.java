package com.savbill.notification.utils;

import com.savbill.notification.spring.JaversConfiguration;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;

@Data
public class LogInfo {

    private String ipAddress;
    private String requestFrom;
    private String userName;
    private String type;
    private String logMessage;

    public LogInfo() {
    }

    public LogInfo(String ipAddress, String requestFrom, String userName, String type, String logMessage) {
        this.ipAddress = ipAddress;
        this.requestFrom = requestFrom;
        this.userName = userName;
        this.type = type;
        this.logMessage = logMessage;
    }

    public static LogInfo extractLogInfo(HttpServletRequest request, String type, String userName) {
        String ipAddress = JaversConfiguration.getIpAddressFromHeader(request);
        String requestFrom = request.getHeader(LogConstants.LogConstant.REQUEST_FROM);
        String logMessage = LogConstants.LogConstant.LOG_MESSAGE;
        return new LogInfo(ipAddress, requestFrom, userName, type, logMessage);
    }
}
