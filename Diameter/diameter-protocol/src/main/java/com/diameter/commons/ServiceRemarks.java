package com.diameter.commons;

public enum ServiceRemarks {
  INVALID_LICENSE("Invalid License"),
  DRIVER_NOT_CONFIGURED("Driver not configured"),
  DRIVER_INITIALIZATION_FAILED("Driver Initialization Failed"),
  INVALID_CONFIGURATION("Invalid Configuration"),
  DATASOURCE_NOT_CONFIGURED("Datasource not configured"),
  PROBLEM_WITH_DATASOURCE_CONFIGURATION("Problem with Datasource Configuration"),
  PROBLEM_BINDING_IP_PORT("Problem with Binding IP/Port"),
  STARTED_ON_UNIVERSAL_IP("Service Started on Universal IP"),
  MISSING_JAR_FILE("Missing jar File"),
  HTTPS_FAILURE("HTTPS Failure"),
  UNKNOWN_PROBLEM("Unknown problem"),
  SERVER_NAME_OR_DOMAIN_NAME_NOT_CONFIGURED("Server name or Domain name not configured"),
  DIAMETER_STACK_ERROR("Diameter Stack Error");
  
  public final String remark;
  
  ServiceRemarks(String remark) {
    this.remark = remark;
  }
}
