package com.savbill.radius.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;

@Service
public class RadiusCacheServiceImpl {

    @Autowired
    private EurekaClient eurekaClient;

    public List<InstanceInfo> getRadiusInstances() {
	 List<InstanceInfo> instances = new ArrayList<InstanceInfo>();
	List<Application> applications = eurekaClient.getApplications().getRegisteredApplications();
	for (Application application : applications) {
	    List<InstanceInfo> applicationsInstances = application.getInstances();
	    for (InstanceInfo applicationsInstance : applicationsInstances) {
		instances.add(applicationsInstance);
	    }
	}
	return instances;
    }
}
