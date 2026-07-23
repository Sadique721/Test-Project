package com.diameter.serviceImpl;

import java.util.LinkedHashMap;
import java.util.Map;
import com.diameter.commons.MasterType;
import org.springframework.stereotype.Service;
import com.diameter.service.MasterService;

@Service
public class MasterServiceImpl implements MasterService {

    @Override
    public Map<String, String> getMasterData(String type) {

        MasterType masterType;
        try {
            masterType = MasterType.valueOf(type);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid master type: " + type);
        }

        Map<String, String> response = new LinkedHashMap<>();

        switch (masterType) {

            case TYPE:
                response.put("Grouped", "Grouped");
                response.put("String", "String");
                response.put("Boolean", "Boolean");
                response.put("Float", "Float");
                response.put("Integer", "Integer");
                response.put("Unsigned32", "Unsigned32");
                response.put("OctetString", "OctetString");
                break;

            case DICTIONARY_TYPE:
                response.put("RADIUS", "Radius");
                response.put("DIAMETER", "Diameter");
                break;

            case VERIFICATION_MODE:
                response.put("VERIFY_PEER", "Verify Peer");
                response.put("NO_VERIFY", "No Verify");
                break;

            case CERTIFICATE_TYPE:
                response.put("X.509", "X.509");
                response.put("PEM", "PEM");
                break;
                
            case REQUEST_TYPE:
                response.put("Credit-Control-Request","Credit-Control-Request");
                response.put("Authentication-Authorization-Request","Authentication-Authorization-Request");
                response.put("Session-Termination-Request","Session-Termination-Request");
                response.put("Re-Auth-Request", "Re-Auth-Request");
                response.put("Capabilities-Exchange-Request", "Capabilities-Exchange-Request");
                break;
                
            case RESPONSE_TYPE:
                response.put("Credit-Control-Answer","Credit-Control-Answer");
                response.put("Authentication-Authorization-Answer","Authentication-Authorization-Answer");
                response.put("Session-Termination-Answer","Session-Termination-Answer");
                response.put("Re-Auth-Answer", "Re-Auth-Answer");
                response.put("Capabilities-Exchange-Answer", "Capabilities-Exchange-Answer");
                break;
                
            case APPLICATION:
                response.put("GX","GX");
                response.put("GY", "GY");
                response.put("RX", "RX");
                response.put("RO", "RO");
                break;  
                
            case CC_REQUEST_TYPE:
                response.put("INITIAL_REQUEST","INITIAL_REQUEST");
                response.put("UPDATE_REQUEST", "UPDATE_REQUEST");
                response.put("TERMINATION_REQUEST", "TERMINATION_REQUEST");
                response.put("EVENT_REQUEST", "EVENT_REQUEST");
                break;  

            default:
                throw new IllegalArgumentException("Unsupported master type: " + type);
        }

        return response;
    }
}