package com.savbill.notification.config;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class CustomHttpServletRequestFilter extends HttpServletRequestWrapper {
    private Map<String, String> customHeaderMap = null;

    public CustomHttpServletRequestFilter(HttpServletRequest req) {
	super(req);
	customHeaderMap = new HashMap<String, String>();
	// TODO Auto-generated constructor stub
    }

    public void addHeader(String name, String value) {
	customHeaderMap.put(name, value);
    }
    
    public String getHeader(String name) {
        // check the custom headers first
        String headerValue = (String) customHeaderMap.get(name);

        if (headerValue != null){
            return headerValue;
        }
        // else return from into the original wrapped object
        return ((HttpServletRequest) getRequest()).getHeader(name);
    }
}
