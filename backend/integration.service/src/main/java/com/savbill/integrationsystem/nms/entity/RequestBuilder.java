package com.savbill.integrationsystem.nms.entity;

import java.util.*;

public class RequestBuilder {
    private Map<String, Object> requestBody = new HashMap<>();


    public RequestBuilder setProperty(String property, String valueName, Object value) {
        Map<String, Object> propertyMap = (Map<String, Object>) requestBody.computeIfAbsent(property, k -> new HashMap<>());
        propertyMap.put(valueName, value);
        return this;
    }

    public RequestBuilder addEndPoint(Map<String, Object> endPoint) {
        List<Map<String, Object>> endPointList = (List<Map<String, Object>>) requestBody.computeIfAbsent("end-point", k -> new ArrayList<>());
        endPointList.add(endPoint);
        return this;
    }

    public RequestBuilder addDirection(String value) {
        List<Map<String, Object>> directionList = (List<Map<String, Object>>) requestBody.computeIfAbsent("direction", k -> new ArrayList<>());
        directionList.add(Collections.singletonMap("value", value));
        return this;
    }

    public Map<String, Object> build() {
        return requestBody;
    }
}
