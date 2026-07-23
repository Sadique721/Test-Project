package com.savbill.integrationsystem.SOAPService.subSessionIsLoggedOn;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SubsessionIdLoggedOnService {
    @Autowired
    private RadiusClient radiusClient;

    public Boolean checkUserSession(Map<String,Object> request, String ipAddress) throws Exception{
        if(request != null){
            String loginIpAddress = request.get("framedIpAddress").toString();
            if(loginIpAddress.equalsIgnoreCase(ipAddress)){
                return true;
            }
        }
        return false;
    }
}
